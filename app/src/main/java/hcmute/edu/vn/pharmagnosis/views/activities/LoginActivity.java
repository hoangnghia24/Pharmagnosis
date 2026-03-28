package hcmute.edu.vn.pharmagnosis.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import hcmute.edu.vn.pharmagnosis.databinding.ActivityLoginBinding;
import hcmute.edu.vn.pharmagnosis.viewmodels.LoginViewModel;
import hcmute.edu.vn.pharmagnosis.ENUM.ERole; // Import Enum phân quyền

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

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

            loginViewModel.handleLogin(email, password);
        });

        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐĂNG NHẬP");
        });

        loginViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        // 1. Khi Đăng nhập Email/Password thành công -> Đi lấy phân quyền (Role)
        loginViewModel.getIsLoginSuccess().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(this, "Đăng nhập thành công! Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
                // Gọi ViewModel để chọc lên Firebase hỏi Role
                loginViewModel.fetchRoleAfterLogin();
            }
        });

        // 2. Lắng nghe kết quả Role trả về -> Bẻ lái luồng chuyển trang
        loginViewModel.getUserRole().observe(this, role -> {
            if (role != null) {
                Intent intent;

                // Nếu là ADMIN -> Chuyển vào luồng quản trị
                if (role.equals(ERole.ADMIN.name())) {
                    // Lưu ý: Đảm bảo bạn đã tạo file AdminMainActivity nhé. Nếu tên khác thì sửa lại ở đây.
                    intent = new Intent(LoginActivity.this, hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment.class);
                }
                // Nếu là USER hoặc không có quyền đặc biệt -> Chuyển vào ứng dụng chính
                else {
                    intent = new Intent(LoginActivity.this, hcmute.edu.vn.pharmagnosis.MainActivity.class);
                }

                startActivity(intent);
                finish(); // Đóng màn hình Login lại để user không back lại được
            }
        });
    }
}