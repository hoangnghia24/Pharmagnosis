package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
@IgnoreExtraProperties
public class Desease {
    private String deseaseId;
    private String name;
    private String description;
    private List<Symptom> symptoms;

    public Desease() {
    }

    public Desease(String deseaseId, String name, String description, List<Symptom> symptoms) {
        this.deseaseId = deseaseId;
        this.name = name;
        this.description = description;
        this.symptoms = symptoms;
    }

    public String getDeseaseId() {
        return deseaseId;
    }

    public void setDeseaseId(String deseaseId) {
        this.deseaseId = deseaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }
}
