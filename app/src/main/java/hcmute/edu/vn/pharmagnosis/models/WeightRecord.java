package hcmute.edu.vn.pharmagnosis.models;

public class WeightRecord {
    private String id;
    private double weight;
    private double bmi;
    private long timestamp; // Lưu ngày dưới dạng Long để sau này vẽ biểu đồ

    public WeightRecord() {} // Cần thiết cho Firebase

    public WeightRecord(String id, double weight, double bmi, long timestamp) {
        this.id = id;
        this.weight = weight;
        this.bmi = bmi;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}