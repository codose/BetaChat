package com.codose.betachat;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codose.betachat.Models.Chat;
import com.codose.betachat.Models.Conversation;
import com.codose.betachat.Models.GetTimeAgo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment {


    private RecyclerView mChatList;

    private DatabaseReference mChatsData;

    private DatabaseReference UsersDatabase;

    private FirebaseAuth mAuth;

    private String current_uid;

    private View mainView;


    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_chats, container, false);

        // Inflate the layout for this fragment

        mChatList = mainView.findViewById(R.id.rv_chats);

        mAuth = FirebaseAuth.getInstance();
        current_uid = mAuth.getCurrentUser().getUid();

        mChatsData = FirebaseDatabase.getInstance().getReference().child("Chat").child(current_uid);
        mChatsData.keepSynced(true);
        UsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersDatabase.keepSynced(true);
        mChatList.setHasFixedSize(true);

        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mainView;

    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();


    }
    public void startListening(){
        Query query = mChatsData
                .orderByChild("timestamp");

        final FirebaseRecyclerOptions<Conversation> options =
                new FirebaseRecyclerOptions.Builder<Conversation>()
                        .setQuery(query, Conversation.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Conversation, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int i, @NonNull final Conversation conversation) {
                // Bind the Chat object to the ChatHolder
                final String user_id = getRef(i).getKey();

                UsersDatabase.child(user_id).child("online").keepSynced(false);


                UsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("username").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String t_img = dataSnapshot.child("t_img").getValue().toString();
                        String online = dataSnapshot.child("online").getValue().toString();
                        final DatabaseReference dbref =
                                FirebaseDatabase.getInstance().getReference();
                        Query lastchild =
                                dbref.child("messages")
                                        .child(current_uid)
                                        .child(user_id)
                                        .orderByKey().limitToLast(1);
                        lastchild.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String last_message = dataSnapshot.child("message").getValue().toString();

                                holder.setMessage(last_message, conversation.getSeen());
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

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }

                        if(online.equals("true")){
                            holder.setLastSeen("Online");
                        }else{

                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long lastTime =Long.parseLong(online);

                            String LastSeenTime = getTimeAgo.getTimeAgo(lastTime,getContext());
                            holder.setLastSeen(LastSeenTime);
                        }
                        Query un_read = dbref.child("messages").child(user_id).child(current_uid).orderByChild("seen").equalTo("false");
                        un_read.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int count = 0;
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    count++;
                                }

                                holder.setUnread(count);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.setName(username);
                        holder.setImage(t_img);
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chat = new Intent(getContext(),ChatActivity.class);
                                chat.putExtra("user_id",user_id)
                                        .putExtra("username",username);
                                startActivity(chat);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_menu, parent, false);

                return new ChatsViewHolder(view);
            }

        };
        mChatList.setAdapter(adapter);
        adapter.startListening();
    }

    public  static class ChatsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setUnread(int count){
            TextView uCount = mView.findViewById(R.id.un_read);
            if(count == 0){
                uCount.setVisibility(View.GONE);
            }else{
                String Count = Integer.toString(count);
                uCount.setVisibility(View.VISIBLE);
                uCount.setText(Count);
            }

        }
        public void setName(String name){
            TextView mUsername = (TextView) mView.findViewById(R.id.user_single);
            mUsername.setText(name);
        }

        public void setMessage(String message, String isSeen){
            TextView mStatus = (TextView) mView.findViewById(R.id.user_status);
            mStatus.setText(message);
            mStatus.setSingleLine(true);
            mStatus.setEllipsize(TextUtils.TruncateAt.END);
            if(isSeen.equals("false")){
                mStatus.setTypeface(mStatus.getTypeface(), Typeface.BOLD);
            }else{
                mStatus.setTypeface(mStatus.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setImage(final String t_img) {
            final CircularImageView dp =  mView.findViewById(R.id.dp);
            Picasso.get().load(t_img).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.headshot).into(dp, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load(t_img).placeholder(R.drawable.headshot).into(dp);

                }
            });
        }
        public void setDate(final String date){

        }
        public void setLastSeen(final String lastSeen){
            TextView LastSeen = (TextView) mView.findViewById(R.id.last_seen);
            LastSeen.setVisibility(View.VISIBLE);
            LastSeen.setText(lastSeen);
        }

        public void setUserOnline(String online_user){

            View view = mView.findViewById(R.id.online_icon);

            if(online_user.equals("true")){
                view.setBackgroundResource(R.drawable.circle);
                view.setVisibility(View.VISIBLE);

            }else{
                view.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.circle_offline);
            }


        }
    }

}
