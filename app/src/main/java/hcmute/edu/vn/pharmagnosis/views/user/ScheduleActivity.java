package hcmute.edu.vn.pharmagnosis.views.user;

import android.app.Activity;
import android.os.Bundle;

import hcmute.edu.vn.pharmagnosis.R;

public class ScheduleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // gọi giao diện XML
        setContentView(R.layout.activity_medication_schedule);
    }

}
