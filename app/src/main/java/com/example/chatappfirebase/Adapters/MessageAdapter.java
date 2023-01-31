package com.example.chatappfirebase.Adapters;

import static com.example.chatappfirebase.Activity.ChatActivity.rImage;
import static com.example.chatappfirebase.Activity.ChatActivity.senderImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappfirebase.Models.Messages;
import com.example.chatappfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_layout_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciever_layout_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        Messages messages = messagesArrayList.get(adapterPosition);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.chatText.setText(messages.getMessage());
            Picasso.get().load(senderImage).into(viewHolder.chatProfile);

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.chatText.setText(messages.getMessage());
            Picasso.get().load(rImage).into(viewHolder.chatProfile);

        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())) {
            return ITEM_SEND;
        } else
            return ITEM_RECEIVE;
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chatProfile;
        TextView chatText;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            chatProfile = itemView.findViewById(R.id.profile_image);
            chatText = itemView.findViewById(R.id.chat_text);

        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chatProfile;
        TextView chatText;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            chatProfile = itemView.findViewById(R.id.profile_image);
            chatText = itemView.findViewById(R.id.chat_text);

        }
    }
}