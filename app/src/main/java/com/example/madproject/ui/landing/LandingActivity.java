package com.example.madproject.ui.landing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.madproject.MainActivity;
import com.example.madproject.R;
import com.example.madproject.ui.authentication.LoginActivity;
import com.example.madproject.ui.authentication.RegisterActivity;

    public class LandingActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_landing); // Replace with your landing layout file

            // Find buttons
            Button loginButton = findViewById(R.id.btnLogin);
            Button signupButton = findViewById(R.id.btnSignUp);

            // Navigate to MainActivity when Log In is clicked
            loginButton.setOnClickListener(v -> {
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
            });

            // Navigate to MainActivity when Sign Up is clicked
            signupButton.setOnClickListener(v -> {
                Intent intent = new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }