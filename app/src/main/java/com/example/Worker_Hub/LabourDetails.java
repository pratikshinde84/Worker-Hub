package com.example.Worker_Hub;

public class LabourDetails {
    private String id;
    private String fullName;
    private String mobileNumber;
    private String address;
    private String hourlyWage;
    private String dob;
    private String profession;
    private String aboutInfo;
    private String gender;

    public LabourDetails() {
        // Default constructor required for Firebase
    }

    public LabourDetails(String id, String fullName, String mobileNumber, String address, String hourlyWage, String dob, String profession, String aboutInfo, String gender) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.hourlyWage = hourlyWage;
        this.dob = dob;
        this.profession = profession;
        this.aboutInfo = aboutInfo;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getHourlyWage() {
        return hourlyWage;
    }

    public String getDob() {
        return dob;
    }

    public String getProfession() {
        return profession;
    }

    public String getAboutInfo() {
        return aboutInfo;
    }

    public String getGender() {
        return gender;
    }
}
