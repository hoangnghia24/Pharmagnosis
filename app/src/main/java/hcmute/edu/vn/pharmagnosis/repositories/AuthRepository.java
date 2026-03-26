package hcmute.edu.vn.pharmagnosis.repositories;

import com.google.firebase.auth.FirebaseAuth;
import hcmute.edu.vn.pharmagnosis.di.FirebaseModule;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        // Lấy instance từ thư mục di theo đúng quy ước
        this.firebaseAuth = FirebaseModule.provideFirebaseAuth();
    }

    // Interface để trả kết quả về cho ViewModel
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Firebase báo đăng nhập thành công
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Firebase báo lỗi (sai pass, tài khoản không tồn tại...)
                    callback.onFailure(e.getMessage());
                });
    }

    public void registerUser(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Firebase báo tạo tài khoản thành công
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Lỗi (email đã tồn tại, pass quá ngắn...)
                    callback.onFailure(e.getMessage());
                });
    }
}