package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
@IgnoreExtraProperties
public class Reminder {
    private String reminderId;
    private LocalDateTime time;
    private int frequency;

    public Reminder() {
    }

    public Reminder(String reminderId, LocalDateTime time, int frequency) {
        this.reminderId = reminderId;
        this.time = time;
        this.frequency = frequency;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
