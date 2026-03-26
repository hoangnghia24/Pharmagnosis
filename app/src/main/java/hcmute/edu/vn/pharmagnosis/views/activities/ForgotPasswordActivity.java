package hcmute.edu.vn.pharmagnosis.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import hcmute.edu.vn.pharmagnosis.databinding.ActivityForgotPasswordBinding;
import hcmute.edu.vn.pharmagnosis.viewmodels.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnResetPassword.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.tilEmail.setError("Vui lòng nhập Email của bạn");
                return;
            } else {
                binding.tilEmail.setError(null);
            }

            // Gọi ViewModel xử lý
            viewModel.handleResetPassword(email);
        });

        // Quay lại trang Đăng nhập
        binding.tvBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnResetPassword.setEnabled(!isLoading);
            binding.btnResetPassword.setText(isLoading ? "ĐANG XỬ LÝ..." : "GỬI LIÊN KẾT ĐẶT LẠI");
        });

        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getIsResetSuccess().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(this, "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn!", Toast.LENGTH_LONG).show();
                // Tùy chọn: Có thể tự động quay về trang login sau khi gửi thành công
                finish();
            }
        });
    }
}