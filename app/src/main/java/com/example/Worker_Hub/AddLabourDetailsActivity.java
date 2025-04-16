package com.example.Worker_Hub;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddLabourDetailsActivity extends AppCompatActivity {

    private EditText etFullName, etMobileNumber, etAddress, etHourlyWage, etDob, etProfession, etAboutInfo;
    private RadioGroup radioGroupGender;
    private Button btnRegister, btnStatus;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_labour_details);

        etFullName = findViewById(R.id.etFullName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddress = findViewById(R.id.etAddress);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        etDob = findViewById(R.id.etDob);
        etProfession = findViewById(R.id.etProfession);
        etAboutInfo = findViewById(R.id.etAboutInfo);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnStatus = findViewById(R.id.btnStatus);

        databaseReference = FirebaseDatabase.getInstance().getReference("LabourDetails");

        SharedPreferences sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", "default_user");

        if (username.equals("default_user")) {
            Toast.makeText(this, "User not logged in. Please log in to continue.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnStatus.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddLabourDetailsActivity.this);
            builder.setTitle("Set Status");

            String[] statusOptions = {"Available", "Not Available"};
            builder.setSingleChoiceItems(statusOptions, -1, (dialog, which) -> {
                String selectedStatus = statusOptions[which];
                boolean isAvailable = selectedStatus.equals("Available");

                SharedPreferences sharedPreferences1 = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
                String username1 = sharedPreferences1.getString("loggedInUsername", "default_user");

                if (username1.equals("default_user")) {
                    Toast.makeText(AddLabourDetailsActivity.this, "User not logged in. Please log in to continue.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                databaseReference.child(username1).child("isAvailable").setValue(isAvailable)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddLabourDetailsActivity.this, "Status updated to: " + selectedStatus, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddLabourDetailsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            }
                        });

                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LabourDetails labourDetails = dataSnapshot.getValue(LabourDetails.class);
                    if (labourDetails != null) {
                        etFullName.setText(labourDetails.getFullName());
                        etMobileNumber.setText(labourDetails.getMobileNumber());
                        etAddress.setText(labourDetails.getAddress());
                        etHourlyWage.setText(String.valueOf(labourDetails.getHourlyWage()));
                        etDob.setText(labourDetails.getDob());
                        etProfession.setText(labourDetails.getProfession());
                        etAboutInfo.setText(labourDetails.getAboutInfo());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddLabourDetailsActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
