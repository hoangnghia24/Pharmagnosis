package hcmute.edu.vn.pharmagnosis.repositories;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.models.Medicine;

public class MedicineRepository {

    private final FirebaseFirestore db;

    // Đã xóa FirebaseStorage vì giờ đây ảnh Base64 được lưu trực tiếp như một String vào Firestore

    public MedicineRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Hàm lấy toàn bộ danh sách thuốc từ Firebase về
    public MutableLiveData<List<Medicine>> getAllMedicines() {
        MutableLiveData<List<Medicine>> medicineLiveData = new MutableLiveData<>();

        // Tìm đúng collection "medicines"
        db.collection("medicines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Medicine> medicineList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Firestore tự động chuyển đổi cả các Array thành List<String>
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

    // Đã đổi tên thành saveMedicine để khớp với lệnh gọi trong AddMedicineViewModel
    public void saveMedicine(Medicine medicine, OnCompleteListener<Void> listener) {
        String docId = db.collection("medicines").document().getId();
        medicine.setMedicineId(docId); // Gán ID document cho model trước khi lưu

        // Lưu object Medicine (đã bao gồm chuỗi Base64 của ảnh) vào Firestore
        db.collection("medicines").document(docId)
                .set(medicine)
                .addOnCompleteListener(listener);
    }

    // Hàm cập nhật thuốc đã có
    public void updateMedicine(Medicine medicine, OnCompleteListener<Void> listener) {
        if (medicine.getMedicineId() != null) {
            // Tương tự, nếu có sửa ảnh thì object Medicine truyền vào đây cũng đã có Base64 mới
            db.collection("medicines").document(medicine.getMedicineId())
                    .set(medicine)
                    .addOnCompleteListener(listener);
        }
    }

    // Hàm xóa thuốc
    public void deleteMedicine(String medicineId, OnCompleteListener<Void> listener) {
        if (medicineId != null) {
            db.collection("medicines").document(medicineId)
                    .delete()
                    .addOnCompleteListener(listener);
        }
    }
}