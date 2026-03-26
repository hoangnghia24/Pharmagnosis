package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

import hcmute.edu.vn.pharmagnosis.ENUM.ESeverity;
@IgnoreExtraProperties
public class Allergy {
    private String allergyId;
    private String allergenName;
    private ESeverity severity;
    private List<Symptom> symptoms;

    public Allergy() {
    }

    public Allergy(String allergyId, String allergenName, ESeverity severity, List<Symptom> symptoms) {
        this.allergyId = allergyId;
        this.allergenName = allergenName;
        this.severity = severity;
        this.symptoms = symptoms;
    }

    public String getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(String allergyId) {
        this.allergyId = allergyId;
    }

    public String getAllergenName() {
        return allergenName;
    }

    public void setAllergenName(String allergenName) {
        this.allergenName = allergenName;
    }

    public ESeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ESeverity severity) {
        this.severity = severity;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }
}
