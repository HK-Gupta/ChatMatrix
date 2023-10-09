package com.example.chatmatrix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    Context mainActivity;
    ArrayList<UsersDatabase> usersDatabaseArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<UsersDatabase> usersDatabaseArrayList) {
        this.mainActivity = mainActivity;
        this.usersDatabaseArrayList = usersDatabaseArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the User items in teh layout.
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.users_item, parent, false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewHolder holder, int position) {
        // Setting the User information int the holder
        UsersDatabase usersDatabase = usersDatabaseArrayList.get(position);
        holder.user_name.setText(usersDatabase.getUserName());
        holder.user_status.setText(usersDatabase.getUserStatus());
        Picasso.get().load(usersDatabase.getProfilePic()).into(holder.user_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, ChatClass.class);
                intent.putExtra("name_to_user", usersDatabase.getUserName());
                intent.putExtra("image_to_user", usersDatabase.getProfilePic());
                intent.putExtra("uid_to_user", usersDatabase.getUserId());
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersDatabaseArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView user_name, user_status;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            user_status = itemView.findViewById(R.id.user_status);
        }
    }
}
