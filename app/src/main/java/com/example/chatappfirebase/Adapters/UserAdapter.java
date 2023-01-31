package com.example.chatappfirebase.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappfirebase.Activity.ChatActivity;
import com.example.chatappfirebase.Models.Users;
import com.example.chatappfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    List<Users> userProfileList;
    Context context;

    public UserAdapter(List<Users> userProfileList, Context context) {
        this.userProfileList = userProfileList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        Users users = userProfileList.get(adapterPosition);

        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                .equals(users.getUid())) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }

        holder.profileName.setText(users.getName());
        holder.profileStatus.setText(users.getStatus());
        Picasso.get().load(users.getImageUri()).into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userName",users.getName());
                intent.putExtra("userImage",users.getImageUri());
                intent.putExtra("userUid",users.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userProfileList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView profileName;
        TextView profileStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.user_name);
            profileStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
