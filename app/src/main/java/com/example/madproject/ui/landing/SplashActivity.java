package com.example.madproject.ui.landing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.MainActivity;
import com.example.madproject.R;
import com.example.madproject.ui.authentication.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");

            Intent intent;
            if (isLoggedIn && !savedUsername.isEmpty() && !savedPassword.isEmpty()) {
                // Redirect to MainActivity if logged in
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                // Redirect to LandingActivity if not logged in
                intent = new Intent(SplashActivity.this, LandingActivity.class);
            }

            startActivity(intent);
            finish();
        }, 3000);
    }
}