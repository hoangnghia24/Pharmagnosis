package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
@IgnoreExtraProperties
public class Prescription {
    private String prescriptionId;
    private List<Reminder> reminders;

    public Prescription() {
    }

    public Prescription(String prescriptionId, List<Reminder> reminders) {
        this.prescriptionId = prescriptionId;
        this.reminders = reminders;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
}
