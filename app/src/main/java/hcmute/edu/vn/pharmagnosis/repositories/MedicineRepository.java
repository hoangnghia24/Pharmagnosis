package hcmute.edu.vn.pharmagnosis.repositories;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.models.Medicine;

public class MedicineRepository {

    private FirebaseFirestore db;

    public MedicineRepository() {
        db = FirebaseFirestore.getInstance();
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
}