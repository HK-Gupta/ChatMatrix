package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.sql.StatementEvent;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    EditText signup_user_name, signup_email_id, signup_password;
    Button signup_button, goto_login_page;
    CircleImageView profile_pic;
    Uri imageUri;
    String imageUriStr;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseStorage mFirebaseStorage;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please Check the Mail to Continue");
        mProgressDialog.setCancelable(false);

        signup_user_name = findViewById(R.id.signup_user_name);
        signup_email_id = findViewById(R.id.signup_email_id);
        signup_password = findViewById(R.id.signup_password);
        signup_button = findViewById(R.id.signup_button);
        goto_login_page = findViewById(R.id.goto_login_page);
        profile_pic = findViewById(R.id.profile_pic);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        goto_login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNextActivity(Login.class);
            }
        });

        // To access photos from the device and choose it.
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 999);

            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signup_user_name.getText().toString();
                String emailId = signup_email_id.getText().toString();
                String password = signup_password.getText().toString();
                String userStatue = "Hello Everyone"; // This is the Default status.

                if(name.isEmpty() || emailId.isEmpty() || password.isEmpty()) {
                    mProgressDialog.dismiss();
                    displayToast("Please Fill All The Fields");
                } else if(password.length() < 6) {
                    mProgressDialog.dismiss();
                    signup_password.setError("Please Enter a Strong Password");
                    displayToast("Password is too weak,\nKindly use a strong password.");
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(emailId, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                addUserDataTODataBase(task, emailId, name, password, userStatue);
                            } else {
                                displayToast(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void addUserDataTODataBase(Task<AuthResult> task, String emailId, String name, String password, String userStatue) {
        String id = task.getResult().getUser().getUid();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("user").child(id);
        StorageReference storageReference = mFirebaseStorage.getReference().child("Upload").child(id);

        if(imageUri != null) {
            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUriStr = uri.toString();
                            }
                        });
                    }
                }
            });
        } else {
            imageUriStr = "https://firebasestorage.googleapis.com/v0/b/chatmatrix-5659d.appspot.com/o/avatar.png?alt=media&token=bc28e5cf-7eb6-4da0-9e3d-56d6e933d2e0";
        }
        UsersDatabase usersDatabase = new UsersDatabase(id, emailId, name, password, imageUriStr, userStatue);
        databaseReference.setValue(usersDatabase).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mProgressDialog.show();
                    displayToast("Id Created Successfully.\nCheck Your Mail Inbox.");
                    callEmailVerificationMethod();
                } else {
                    displayToast("Something went Wrong.\n User Id is not Created.");
                }
            }
        });
    }

    private void callEmailVerificationMethod() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    firebaseAuth.signOut();
                    callNextActivity(Login.class);
                }
            });
        } else {
            displayToast("Something went Wrong.\nPlease check the connectivity.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999) {
            if(data != null) {
                imageUri = data.getData();
                profile_pic.setImageURI(imageUri);
            }
        }
    }

    private void callNextActivity(Class<?> destinationActivity) {
        finish();
        Intent intent = new Intent(SignUp.this, destinationActivity);
        startActivity(intent);
    }
    private void displayToast(String message) {
        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
    }
}