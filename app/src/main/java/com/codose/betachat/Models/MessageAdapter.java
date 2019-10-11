package com.codose.betachat.Models;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.codose.betachat.ChatActivity;
import com.codose.betachat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.Gravity.END;
import static android.view.Gravity.START;
import static android.view.View.GONE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;


    public MessageAdapter( List<Messages> mMessageList ){
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single,parent,false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        String cUid = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(position);

        long timestamp = c.getTime();
        String time = DateUtils.formatDateTime(holder.mView.getContext(),timestamp,DateUtils.FORMAT_SHOW_TIME);
        holder.msg_time.setText(time);

        String from_user = c.getFrom();
        String seen = c.getSeen();


        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference();
        mUser.keepSynced(true);

        mUser.child("Users").child(from_user).child("t_img").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String t_img = dataSnapshot.getValue().toString();
                Picasso.get().load(t_img).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.headshot).into(holder.profile_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(t_img).placeholder(R.drawable.headshot).into(holder.profile_img);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(from_user.equals(cUid)){
            holder.msg_time.setGravity(END);
            holder.constraintLayout.setGravity(END);
            holder.constraintLayout.setPadding(65,0,15,5);
            holder.messageText.setBackgroundResource(R.drawable.msg_send);
            holder.messageText.setTextColor(Color.BLACK);
            holder.profile_img.setVisibility(GONE);
            holder.messageText.setText(c.getMessage());
            holder.seen_icon.setVisibility(View.VISIBLE);
        }else{
            holder.seen_icon.setVisibility(GONE);
            holder.msg_time.setGravity(START);
            holder.profile_img.setVisibility(View.VISIBLE);
            holder.constraintLayout.setGravity(Gravity.START);
            holder.constraintLayout.setPadding(15,0,65,5);
            holder.messageText.setBackgroundResource(R.drawable.msg_rec);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setText(c.getMessage());
        }


        if(seen.equals("true")){
            holder.seen_icon.setBackgroundResource(R.drawable.ic_check_double_solid);
        }else if (seen.equals("false")){
            holder.seen_icon.setBackgroundResource(R.drawable.ic_check_solid);
        }else{
            holder.seen_icon.setBackgroundResource(R.drawable.ic_clock_regular);
        }


    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, msg_time;
        public CircularImageView profile_img;
        public LinearLayout constraintLayout;
        public View seen_icon;
        View mView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            constraintLayout = itemView.findViewById(R.id.msg_single);
            messageText = itemView.findViewById(R.id.msg_txt);
            profile_img = itemView.findViewById(R.id.msg_dp);
            msg_time = itemView.findViewById(R.id.msg_time);
            seen_icon = itemView.findViewById(R.id.seen_icon);

        }
    }
}
