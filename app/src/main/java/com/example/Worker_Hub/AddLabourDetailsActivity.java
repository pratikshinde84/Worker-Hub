package com.example.Worker_Hub;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

        // Add DatePickerDialog for etDob
        etDob.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddLabourDetailsActivity.this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etDob.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

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

        btnRegister.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString().trim();
            String mobileNumber = etMobileNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String hourlyWageStr = etHourlyWage.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String profession = etProfession.getText().toString().trim();
            String aboutInfo = etAboutInfo.getText().toString().trim();

            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            String gender;
            if (selectedGenderId == R.id.rbMale) {
                gender = "Male";
            } else if (selectedGenderId == R.id.rbFemale) {
                gender = "Female";
            } else {
                gender = "Other";
            }

            if (fullName.isEmpty() || mobileNumber.isEmpty() || address.isEmpty() || hourlyWageStr.isEmpty() || dob.isEmpty() || profession.isEmpty()) {
                Toast.makeText(AddLabourDetailsActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            LabourDetails labourDetails = new LabourDetails(username,fullName, mobileNumber, address, hourlyWageStr, dob, profession, aboutInfo, gender);

            databaseReference.child(username).setValue(labourDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddLabourDetailsActivity.this, "Details saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddLabourDetailsActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulabour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.labourmenu) {
            SharedPreferences sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, LabourLoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
