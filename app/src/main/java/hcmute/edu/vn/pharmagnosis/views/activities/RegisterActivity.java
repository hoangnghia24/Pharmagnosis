package hcmute.edu.vn.pharmagnosis.views.activities;

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

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            registerViewModel.handleRegister(name, email, password, confirmPassword, isTermsAccepted);
        });

        binding.tvLogin.setOnClickListener(v -> finish());
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
                // NÂNG CẤP: Đổi thông báo Toast ở đây
                Toast.makeText(this, "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}