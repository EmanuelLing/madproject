package com.example.madproject.ui.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.madproject.MainActivity;
import com.example.madproject.R;
import com.example.madproject.ui.landing.LandingActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    ImageView ivBack;
    EditText username, password;
    Button btnLogin;
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ivBack = findViewById(R.id.ivBack);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etpPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean isRemembered = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!savedUsername.isEmpty()) {
            username.setText(savedUsername);
            cbRememberMe.setChecked(true);
        }

        // Optional: Auto-login if credentials are saved
        if (isRemembered && !savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            password.setText(savedPassword);
        }


        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Ensure LoginActivity is closed
        });

        btnLogin.setOnClickListener(v -> {
            if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                loginAccount();
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserCredentials(String userId, String username, String password, String fullName, boolean rememberMe) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (rememberMe) {
            editor.putString("userId", userId);
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("fullName", fullName);
            editor.putBoolean("isLoggedIn", true);
        } else {
            editor.clear(); // Clear saved credentials if not remembering
            editor.putString("userId", userId);
            editor.putString("username", username);
            editor.putString("fullName", fullName);
            editor.putBoolean("isLoggedIn", false);
        }

        editor.apply();
    }

    private void loginAccount() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("custUsername", username.getText().toString());
                jsonObject.put("password", password.getText().toString());

                URL loginUrl = new URL("https://hushed-charming-clipper.glitch.me/customer/login");
                HttpURLConnection loginConnection = (HttpURLConnection) loginUrl.openConnection();
                loginConnection.setRequestMethod("POST");
                loginConnection.setRequestProperty("Content-Type", "application/json");
                loginConnection.setDoOutput(true);

                try (OutputStream os = loginConnection.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = loginConnection.getResponseCode();
                Handler mainHandler = new Handler(Looper.getMainLooper());

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(loginConnection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        // Parse login response
                        JSONObject responseJson = new JSONObject(response.toString());
                        JSONObject userJson = responseJson.getJSONObject("user");

                        String userId = userJson.getString("custId");
                        String username = userJson.getString("custUsername");

                        // Fetch full name from customer API
                        fetchCustomerName(userId, username);
                    }
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    mainHandler.post(() ->
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    mainHandler.post(() ->
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("LoginError", "Error during login", e);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() ->
                        Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }
    private void fetchCustomerName(String userId, String username) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                URL url = new URL("https://hushed-charming-clipper.glitch.me/customer");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Handler mainHandler = new Handler(Looper.getMainLooper());

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        // Parse customer data
                        JSONArray customers = new JSONArray(response.toString());
                        for (int i = 0; i < customers.length(); i++) {
                            JSONObject customer = customers.getJSONObject(i);
                            if (customer.getString("custUsername").equals(username)) {
                                String fullName = customer.getString("custName");

                                // Update UI with the user's name
                                mainHandler.post(() -> {
                                    Toast.makeText(LoginActivity.this, "Welcome, " + fullName, Toast.LENGTH_SHORT).show();
                                    saveUserCredentials(
                                            userId,
                                            username,
                                            password.getText().toString(),
                                            fullName,
                                            cbRememberMe.isChecked()
                                    );

                                    // Redirect to MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("userId", userId); // Pass the userId
                                    intent.putExtra("fullName", fullName);
                                    startActivity(intent);
                                    finish();
                                });
                                break;
                            }
                        }
                    }
                } else {
                    mainHandler.post(() ->
                            Toast.makeText(LoginActivity.this, "Failed to fetch customer data", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("FetchCustomerError", "Error during fetching customer data", e);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() ->
                        Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // This will close LoginActivity and return to LandingActivity
    }
}