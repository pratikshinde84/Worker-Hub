package com.example.Worker_Hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnerDashboardActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<LabourDetails> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>(); // Initialize userList
        userAdapter = new UserAdapter(this, userList); // Initialize adapter
        recyclerView.setAdapter(userAdapter); // Set the adapter to RecyclerView

        fetchUserData();
    }

    private void fetchUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LabourDetails");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LabourDetails user = dataSnapshot.getValue(LabourDetails.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }


                // Notify the adapter that data has changed
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OwnerDashboardActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu (excluding search functionality)
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuowner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle logout action
        if (item.getItemId() == R.id.ownermenu) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isOwnerLoggedIn", false);
            editor.apply();

            // Navigate back to the login activity
            Intent intent = new Intent(this, OwnerLoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}

