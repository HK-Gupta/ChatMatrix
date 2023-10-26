package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.ProtectionDomain;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    RecyclerView main_user_recyclerView;
    UserAdapter userAdapter;
    ArrayList<UsersDatabase> usersDatabaseArrayList;
    CircleImageView admin_image;
    TextView admin_name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chat Matrix");
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#344955"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setHomeButtonEnabled(false); // Disable the button.
        actionBar.setDisplayHomeAsUpEnabled(false); // Remove left caret.
        actionBar.setDisplayShowHomeEnabled(false); // Remove icon.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            callNextActivity(Login.class);
        }

        admin_image = findViewById(R.id.admin_image);
        admin_name = findViewById(R.id.admin_name);

        // For Collecting Admin data.
        DatabaseReference dbReference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("userEmail").getValue().toString();
                String password = snapshot.child("password").getValue().toString();
                String userName = snapshot.child("userName").getValue().toString();
                String profilePic = snapshot.child("profilePic").getValue().toString();
                String userStatus = snapshot.child("userStatus").getValue().toString();

                admin_name.setText(userName);
                Picasso.get().load(profilePic).into(admin_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("user");

        usersDatabaseArrayList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UsersDatabase usersDatabase = dataSnapshot.getValue(UsersDatabase.class);
                    if(usersDatabase.getUserEmail() == email || usersDatabase.getUserId() == firebaseAuth.getUid()) {
                        continue;
                    }
                    usersDatabaseArrayList.add(usersDatabase);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        main_user_recyclerView = findViewById(R.id.main_user_recyclerView);
        main_user_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(MainActivity.this, usersDatabaseArrayList);
        main_user_recyclerView.setAdapter(userAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.goto_setting_main) {
            callNextActivity(Setting.class);
        } else if (item.getItemId()==R.id.user_profile_main) {
            callNextActivity(UserProfile.class);
        }  else if(item.getItemId() == R.id.logout_id_main){
            AlertDialog.Builder name = new AlertDialog.Builder(MainActivity.this);
            name.setTitle("Logging Out!");
            name.setCancelable(false);
            name.setMessage("Do you really want to logout?");
            name.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firebaseAuth.signOut();
                    displayToast("Logged Out Successfully");
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            name.show();
        }
        return true;
    }

    private void callNextActivity(Class<?> destinationActivity) {
        Intent intent = new Intent(MainActivity.this, destinationActivity);
        startActivity(intent);
    }
    private void displayToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}