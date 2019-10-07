package com.codose.betachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserProfile extends AppCompatActivity {
    private TextView profile_name, profile_status, friends_count, mutual_count;
    private Button send_req,decline_req;
    private ImageView profile_dp;
    private ProgressBar progressBar;

    private DatabaseReference mDataRef;

    private DatabaseReference mDatabaseRef;

    private DatabaseReference FriendDatabase;

    private DatabaseReference NotificationDatabase;



    private FirebaseUser currentUser;

    private DatabaseReference ReqDatabase;

    private int current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        final String user_id = getIntent().getStringExtra("user_id");
        profile_name = findViewById(R.id.profile_name);
        profile_status = findViewById(R.id.profile_status);
        friends_count = findViewById(R.id.friend_count);
        mutual_count = findViewById(R.id.mutual_count);
        profile_dp = findViewById(R.id.profile_dp);

        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.VISIBLE);

        current_state = 0;



        send_req = findViewById(R.id.send_req);
        decline_req = findViewById(R.id.decline_req);
        decline_req.setEnabled(false);
        decline_req.setVisibility(View.INVISIBLE);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        ReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");

        FriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        NotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String u_name = dataSnapshot.child("username").getValue().toString();
                String u_status = dataSnapshot.child("status").getValue().toString();
                String u_img = dataSnapshot.child("image").getValue().toString();

                profile_name.setText(u_name);
                profile_status.setText(u_status);
                Picasso.get().load(u_img).placeholder(R.drawable.headshot).into(profile_dp);

                //--------------- FRIENDS -----------//
                    ReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(user_id)) {
                                String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                if (req_type.equals("received")) {
                                    send_req.setText("Accept Friend Request");

                                    current_state = 2;

                                    decline_req.setVisibility(View.VISIBLE);
                                    decline_req.setEnabled(true);

                                } else if (req_type.equals("sent")) {
                                    send_req.setText("Cancel Friend Request");

                                    current_state = 1;
                                }

                                progressBar.setVisibility(View.GONE);
                            }else{

                                FriendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild(user_id)){

                                            send_req.setText("UnFriend");
                                            send_req.setEnabled(true);

                                            current_state = 3;
                                            decline_req.setVisibility(View.INVISIBLE);
                                            decline_req.setEnabled(false);


                                        }
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                        progressBar.setVisibility(View.GONE);

                                    }
                                });

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            send_req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_req.setEnabled(false);

                    //---------- Not Friends State-----------------//
                    if(current_state == 0){

                        ReqDatabase.child(currentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    ReqDatabase.child(user_id).child(currentUser.getUid()).child("request_type")
                                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(UserProfile.this, "Request Sent.",
                                                    Toast.LENGTH_SHORT).show();
                                            HashMap<String, String> notification_data = new HashMap<>();
                                            notification_data.put("from",currentUser.getUid());
                                            notification_data.put("type","request");

                                            NotificationDatabase.child(user_id).push().setValue(notification_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    send_req.setText("Cancel Friend Request");
                                                    send_req.setEnabled(true);

                                                    current_state = 1;
                                                }
                                            });


                                        }
                                    });



                                }else{

                                    Toast.makeText(UserProfile.this, "Unable to send. Please try again later",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                    //-----------------Cancel Request state---------------//

                    if(current_state==1){
                        ReqDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ReqDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        send_req.setText("Send Request");
                                        send_req.setEnabled(true);

                                        current_state = 0;

                                    }
                                });
                            }
                        });


                    }
                    //-----------Accept Request State-------------
                    if(current_state==2){

                        final String mDate = DateFormat.getDateTimeInstance().format(new Date());

                        FriendDatabase.child(currentUser.getUid()).child(user_id).child("date").setValue(mDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                FriendDatabase.child(user_id).child(currentUser.getUid()).child("date").setValue(mDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ReqDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                ReqDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        send_req.setText("UnFriend");
                                                        send_req.setEnabled(true);

                                                        current_state = 3;

                                                        decline_req.setVisibility(View.INVISIBLE);
                                                        decline_req.setEnabled(false);

                                                    }
                                                });
                                            }
                                        });


                                    }
                                });

                            }
                        });

                    }
                    //-----------Unfriend----------
                    if(current_state==3){
                        FriendDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FriendDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        send_req.setText("Send Request");
                                        send_req.setEnabled(true);

                                        current_state = 0;

                                    }
                                });
                            }
                        });

                    }
                }
            });










    }

    @Override
    protected void onStart() {
        super.onStart();
        String uid = getIntent().getStringExtra("user_id");
        if(currentUser.getUid().equals(uid)){
            send_req.setVisibility(View.GONE);
            decline_req.setVisibility(View.GONE);
        }
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        mDataRef.child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        mDataRef.child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onStop() {
        super.onStop();


    }
}
