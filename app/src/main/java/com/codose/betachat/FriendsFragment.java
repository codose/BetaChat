package com.codose.betachat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
public class FriendsFragment extends Fragment {
    private RecyclerView mFriendList;

    private DatabaseReference mFriendsData;

    private DatabaseReference UsersDatabase;

    private FirebaseAuth mAuth;

    private String current_uid;

    private View mainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_friends, container, false);

        // Inflate the layout for this fragment

        mFriendList = mainView.findViewById(R.id.rv_friends);

        mAuth = FirebaseAuth.getInstance();
        current_uid = mAuth.getCurrentUser().getUid();

        mFriendsData = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);
        mFriendsData.keepSynced(true);
        UsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersDatabase.keepSynced(true);
        mFriendList.setHasFixedSize(true);

        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mainView;

    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();


    }
    public void startListening(){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(current_uid)
                .limitToLast(50);

        final FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(query, Friend.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, int i, @NonNull Friend friend) {
                // Bind the Chat object to the ChatHolder
                final String user_id = getRef(i).getKey();

                UsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("username").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String t_img = dataSnapshot.child("t_img").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }

                        holder.setName(username);
                        holder.setStatus(status);
                        holder.setImage(t_img);
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(),"Heyy",Toast.LENGTH_LONG).show();
                                CharSequence[] option = new CharSequence[]{"Open Profile", "Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){

                                            Intent prof = new Intent(getContext(),UserProfile.class);
                                            prof.putExtra("user_id",user_id);
                                            startActivity(prof);

                                        }
                                        if(which == 1){
                                            Intent chat = new Intent(getContext(),ChatActivity.class);
                                            chat.putExtra("user_id",user_id)
                                            .putExtra("username",username);
                                            startActivity(chat);

                                        }
                                    }
                                });
                                builder.show();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // ...

            }

            @Override
            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_menu, parent, false);

                return new FriendViewHolder(view);
            }

        };
        mFriendList.setAdapter(adapter);
        adapter.startListening();
    }
    public  static class FriendViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public FriendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView mUsername = (TextView) mView.findViewById(R.id.user_single);
            mUsername.setText(name);
        }

        public void setStatus(String status){
            TextView mStatus = (TextView) mView.findViewById(R.id.user_status);
            mStatus.setText(status);
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

        public void setUserOnline(String online_user){

            View view = mView.findViewById(R.id.online_icon);

            if(online_user.equals("true")){

                 view.setVisibility(View.VISIBLE);

            }else{
                view.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.circle_offline);
            }


        }
    }
}
