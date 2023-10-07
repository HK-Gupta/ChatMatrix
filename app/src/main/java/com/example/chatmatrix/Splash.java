package com.example.chatmatrix;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    ImageView logoImg;
    TextView logoName, developerName;
    Animation topAnimation, bottomAnimation;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chat Matrix");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#344955"));
        actionBar.setBackgroundDrawable(colorDrawable);

        logoImg = findViewById(R.id.logoImg);
        logoName = findViewById(R.id.logoName);
        developerName = findViewById(R.id.developerName);
        firebaseAuth = FirebaseAuth.getInstance();

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logoImg.setAnimation(topAnimation);
        logoName.setAnimation(bottomAnimation);
        developerName.setAnimation(bottomAnimation);

        new Handler().postDelayed((Runnable) () -> {
            finish();
            if(firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
        },4000);
    }
}