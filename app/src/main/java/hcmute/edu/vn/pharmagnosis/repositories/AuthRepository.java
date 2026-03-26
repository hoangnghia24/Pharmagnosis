package hcmute.edu.vn.pharmagnosis.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hcmute.edu.vn.pharmagnosis.di.FirebaseModule;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.firebaseAuth = FirebaseModule.provideFirebaseAuth();
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // NÂNG CẤP: Kiểm tra Xác thực Email trước khi cho Đăng nhập
    public void loginUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        callback.onSuccess(); // Hợp lệ, cho phép vào App
                    } else {
                        firebaseAuth.signOut(); // Ép đăng xuất
                        callback.onFailure("Vui lòng kiểm tra hộp thư và xác thực email trước khi đăng nhập!");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // NÂNG CẤP: Gửi Email xác thực sau khi tạo tài khoản thành công
    public void registerUser(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification()
                                .addOnSuccessListener(aVoid -> {
                                    firebaseAuth.signOut(); // Ép đăng xuất ngay lập tức
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> callback.onFailure("Tạo tài khoản thành công nhưng lỗi gửi email xác thực."));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // GIỮ NGUYÊN: Gửi liên kết đặt lại mật khẩu
    public void resetPassword(String email, AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}