package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.EditText;
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


import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends AppCompatActivity {

    EditText setting_name_change, setting_status_change;
    CircleImageView setting_profile_pic;
    Button setting_save;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    String email, password, profilePic;
    Uri imageUri;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chat Matrix");
        actionBar.setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#344955"));
        actionBar.setBackgroundDrawable(colorDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        }


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Data is currently being updated");
        mProgressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setting_name_change = findViewById(R.id.setting_name_change);
        setting_status_change = findViewById(R.id.setting_status_change);
        setting_save = findViewById(R.id.setting_save);
        setting_profile_pic = findViewById(R.id.setting_profile_pic);
       
        // Fetching the data form firebase.
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference().child("Upload").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("userEmail").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                String userName = snapshot.child("userName").getValue().toString();
                profilePic = snapshot.child("profilePic").getValue().toString();
                String userStatus = snapshot.child("userStatus").getValue().toString();

                setting_name_change.setText(userName);
                setting_status_change.setText(userStatus);
                Picasso.get().load(profilePic).into(setting_profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Updating the profile pic.
        setting_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 999);

            }
        });

        setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                displayToast("Please Change the Default Image to Continue.");
                String name = setting_name_change.getText().toString();
                String status = setting_status_change.getText().toString();
                if(imageUri != null) {
                    mProgressDialog.dismiss();
                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    UsersDatabase usersDatabase = new
                                            UsersDatabase(firebaseAuth.getUid(), email, name, password, finalImageUri, status);
                                    updateTheData(databaseReference, usersDatabase);
                                }
                            });
                        }
                    });
                } else {
                    mProgressDialog.dismiss();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri = uri.toString();
                            UsersDatabase usersDatabase = new
                                    UsersDatabase(firebaseAuth.getUid(), email, name, password, finalImageUri, status);
                            updateTheData(databaseReference, usersDatabase);
                        }
                    });

                }
            }
        });
    }

    private void updateTheData(DatabaseReference databaseReference, UsersDatabase usersDatabase) {
        databaseReference.setValue(usersDatabase).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    finish();
                    mProgressDialog.dismiss();
                    displayToast("Data is Updated");
                    Intent intent = new Intent(Setting.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    displayToast("Something went Wrong");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            if (data != null) {
                imageUri = data.getData();
                setting_profile_pic.setImageURI(imageUri);
            }
        }
    }

    private void displayToast(String message) {
        Toast.makeText(Setting.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        Intent intent = new Intent(Setting.this, MainActivity.class);
        startActivity(intent);
        return  true;
    }
}