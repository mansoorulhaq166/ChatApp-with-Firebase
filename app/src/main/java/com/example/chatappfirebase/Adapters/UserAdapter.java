package com.example.chatappfirebase.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappfirebase.HomeActivity;
import com.example.chatappfirebase.Models.Users;
import com.example.chatappfirebase.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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

        holder.profileName.setText(users.getName());
        holder.profileStatus.setText(users.getStatus());
        Picasso.get().load(users.getImageUri()).into(holder.profileImage);
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
