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
    private Button btnRegister,btnStatus;
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
        btnStatus=findViewById(R.id.btnStatus);

        databaseReference = FirebaseDatabase.getInstance().getReference("LabourDetails");

        SharedPreferences sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", "default_user");

        if (username.equals("default_user")) {
            Toast.makeText(this, "User not logged in. Please log in to continue.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddLabourDetailsActivity.this);
                builder.setTitle("Set Status");

                String[] statusOptions = {"Available", "Not Available"};
                builder.setSingleChoiceItems(statusOptions, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get selected status
                        String selectedStatus = statusOptions[which];
                        boolean isAvailable = selectedStatus.equals("Available");

                        // Retrieve username from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
                        String username = sharedPreferences.getString("loggedInUsername", "default_user");

                        if (username.equals("default_user")) {
                            Toast.makeText(AddLabourDetailsActivity.this, "User not logged in. Please log in to continue.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }

                        // Update isAvailable in Firebase
                        databaseReference.child(username).child("isAvailable").setValue(isAvailable)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddLabourDetailsActivity.this, "Status updated to: " + selectedStatus, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddLabourDetailsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // Close the dialog
                        dialog.dismiss();
                    }
                });

                // Add a cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog without any action
                    }
                });

                // Show the dialog
                builder.create().show();
            }
        });



        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LabourDetails labourDetails = dataSnapshot.getValue(LabourDetails.class);
                    if (labourDetails != null) {
                        // Populate EditText fields with retrieved data
                        etFullName.setText(labourDetails.getFullName());
                        etMobileNumber.setText(labourDetails.getMobileNumber());
                        etAddress.setText(labourDetails.getAddress());
                        etHourlyWage.setText(labourDetails.getHourlyWage());
                        etDob.setText(labourDetails.getDob());
                        etProfession.setText(labourDetails.getProfession());
                        etAboutInfo.setText(labourDetails.getAboutInfo());

                        if (labourDetails.getGender().equals("Male")) {
                            ((RadioButton) radioGroupGender.getChildAt(0)).setChecked(true); // Male
                        } else if (labourDetails.getGender().equals("Female")) {
                            ((RadioButton) radioGroupGender.getChildAt(1)).setChecked(true); // Female
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddLabourDetailsActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up DatePicker for DOB
        etDob.setOnClickListener(v -> showDatePickerDialog());

        // Register button listener
        btnRegister.setOnClickListener(v -> registerLabourDetails());
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void registerLabourDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", "default_user");

        if (username.equals("default_user")) {
            Toast.makeText(this, "User not logged in. Please log in to continue.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String hourlyWage = etHourlyWage.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String profession = etProfession.getText().toString().trim();
        String aboutInfo = etAboutInfo.getText().toString().trim();

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String gender = selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            return;
        }
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 10) {
            etMobileNumber.setError("Valid mobile number is required");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            return;
        }
        if (TextUtils.isEmpty(hourlyWage)) {
            etHourlyWage.setError("Hourly wage is required");
            return;
        }
        if (TextUtils.isEmpty(dob)) {
            etDob.setError("Date of birth is required");
            return;
        }
        if (TextUtils.isEmpty(profession)) {
            etProfession.setError("Profession is required");
            return;
        }
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        LabourDetails labourDetails = new LabourDetails(username, fullName, mobileNumber, address, hourlyWage, dob, profession, aboutInfo, gender);

        // Save details under the user's node
        databaseReference.child(username).setValue(labourDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Labour details updated successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Failed to update labour details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        etFullName.setText("");
        etMobileNumber.setText("");
        etAddress.setText("");
        etHourlyWage.setText("");
        etDob.setText("");
        etProfession.setText("");
        etAboutInfo.setText("");
        radioGroupGender.clearCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menulabour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.labourmenu) {
            SharedPreferences sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            finish();
        }
        return true;
    }
}
