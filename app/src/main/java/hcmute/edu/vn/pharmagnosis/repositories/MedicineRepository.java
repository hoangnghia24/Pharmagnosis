package hcmute.edu.vn.pharmagnosis.repositories;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hcmute.edu.vn.pharmagnosis.models.Medicine;

public class MedicineRepository {

    private FirebaseFirestore db;
    private final FirebaseStorage storage;
    public MedicineRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Hàm lấy toàn bộ danh sách thuốc từ Firebase về
    public MutableLiveData<List<Medicine>> getAllMedicines() {
        MutableLiveData<List<Medicine>> medicineLiveData = new MutableLiveData<>();

        // Tìm đúng cái collection "medicines" mà bạn vừa tạo
        db.collection("medicines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Medicine> medicineList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Firestore sẽ tự động chuyển đổi cả các Array thành List<String> cho bạn
                            Medicine medicine = document.toObject(Medicine.class);
                            medicineList.add(medicine);
                        }
                        medicineLiveData.setValue(medicineList);
                    } else {
                        Log.e("FirebaseError", "Lỗi khi tải danh sách thuốc: ", task.getException());
                        medicineLiveData.setValue(new ArrayList<>());
                    }
                });

        return medicineLiveData;
    }


    public void addMedicine(Medicine medicine, OnCompleteListener<Void> listener) {
        String docId = db.collection("medicines").document().getId();
        medicine.setMedicineId(docId);

        db.collection("medicines").document(docId)
                .set(medicine)
                .addOnCompleteListener(listener);
    }
    public void uploadImageAndSaveMedicine(Uri imageUri, Medicine medicine, OnCompleteListener<Void> listener) {
        if (imageUri != null) {
            // Tạo tên file ngẫu nhiên tránh trùng lặp
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storage.getReference().child("medicine_images/" + fileName);

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Upload ảnh thành công, tiến hành lấy URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Gán URL ảnh vào model
                    medicine.setImage(uri.toString());
                    // Gọi hàm lưu vào Firestore
                    addMedicine(medicine, listener);
                }).addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Lỗi lấy URL ảnh", e);
                    listener.onComplete(null); // Báo lỗi
                });
            }).addOnFailureListener(e -> {
                Log.e("FirebaseError", "Lỗi upload ảnh", e);
                listener.onComplete(null); // Báo lỗi
            });
        } else {
            // Nếu không có ảnh, lưu dữ liệu bình thường
            addMedicine(medicine, listener);
        }
    }
    // Hàm cập nhật thuốc đã có
    public void updateMedicine(Medicine medicine, OnCompleteListener<Void> listener) {
        if (medicine.getMedicineId() != null) {
            db.collection("medicines").document(medicine.getMedicineId())
                    .set(medicine)
                    .addOnCompleteListener(listener);
        }
    }

    public void deleteMedicine(String medicineId, OnCompleteListener<Void> listener) {
        if (medicineId != null) {
            db.collection("medicines").document(medicineId)
                    .delete()
                    .addOnCompleteListener(listener);
        }
    }
    // Hàm mới: Upload ảnh mới lên Storage, lấy URL rồi mới gọi updateMedicine
    public void uploadImageAndUpdateMedicine(android.net.Uri imageUri, Medicine medicine, OnCompleteListener<Void> listener) {
        if (imageUri != null) {
            // Tạo tên file ngẫu nhiên để không bị trùng
            String fileName = java.util.UUID.randomUUID().toString() + ".jpg";
            com.google.firebase.storage.StorageReference imageRef = storage.getReference().child("medicine_images/" + fileName);

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Upload thành công, tiến hành lấy URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Cập nhật lại URL ảnh mới vào object Medicine
                    medicine.setImage(uri.toString());

                    // Gọi hàm updateMedicine có sẵn để lưu vào Firestore
                    updateMedicine(medicine, listener);
                }).addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Lỗi lấy URL ảnh", e);
                    listener.onComplete(null);
                });
            }).addOnFailureListener(e -> {
                Log.e("FirebaseError", "Lỗi upload ảnh", e);
                listener.onComplete(null);
            });
        }
    }
}