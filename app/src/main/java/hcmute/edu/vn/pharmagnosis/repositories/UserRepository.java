package hcmute.edu.vn.pharmagnosis.repositories;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.pharmagnosis.models.BmiRecord;
import hcmute.edu.vn.pharmagnosis.models.User;

public class UserRepository {
    private final DatabaseReference userRef;
    private final FirebaseAuth auth;

    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        // Trỏ vào node "Users" trên Firebase Realtime Database
        userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public interface UserDataCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public interface ActionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // Tiện ích lấy UID an toàn
    private String getCurrentUid() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    // 1. LẤY THÔNG TIN USER (Dùng cho Màn 1: Xem hồ sơ)
    public void getUserProfile(UserDataCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onFailure("Người dùng chưa đăng nhập");
            return;
        }

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("Hồ sơ chưa được tạo.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // 2. CẬP NHẬT CHIỀU CAO, CÂN NẶNG & THÊM VÀO LỊCH SỬ (Dùng cho Màn 2: Tính BMI)
    public void updateBmiAndHistory(float height, float weight, float bmi, ActionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) return;

        // Bước A: Ghi đè chỉ số hiện tại
        Map<String, Object> updates = new HashMap<>();
        updates.put("height", height);
        updates.put("weight", weight);
        updates.put("bmi", bmi);

        userRef.child(uid).updateChildren(updates).addOnSuccessListener(aVoid -> {

            // Bước B: Push thêm 1 record mới vào lịch sử (bmiHistory)
            DatabaseReference historyRef = userRef.child(uid).child("bmiHistory").push();
            BmiRecord newRecord = new BmiRecord(weight, height, bmi, System.currentTimeMillis());

            historyRef.setValue(newRecord)
                    .addOnSuccessListener(aVoid1 -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));

        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // 3. CẬP NHẬT THÔNG TIN CÁ NHÂN (Dùng cho Màn hình Chỉnh sửa hồ sơ)
    public void updatePersonalProfile(String fullName, String gender, String bloodType, java.util.Date dob, ActionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullNAme", fullName);
        updates.put("gender", gender);
        updates.put("bloodType", bloodType);
        if (dob != null) {
            updates.put("dob", dob); // Lưu Ngày sinh lên Firebase
        }

        userRef.child(uid).updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // 4. CẬP NHẬT DANH SÁCH DỊ ỨNG
    public void updateAllergies(java.util.List<hcmute.edu.vn.pharmagnosis.models.Allergy> allergies, ActionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) return;

        // Ghi đè toàn bộ danh sách dị ứng mới lên Firebase
        userRef.child(uid).child("allergies").setValue(allergies)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updatePersonalProfile(String fullName, String gender, String bloodType, java.util.Date dob, String avatarBase64, ActionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) return;

        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("fullNAme", fullName);
        updates.put("gender", gender);
        updates.put("bloodType", bloodType);
        if (dob != null) {
            updates.put("dobTimestamp", dob.getTime()); // Lưu dạng số Long an toàn
            updates.put("dob", null); // Xóa cái object lỗi cũ trên DB đi cho sạch
        }
        if (avatarBase64 != null) updates.put("avatar", avatarBase64); // Lưu ảnh

        userRef.child(uid).updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateHealthStats(double weight, double bmi, ActionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("weight", weight);
        updates.put("bmi", bmi);

        userRef.child(uid).updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // =========================================================================
    // 5. [HÀM MỚI] LƯU GỘP TOÀN BỘ HỒ SƠ VÀ THÊM LỊCH SỬ BMI TỪ MÀN CHỈNH SỬA
    // =========================================================================
    public void saveProfileWithHistory(String fullName, String gender, String bloodType, java.util.Date dob, String avatarBase64, float height, float weight, float bmi, ActionCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) return;

        // Cập nhật các trường thông tin cơ bản
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullNAme", fullName);
        updates.put("gender", gender);
        updates.put("bloodType", bloodType);
        if (dob != null) updates.put("dob", dob);
        if (avatarBase64 != null) updates.put("avatar", avatarBase64);

        // Cập nhật luôn Chiều cao, Cân nặng, BMI mới nhất
        updates.put("height", height);
        updates.put("weight", weight);
        updates.put("bmi", bmi);

        userRef.child(uid).updateChildren(updates).addOnSuccessListener(aVoid -> {
            // Nếu có dữ liệu sức khỏe hợp lệ -> Đẩy thêm vào History
            if (weight > 0 && height > 0) {
                DatabaseReference historyRef = userRef.child(uid).child("bmiHistory").push();
                BmiRecord newRecord = new BmiRecord(weight, height, bmi, System.currentTimeMillis());

                historyRef.setValue(newRecord)
                        .addOnSuccessListener(aVoid1 -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            } else {
                callback.onSuccess(); // Trả về thành công nếu không có chiều cao/cân nặng
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}