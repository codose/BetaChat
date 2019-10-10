package com.codose.betachat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codose.betachat.Models.GetTimeAgo;
import com.codose.betachat.Models.MessageAdapter;
import com.codose.betachat.Models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String chatUser,cUid;

    private DatabaseReference mUserData;

    private DatabaseReference mMessageData;
    private FirebaseAuth mAuth;
    private FirebaseUser current_user;

    private ImageButton add_btn, send_btn;

    private RecyclerView mMessageList;

    private EditText msg_edit;

    private Toolbar chat_bar;
    private TextView mTitleView,mLastseen, mStatus;
    private CircularImageView dp_img;

    private final List<Messages> messageList = new ArrayList<>();

    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chat_bar = findViewById(R.id.chat_bar);



        setSupportActionBar(chat_bar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);


        mUserData = FirebaseDatabase.getInstance().getReference();
        chatUser = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");
        //getSupportActionBar().setTitle(username);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = (View) inflater.inflate(R.layout.chat_app_bar, null);

        actionBar.setCustomView(action_bar_view );

        mAuth = FirebaseAuth.getInstance();

        current_user = mAuth.getCurrentUser();

        cUid = current_user.getUid();

        msg_edit = findViewById(R.id.message_edit);
        send_btn = findViewById(R.id.send_btn);
        add_btn = findViewById(R.id.add_btn);
        mMessageList = findViewById(R.id.message_list);

        mStatus = findViewById(R.id.user_status);


        mAdapter = new MessageAdapter(messageList);

        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);

        mUserData.child("Users").child(chatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Status = dataSnapshot.child("status").getValue().toString();

                mStatus.setText(Status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserData.child("Chat").child(cUid).child(chatUser).child("seen").setValue("true");

        Query un_read = mUserData.child("messages").child(chatUser).child(cUid).orderByChild("seen").equalTo("false");
        un_read.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    snapshot.getRef().child("seen").setValue("true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMessageList.setAdapter(mAdapter);

        loadMessages();



        //-----------custombar--------
        mTitleView = findViewById(R.id.chat_name);
        mLastseen = findViewById(R.id.last_seen);
        dp_img = findViewById(R.id.bar_img);

        dp_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prof = new Intent(getApplicationContext(),UserProfile.class);
                prof.putExtra("user_id",chatUser);
                startActivity(prof);
            }
        });

        mTitleView.setText(username);
        mUserData.keepSynced(true);

        //------------------------------Online/Last Seen Check-----------------------
        mUserData.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                final String t_img = dataSnapshot.child("t_img").getValue().toString();

                if(online.equals("true")){
                    mLastseen.setText("Online");
                }else{
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime =Long.parseLong(online);

                    String LastSeenTime = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                    mLastseen.setText(LastSeenTime);
                }
                Picasso.get().load(t_img).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.headshot).into(dp_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(t_img).placeholder(R.drawable.headshot).into(dp_img);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //----------------------------------Chats Database---------------------------
        mUserData.child("Chat").child(cUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(chatUser)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+cUid+"/"+chatUser,chatAddMap);
                    chatUserMap.put("Chat/"+chatUser+"/"+cUid,chatAddMap);

                    mUserData.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //--------------------Send button On click Listener----------
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



    }
//----------------Message Retrieving Method--------------------
    private void loadMessages() {
        mMessageData = FirebaseDatabase.getInstance().getReference();

        mMessageData.child("messages").child(cUid).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(messageList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.child(cUser.getUid()).child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.child(cUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    //--------------Message sending Method/Database-------------------
    private void sendMessage() {
        String message = msg_edit.getText().toString();
        if(! TextUtils.isEmpty(message)){

            String current_user_ref = "messages/"+cUid+"/"+chatUser;
            String chat_user_ref = "messages/"+chatUser+"/"+cUid;

            DatabaseReference user_msg_push = mUserData.child("messages")
                    .child(cUid).child(chatUser).push();
            String push_id =user_msg_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen", "false");
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", cUid);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            msg_edit.setText("");

            mUserData.child("Chat").child(cUid).child(chatUser).child("seen").setValue("true");

            mUserData.child("Chat").child(chatUser).child(cUid).child("seen").setValue("false");

            mUserData.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });

            Query un_read = mUserData.child("messages").child(chatUser).child(cUid).orderByChild("seen").equalTo("false");
            un_read.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        snapshot.getRef().child("seen").setValue("true");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // mUserData.child("messages").child(cUid).child(chatUser).child("seen").setValue("true");


        }
    }
}
