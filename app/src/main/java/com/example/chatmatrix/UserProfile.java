package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    CircleImageView profile_image_details;
    TextView profile_user_name, profile_user_status, profile_user_mail, profile_user_password, profile_user_id;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        profile_image_details = findViewById(R.id.profile_image_details);
        profile_user_name = findViewById(R.id.profile_user_name);
        profile_user_status = findViewById(R.id.profile_user_status);
        profile_user_mail = findViewById(R.id.profile_user_mail);
        profile_user_password = findViewById(R.id.profile_user_password);
        profile_user_id = findViewById(R.id.profile_user_id);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Details:");
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#344955"));
        actionBar.setBackgroundDrawable(colorDrawable);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("userEmail").getValue().toString();
                String password = snapshot.child("password").getValue().toString();
                String userName = snapshot.child("userName").getValue().toString();
                String profilePic = snapshot.child("profilePic").getValue().toString();
                String userStatus = snapshot.child("userStatus").getValue().toString();

                Picasso.get().load(profilePic).into(profile_image_details);
                profile_user_name.setText(userName);
                profile_user_status.setText(userStatus);
                profile_user_mail.setText(email);
                profile_user_password.setText(password);
                profile_user_id.setText(firebaseAuth.getUid().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.goto_setting) {
            callNextActivity(Setting.class);
        }
        else if(item.getItemId() == R.id.logout_id) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setCancelable(false);
            alert.setTitle("Logging Out!");
            alert.setMessage("Do you really want to logout?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firebaseAuth.signOut();
                    finish();
                    Toast.makeText(UserProfile.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                    callNextActivity(Login.class);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();

        } else {
            finish();
            callNextActivity(MainActivity.class);
        }

        return true;
    }

    private void callNextActivity(Class<?> destinationActivity) {
        Intent intent = new Intent(UserProfile.this, destinationActivity);
        startActivity(intent);
    }
}