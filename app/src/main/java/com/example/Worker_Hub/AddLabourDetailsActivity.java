package com.example.Worker_Hub;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddLabourDetailsActivity extends AppCompatActivity {

    private EditText etFullName, etMobileNumber, etAddress, etHourlyWage, etDob, etProfession, etAboutInfo;
    private RadioGroup radioGroupGender;
    private Button btnRegister;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_labour_details);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddress = findViewById(R.id.etAddress);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        etDob = findViewById(R.id.etDob);
        etProfession = findViewById(R.id.etProfession);
        etAboutInfo = findViewById(R.id.etAboutInfo);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("LabourDetails");

        // Set up DatePicker for DOB
        etDob.setOnClickListener(v -> showDatePickerDialog());

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
                    // Format the selected date and set it in the EditText
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDob.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void registerLabourDetails() {
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

        String labourId = databaseReference.push().getKey();
        LabourDetails labourDetails = new LabourDetails(labourId, fullName, mobileNumber, address, hourlyWage, dob, profession, aboutInfo, gender);

        if (labourId != null) {
            databaseReference.child(labourId).setValue(labourDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Labour details registered successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(this, "Failed to register labour details", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
