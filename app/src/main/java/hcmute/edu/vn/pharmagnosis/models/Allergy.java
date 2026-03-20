package hcmute.edu.vn.pharmagnosis.models;

import java.util.List;

import hcmute.edu.vn.pharmagnosis.ENUM.ESeverity;

public class Allergy {
    private String allergyId;
    private String allergenName;
    private ESeverity severity;
    private List<Symptom> symptoms;
}
