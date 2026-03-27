package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BmiRecord {
    private float weight;
    private float height;
    private float bmi;
    private long timestamp; // Lưu thời gian đo (dạng miliseconds)

    public BmiRecord() {
        // Firebase yêu cầu constructor rỗng
    }

    public BmiRecord(float weight, float height, float bmi, long timestamp) {
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.timestamp = timestamp;
    }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getBmi() { return bmi; }
    public void setBmi(float bmi) { this.bmi = bmi; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}