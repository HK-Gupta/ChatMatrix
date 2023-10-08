package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText login_email, login_password;
    Button login_button, goto_sign_in;
    TextView goto_forgot_password;
    FirebaseAuth firebaseAuth;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please Wait Logging In...");
        mProgressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        goto_sign_in = findViewById(R.id.goto_sign_in);
        goto_forgot_password = findViewById(R.id.goto_forgot_password);

        // Login if the id and password are correct.
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();
                if(email.isEmpty() || password.isEmpty()) {
                    mProgressDialog.dismiss();
                    displayToast("Please Fill All The Fields");
                } else if(password.length() < 6) {
                    mProgressDialog.dismiss();
                    login_password.setError("Please Enter a Strong Password");
                    displayToast("Password is too weak,\nKindly use a strong password.");
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.show();
                                checkMailMethod();
                            } else {
                                displayToast("Email & Password is Not Recognised");
                            }
                        }
                    });
                }
            }
        });

        goto_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNextActivity(SignUp.class);
            }
        });

        goto_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNextActivity(ForgotPassword.class);
            }
        });
    }

    private void checkMailMethod() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()) {
            displayToast("Logged In");
            callNextActivity(MainActivity.class);
        } else {
            displayToast("New User SignUp");
            firebaseAuth.signOut();
        }
    }

    private void callNextActivity(Class<?> destinationActivity) {
        finish();
        Intent intent = new Intent(Login.this, destinationActivity);
        startActivity(intent);
    }
    private void displayToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }
}