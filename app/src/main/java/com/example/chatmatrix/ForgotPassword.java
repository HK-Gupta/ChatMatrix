package com.example.chatmatrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText forgot_email;
    TextView goto_login_from_forgot;
    Button recover_password;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().hide();

        forgot_email = findViewById(R.id.forgot_email);
        goto_login_from_forgot = findViewById(R.id.goto_login_from_forgot);
        recover_password = findViewById(R.id.recover_password);

        firebaseAuth = FirebaseAuth.getInstance();

        goto_login_from_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNextActivity(Login.class);
            }
        });

        recover_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgot_email.getText().toString();
                if(email.isEmpty()) {
                    displayToast("Enter the mail id");
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                displayToast("Mail is sent, Kindly check the mail box to recover your Password");
                                callNextActivity(MainActivity.class);
                            } else {
                                displayToast("Account doesn't Exist !");
                            }
                        }
                    });
                }
            }
        });
    }
    void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    private void callNextActivity(Class<?> destinationActivity) {
        finish();
        Intent intent = new Intent(ForgotPassword.this, destinationActivity);
        startActivity(intent);
    }
}