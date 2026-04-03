package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.ENUM.EGender;
import hcmute.edu.vn.pharmagnosis.ENUM.ERole;
import com.google.firebase.database.Exclude;
@IgnoreExtraProperties
public class User {
    private String id;
    private String fullName;
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
    private java.util.Map<String, BmiRecord> bmiHistory;

    public User() {
    }

    public User(String id, String fullName, Date dob, EGender gender, float height, float weight, String email, String password, ERole role, String phone, float bmi) {
        this.id = id;
        this.fullName = fullName;
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

    public User(String id, String fullName, Date dob, EGender gender, float height, float weight, String bloodType, String email, String password, ERole role, String phone, float bmi, List<Allergy> allergies, List<SearchRecord> searchRecords) {
        this.id = id;
        this.fullName = fullName;
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
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullNAme) {
        this.fullName = fullNAme;
    }

    @Exclude
    public Date getDob() {
        return dob;
    }

    @Exclude
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

    public long getDobTimestamp() {
        // Đổi Date thành con số (Milliseconds) để lưu lên Firebase
        return dob != null ? dob.getTime() : 0;
    }

    public void setDobTimestamp(long timestamp) {
        // Đổi con số từ Firebase trả về thành kiểu Date cho Android dùng
        if (timestamp > 0) {
            this.dob = new Date(timestamp);
        }
    }
}