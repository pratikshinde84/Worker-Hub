package com.example.Worker_Hub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<LabourDetails> userList;

    public UserAdapter(List<LabourDetails> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvMobileNumber, tvGender, tvProfession, tvHourlyWage, tvAddress, tvDob, tvAboutInfo;

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
        }
    }
}
