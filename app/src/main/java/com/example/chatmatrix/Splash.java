package com.example.chatmatrix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    ImageView logoImg;
    TextView logoName, developerName;
    Animation topAnimation, bottomAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImg = findViewById(R.id.logoImg);
        logoName = findViewById(R.id.logoName);
        developerName = findViewById(R.id.developerName);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logoImg.setAnimation(topAnimation);
        logoName.setAnimation(bottomAnimation);
        developerName.setAnimation(bottomAnimation);

        new Handler().postDelayed((Runnable) () -> {
            finish();
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
        },4000);
    }
}