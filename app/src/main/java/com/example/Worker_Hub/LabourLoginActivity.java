package com.example.Worker_Hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LabourLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private CheckBox showPassword;
    private TextView labourTvSignup;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startActivity(new Intent(LabourLoginActivity.this, AddLabourDetailsActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_labour_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.loginButton);
        showPassword = findViewById(R.id.showPassword);
        labourTvSignup = findViewById(R.id.signUpLink);

        databaseReference = FirebaseDatabase.getInstance().getReference("LoginLabour");

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etPassword.setSelection(etPassword.length());
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                etUsername.setError("Please enter your username");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Please enter your password");
                return;
            }

            authenticateUser(username, password);
        });

        labourTvSignup.setOnClickListener(v -> {
            startActivity(new Intent(LabourLoginActivity.this, LabourRegisterActivity.class));
        });
    }

    private void authenticateUser(String username, String password) {
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    if (dbPassword != null && dbPassword.equals(password)) {
                        Toast.makeText(LabourLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("loggedInUsername", username); // Store the username
                        editor.apply();

                        startActivity(new Intent(LabourLoginActivity.this, AddLabourDetailsActivity.class));
                        finish();
                    } else {
                        etPassword.setError("Incorrect password");
                    }
                } else {
                    etUsername.setError("Username does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LabourLoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
