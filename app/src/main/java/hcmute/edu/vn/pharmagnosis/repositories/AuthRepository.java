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

    public void loginUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    // Kiểm tra xem user có null không và đã xác thực email chưa
                    if (user != null && user.isEmailVerified()) {
                        callback.onSuccess(); // Cho phép đăng nhập
                    } else {
                        // Nếu chưa xác thực, ép đăng xuất để bảo mật và báo lỗi
                        firebaseAuth.signOut();
                        callback.onFailure("Vui lòng kiểm tra hộp thư và xác thực email trước khi đăng nhập!");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    public void registerUser(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // Gửi email xác thực
                        user.sendEmailVerification()
                                .addOnSuccessListener(aVoid -> {
                                    // Gửi thành công -> Ép đăng xuất để họ không lọt vào app -> Báo ViewModel thành công
                                    firebaseAuth.signOut();
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    callback.onFailure("Tạo tài khoản thành công nhưng không thể gửi email xác thực.");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    public void resetPassword(String email, AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}