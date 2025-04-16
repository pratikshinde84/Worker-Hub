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
import androidx.appcompat.widget.SearchView;
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
    private List<LabourDetails> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        sharedPreferences = getSharedPreferences("WorkerHubPrefs", MODE_PRIVATE);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        userAdapter = new UserAdapter(this, filteredList); // Use filteredList for displaying data
        recyclerView.setAdapter(userAdapter);

        // Fetch user data from Firebase
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
                filteredList.clear();
                filteredList.addAll(userList); // Initially, show all data
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuowner, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("Search Labour...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterData(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterData(newText);
                    return true;
                }
            });
        }

        return true;
    }

    private void filterData(String query) {
        filteredList.clear(); // Clear the filtered list before adding new filtered results

        if (query.isEmpty()) {
            filteredList.addAll(userList); // If query is empty, show all users
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (LabourDetails user : userList) {
                // Check if the user details (full name or profession) match the query
                if (user.getFullName() != null && user.getProfession() != null) {
                    if (user.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                            user.getProfession().toLowerCase().contains(lowerCaseQuery)) {
                        filteredList.add(user); // Add matching user to filtered list
                    }
                }
            }
        }

        userAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ownermenu) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isOwnerLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(this, OwnerLoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
