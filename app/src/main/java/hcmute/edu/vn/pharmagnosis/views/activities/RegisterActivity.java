package hcmute.edu.vn.pharmagnosis.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import hcmute.edu.vn.pharmagnosis.databinding.ActivityRegisterBinding;
import hcmute.edu.vn.pharmagnosis.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo binding cho activity_register.xml
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            boolean isTermsAccepted = binding.cbTerms.isChecked();

            // Kiểm tra rỗng cơ bản
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Giao việc cho ViewModel
            registerViewModel.handleRegister(name, email, password, confirmPassword, isTermsAccepted);
        });

        // Nút chuyển về trang Đăng nhập
        binding.tvLogin.setOnClickListener(v -> {
            finish(); // Đóng trang Đăng ký để quay về trang Đăng nhập trước đó
        });
    }

    private void observeViewModel() {
        registerViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnRegister.setEnabled(!isLoading);
            binding.btnRegister.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐĂNG KÝ");
        });

        registerViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        registerViewModel.getIsRegisterSuccess().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                // Quay về trang đăng nhập hoặc vào thẳng App
                finish();
            }
        });
    }
}