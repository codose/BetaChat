package com.codose.betachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AllUsers extends AppCompatActivity {
    private Toolbar mToolbar;

    private DatabaseReference mDatabase;

    private FirebaseAuth currentUser;

    private RecyclerView rv_user;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mToolbar = findViewById(R.id.users_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        rv_user = findViewById(R.id.user_rv);

        LinearLayoutManager UserLayout = new LinearLayoutManager(this);
        rv_user.setLayoutManager(UserLayout);
    }
    @Override
    protected void onStart() {
        super.onStart();
        cUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.child(cUser.getUid()).child("online").setValue("true");
        startListening();

    }

    @Override
    protected void onPause() {
        super.onPause();
        cUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.child(cUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    public void startListening(){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_menu, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, Users model) {
                // Bind the Chat object to the ChatHolder
                holder.setName(model.username);
                holder.setStatus(model.status);
                holder.setImage(model.t_img);
                final String user_id = getRef(position).getKey();
                // ...
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent prof = new Intent(getApplicationContext(),UserProfile.class);
                        prof.putExtra("user_id",user_id);
                        startActivity(prof);
                    }
                });
            }

        };
        rv_user.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UserViewHolder(View itemView) {
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
    }


}
