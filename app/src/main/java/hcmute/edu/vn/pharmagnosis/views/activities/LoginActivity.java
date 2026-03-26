package hcmute.edu.vn.pharmagnosis.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import hcmute.edu.vn.pharmagnosis.databinding.ActivityLoginBinding;
import hcmute.edu.vn.pharmagnosis.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        // Xử lý sự kiện nhấn nút Đăng nhập
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Kiểm tra rỗng tại View
            if (TextUtils.isEmpty(email)) {
                binding.tilEmail.setError("Vui lòng nhập Email");
                return;
            } else {
                binding.tilEmail.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                binding.tilPassword.setError("Vui lòng nhập Mật khẩu");
                return;
            } else {
                binding.tilPassword.setError(null);
            }

            // Giao việc cho ViewModel
            loginViewModel.handleLogin(email, password);
        });

        // Chuyển sang màn hình Đăng ký
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Quên mật khẩu
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        // Quan sát trạng thái Loading để khóa/mở nút bấm
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐĂNG NHẬP");
        });

        // Quan sát lỗi từ Firebase gửi về
        loginViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        // Quan sát thành công
        loginViewModel.getIsLoginSuccess().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                // TODO: Dùng Intent để chuyển sang màn hình HomeActivity tại đây
            }
        });
    }
}