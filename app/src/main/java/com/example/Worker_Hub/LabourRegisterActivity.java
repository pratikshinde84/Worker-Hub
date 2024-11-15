package com.example.Worker_Hub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Worker_Hub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LabourRegisterActivity extends AppCompatActivity {

    private EditText etName, etMobile, etEmail, etUsername, etPassword;
    private Button btnRegister;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labour_register);

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("LoginLabour");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String mobile = etMobile.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    etName.setError("Please enter your name");
                    etName.requestFocus();
                } else if (mobile.isEmpty()) {
                    etMobile.setError("Please enter your mobile number");
                    etMobile.requestFocus();
                } else if (mobile.length() != 10) {
                    etMobile.setError("Mobile number must be 10 digits");
                    etMobile.requestFocus();
                } else if (email.isEmpty()) {
                    etEmail.setError("Please enter your email");
                    etEmail.requestFocus();
                } else if (!email.contains("@") || !email.contains(".com")) {
                    etEmail.setError("Please enter a valid email");
                    etEmail.requestFocus();
                } else if (username.isEmpty()) {
                    etUsername.setError("Please enter a username");
                    etUsername.requestFocus();
                } else if (username.length() < 8) {
                    etUsername.setError("Username must be at least 8 characters");
                    etUsername.requestFocus();
                } else if (password.isEmpty()) {
                    etPassword.setError("Please enter a password");
                    etPassword.requestFocus();
                } else if (password.length() < 8) {
                    etPassword.setError("Password must be at least 8 characters");
                    etPassword.requestFocus();
                } else {
                    reference.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    etUsername.setError("Username already exists. Please choose another.");
                                    etUsername.requestFocus();
                                } else {
                                    LabourHelper labourHelper = new LabourHelper(name, mobile, email, username, password);
                                    reference.child(username).setValue(labourHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LabourRegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LabourRegisterActivity.this, LabourLoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(LabourRegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(LabourRegisterActivity.this, "Error checking username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
