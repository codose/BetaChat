package com.codose.betachat.Models;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codose.betachat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

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
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        String cUid = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(position);

        String from_user = c.getFrom();

        if(from_user.equals(cUid)){
            holder.constraintLayout.setGravity(Gravity.RIGHT);
            holder.messageText.setBackgroundResource(R.drawable.msg_send);
            holder.messageText.setTextColor(Color.BLACK);
            holder.profile_img.setVisibility(View.GONE);

        }else{
            holder.messageText.setBackgroundResource(R.drawable.msg_rec);
            holder.messageText.setTextColor(Color.WHITE);
        }
        holder.messageText.setText(c.getMessage());


    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircularImageView profile_img;
        public LinearLayout constraintLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.msg_single);
            messageText = itemView.findViewById(R.id.msg_txt);
            profile_img = itemView.findViewById(R.id.msg_dp);

        }
    }
}
