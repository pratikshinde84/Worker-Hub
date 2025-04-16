package com.example.Worker_Hub;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<LabourDetails> userList;
    private final Context context;

    public UserAdapter(Context context, List<LabourDetails> userList) {
        this.context = context;
        this.userList = userList;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        LabourDetails user = userList.get(position);

        holder.tvFullName.setText("Full Name: " + user.getFullName());
        holder.tvMobileNumber.setText("Mobile Number: " + user.getMobileNumber());
        holder.tvGender.setText("Gender: " + user.getGender());
        holder.tvProfession.setText("Profession: " + user.getProfession());
        holder.tvHourlyWage.setText("Hourly Wage: " + user.getHourlyWage());
        holder.tvAddress.setText("Address: " + user.getAddress());
        holder.tvDob.setText("Date of Birth: " + user.getDob());
        holder.tvAboutInfo.setText("About Info: " + user.getAboutInfo());

        String username = user.getId();

        if (username == null || username.isEmpty()) {
            holder.tv_available.setText("Unavailable");
            holder.tv_available.setTextColor(Color.RED); // Default color for missing data
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("LabourDetails")
                .child(username);

        databaseReference.child("isAvailable").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isAvailable = dataSnapshot.getValue(Boolean.class);
                    if (isAvailable != null && isAvailable) {
                        holder.tv_available.setText("Available");
                        holder.tv_available.setTextColor(Color.GREEN);
                    } else {
                        holder.tv_available.setText("Not Available");
                        holder.tv_available.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch availability status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> showBookingDialog(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void showBookingDialog(LabourDetails labourDetails) {
        new AlertDialog.Builder(context)
                .setTitle("Book Labour")
                .setMessage("Do you want to book this labour?\n\n" +
                        "Name: " + labourDetails.getFullName() + "\n" +
                        "Profession: " + labourDetails.getProfession())
                .setPositiveButton("Yes", (dialog, which) -> bookLabour(labourDetails))
                .setNegativeButton("No", null)
                .show();
    }

    private void bookLabour(LabourDetails labourDetails) {
        String labourId = labourDetails.getId();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("LabourDetails")
                .child(labourId);

        databaseReference.child("isAvailable").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Labour booked successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update labour status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvMobileNumber, tvGender, tvProfession, tvHourlyWage, tvAddress, tvDob, tvAboutInfo, tv_available;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvMobileNumber = itemView.findViewById(R.id.tvMobileNumber);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvProfession = itemView.findViewById(R.id.tvProfession);
            tvHourlyWage = itemView.findViewById(R.id.tvHourlyWage);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDob = itemView.findViewById(R.id.tvDob);
            tvAboutInfo = itemView.findViewById(R.id.tvAboutInfo);
            tv_available = itemView.findViewById(R.id.tv_available);
        }
    }
}
