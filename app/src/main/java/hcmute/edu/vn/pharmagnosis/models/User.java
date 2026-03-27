package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.ENUM.EGender;
import hcmute.edu.vn.pharmagnosis.ENUM.ERole;
@IgnoreExtraProperties
public class User {
    private String id;
    private String fullNAme;
    private Date dob;
    private EGender gender;
    private float height;
    private float weight;
    private String bloodType;
    private String email;
    private String password;
    private ERole role;
    private String phone;
    private float bmi;
    private String avatar;

    private List<Allergy> allergies;
    private List<SearchRecord> searchRecords;
    private List<Prescription> prescriptions;
    private java.util.Map<String, BmiRecord> bmiHistory;

    public User() {
    }

    public User(String id, String fullNAme, Date dob, EGender gender, float height, float weight, String email, String password, ERole role, String phone, float bmi) {
        this.id = id;
        this.fullNAme = fullNAme;
        this.dob = dob;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.bmi = bmi;
    }

    public User(String id, String fullNAme, Date dob, EGender gender, float height, float weight, String bloodType, String email, String password, ERole role, String phone, float bmi, List<Allergy> allergies, List<SearchRecord> searchRecords, List<Prescription> prescriptions) {
        this.id = id;
        this.fullNAme = fullNAme;
        this.dob = dob;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.bmi = bmi;
        this.allergies = allergies;
        this.searchRecords = searchRecords;
        this.prescriptions = prescriptions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullNAme() {
        return fullNAme;
    }

    public void setFullNAme(String fullNAme) {
        this.fullNAme = fullNAme;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public EGender getGender() {
        return gender;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;
    }

    public List<SearchRecord> getSearchRecords() {
        return searchRecords;
    }

    public void setSearchRecords(List<SearchRecord> searchRecords) {
        this.searchRecords = searchRecords;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public void setGender(EGender gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public java.util.Map<String, BmiRecord> getBmiHistory() {
        return bmiHistory;
    }
    public void setBmiHistory(java.util.Map<String, BmiRecord> bmiHistory) {
        this.bmiHistory = bmiHistory;
    }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}