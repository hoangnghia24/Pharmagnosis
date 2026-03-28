package hcmute.edu.vn.pharmagnosis.views.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.pharmagnosis.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo giao diện bằng ViewBinding
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tạm thời hiển thị giao diện lên trước.
        // Logic điều hướng Bottom Navigation của Admin chúng ta sẽ làm sau nhé!
    }
}