package com.example.madproject.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.example.madproject.ui.landing.LandingActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class RegisterActivity extends AppCompatActivity {

    ImageView ivBack;
    EditText username, password, passwordConfirm, fullName, phoneNumber, address;
    Button btnRegister;
    TextView tvPasswordError, tvConfirmPasswordError;

    private static final String REGISTER_URL = "https://hushed-charming-clipper.glitch.me/customer/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ivBack = findViewById(R.id.ivBack);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etpPassword);
        passwordConfirm = findViewById(R.id.etpPasswordConfirm);
        fullName = findViewById(R.id.etName);
        phoneNumber = findViewById(R.id.etMobile);
        address = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);

        setupPasswordValidation();

        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Ensure LoginActivity is closed
        });

        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                registerAccount();
            }
        });
    }

    private void setupPasswordValidation() {
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s.toString());
                validateConfirmPassword(); // Check confirm password when main password changes
            }
        });

        passwordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword();
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePhoneNumber(s.toString());
            }
        });
    }

    private boolean validateConfirmPassword() {
        String passwordText = password.getText().toString();
        String confirmPasswordText = passwordConfirm.getText().toString();

        if (confirmPasswordText.isEmpty()) {
            passwordConfirm.setError("Please confirm your password");
            return false;
        } else if (!passwordText.equals(confirmPasswordText)) {
            passwordConfirm.setError("Passwords do not match");
            return false;
        } else {
            passwordConfirm.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber(String phoneText) {
        // Basic phone number validation
        if (phoneText.isEmpty()) {
            phoneNumber.setError("Phone number cannot be empty");
            return false;
        } else if (!phoneText.matches("^[0-9]{10,11}$")) {
            phoneNumber.setError("Phone number must be 10 or 11 digits");
            return false;
        } else {
            phoneNumber.setError(null);
            return true;
        }
    }

    // Update validateInputs method
    private boolean validateInputs() {
        return validatePassword(password.getText().toString()) &&
                validateConfirmPassword() &&
                validatePhoneNumber(phoneNumber.getText().toString()) &&
                !username.getText().toString().isEmpty() &&
                !fullName.getText().toString().isEmpty() &&
                !address.getText().toString().isEmpty();
    }

    private boolean validatePassword(String passwordText) {
        // Check for minimum requirements
        boolean hasUppercase = !passwordText.equals(passwordText.toLowerCase());
        boolean hasLowercase = !passwordText.equals(passwordText.toUpperCase());
        boolean hasSymbol = passwordText.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (passwordText.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else if (passwordText.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            return false;
        } else if (passwordText.length() > 12) {
            password.setError("Password must be no more than 12 characters long");
            return false;
        } else if (!hasUppercase) {
            password.setError("Password must include an uppercase letter");
            return false;
        } else if (!hasLowercase) {
            password.setError("Password must include a lowercase letter");
            return false;
        } else if (!hasSymbol) {
            password.setError("Password must include a symbol");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void registerAccount() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("custUsername", username.getText().toString().trim());
                jsonObject.put("password", password.getText().toString().trim());
                jsonObject.put("custName", fullName.getText().toString().trim());
                jsonObject.put("custPhoneNo", phoneNumber.getText().toString().trim());
                jsonObject.put("custAddress", address.getText().toString().trim());

                URL url = new URL(REGISTER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                Handler mainHandler = new Handler(Looper.getMainLooper());

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    mainHandler.post(() -> {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    });
                } else {
                    String errorResponse = readErrorStream(connection);
                    Log.e("RegisterError", "Error Code: " + responseCode + ", Response: " + errorResponse);

                    mainHandler.post(() ->
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed: " + (errorResponse.isEmpty() ? "Unknown error" : errorResponse),
                                    Toast.LENGTH_LONG).show()
                    );
                }
            } catch (Exception e) {
                Log.e("RegisterException", "Registration error", e);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() ->
                        Toast.makeText(RegisterActivity.this,
                                "Network error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                executorService.shutdown();
            }
        });
    }

    private String readErrorStream(HttpURLConnection connection) {
        try {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream == null) return "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            Log.e("ErrorStreamRead", "Failed to read error stream", e);
            return "";
        }
    }
}
