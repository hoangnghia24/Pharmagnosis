package hcmute.edu.vn.pharmagnosis.views.activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hcmute.edu.vn.pharmagnosis.databinding.ActivitySplashBinding;
import hcmute.edu.vn.pharmagnosis.di.FirebaseModule;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng ViewBinding
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startAnimations();
    }

    private void startAnimations() {
        // 1. Cài đặt trạng thái ban đầu (Ẩn đi và đẩy xuống dưới một chút)
        binding.cvLogoSplash.setAlpha(0f);
        binding.cvLogoSplash.setTranslationY(100f);

        binding.tvAppNameSplash.setAlpha(0f);
        binding.tvAppNameSplash.setTranslationY(50f);

        binding.tvSloganSplash.setAlpha(0f);
        binding.tvSloganSplash.setTranslationY(50f);

        binding.pbSplash.setAlpha(0f);

        // 2. Chạy chuỗi hiệu ứng (Animation)
        // Hiệu ứng Logo bay lên và hiện rõ (Kéo dài 1 giây)
        binding.cvLogoSplash.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .setStartDelay(200)
                .start();

        // Hiệu ứng Tên App xuất hiện
        binding.tvAppNameSplash.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .setStartDelay(600)
                .start();

        // Hiệu ứng Slogan xuất hiện
        binding.tvSloganSplash.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .setStartDelay(800)
                .start();

        // Hiệu ứng vòng Loading hiện lên cuối cùng và bắt đầu kiểm tra Auth
        binding.pbSplash.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(1200)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Khi mọi hiệu ứng bay nhảy kết thúc -> Mới tiến hành kiểm tra đăng nhập
                        checkUserAuthentication();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                })
                .start();
    }

    private void checkUserAuthentication() {
        FirebaseAuth firebaseAuth = FirebaseModule.provideFirebaseAuth();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Chuẩn bị sẵn Intent chuyển sang Login (Dành cho trường hợp chưa đăng nhập)
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);

        // KIỂM TRA ĐĂNG NHẬP
        if (currentUser != null && currentUser.isEmailVerified()) {

            // CẬP NHẬT: ĐÃ ĐĂNG NHẬP & XÁC THỰC -> CHUYỂN THẲNG VÀO TRANG CHỦ (MainActivity)
            Intent homeIntent = new Intent(SplashActivity.this, hcmute.edu.vn.pharmagnosis.MainActivity.class);
            startActivity(homeIntent);
            finish();

        } else {
            // Chưa đăng nhập hoặc chưa xác thực email -> Ép đăng xuất & về Login
            if (currentUser != null && !currentUser.isEmailVerified()) {
                firebaseAuth.signOut();
            }

            // CẬP NHẬT: KÍCH HOẠT HIỆU ỨNG LOGO BAY (Shared Element Transition)
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    SplashActivity.this,
                    binding.cvLogoSplash, // View hiện tại ở Splash
                    "logo_transition"     // Sợi dây tàng hình đã đặt trong XML
            );

            // Bắt đầu chuyển sang Login kèm theo hiệu ứng bay
            startActivity(loginIntent, options.toBundle());

            // Lưu ý: Dùng Handler chờ 1 giây (để logo bay xong) rồi mới đóng màn hình Splash
            new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1000);
        }
    }
}