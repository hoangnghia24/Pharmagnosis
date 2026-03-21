package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
@IgnoreExtraProperties
public class Symptom {
    private String symptomId;
    private String name;
    private List<String> bodyPart;

    public Symptom() {
    }

    public Symptom(String symptomId, String name, List<String> bodyPart) {
        this.symptomId = symptomId;
        this.name = name;
        this.bodyPart = bodyPart;
    }

    public String getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(String symptomId) {
        this.symptomId = symptomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(List<String> bodyPart) {
        this.bodyPart = bodyPart;
    }
}
