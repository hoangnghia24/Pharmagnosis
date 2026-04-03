package hcmute.edu.vn.pharmagnosis.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

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

                    if (user != null) {
                        String uid = user.getUid();
                        // Chọc vào Database để lấy Role trước khi quyết định
                        DatabaseReference roleRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("role");

                        roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Nếu không có role (tài khoản mới), mặc định coi là USER
                                String role = snapshot.exists() ? snapshot.getValue(String.class) : "USER";

                                if ("ADMIN".equals(role)) {
                                    // 1. LÀ ADMIN: Đặc quyền VIP, cho vào thẳng ứng dụng không cần check mail
                                    callback.onSuccess();
                                } else {
                                    // 2. LÀ USER BÌNH THƯỜNG: Bắt buộc phải xác thực Email
                                    if (user.isEmailVerified()) {
                                        callback.onSuccess(); // Đã xác thực -> Cho vào
                                    } else {
                                        firebaseAuth.signOut(); // Chưa xác thực -> Đuổi ra
                                        callback.onFailure("Vui lòng kiểm tra hộp thư và xác thực email trước khi đăng nhập!");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                firebaseAuth.signOut();
                                callback.onFailure("Lỗi hệ thống khi kiểm tra quyền: " + error.getMessage());
                            }
                        });

                    } else {
                        callback.onFailure("Lỗi: Không lấy được thông tin phiên đăng nhập!");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Đăng nhập thất bại: Sai email hoặc mật khẩu!"));
    }

    // CẬP NHẬT: Thêm tham số name để lưu vào Database
    public void registerUser(String name, String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        // Lưu thông tin cơ bản vào Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                        
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fullName", name); // Lưu đúng tên field trong Model User là "fullName"
                        userData.put("email", email);
                        userData.put("role", "USER");

                        userRef.setValue(userData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.sendEmailVerification()
                                        .addOnSuccessListener(aVoid -> {
                                            firebaseAuth.signOut();
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> callback.onFailure("Tạo tài khoản thành công nhưng lỗi gửi email xác thực."));
                            } else {
                                callback.onFailure("Lỗi lưu thông tin: " + task.getException().getMessage());
                            }
                        });
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

    // Thêm hàm Callback để trả về Role
    public interface RoleCallback {
        void onRoleFetched(String role);
        void onFailure(String errorMessage);
    }

    // Hàm lấy Role từ Realtime Database
    public void fetchUserRole(RoleCallback callback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Không tìm thấy thông tin đăng nhập");
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.child("role").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String roleStr = snapshot.getValue(String.class);
                    callback.onRoleFetched(roleStr); // Trả về "ADMIN" hoặc "USER"
                } else {
                    // Nếu user mới tạo chưa kịp có role, mặc định cho là USER
                    callback.onRoleFetched("USER");
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}