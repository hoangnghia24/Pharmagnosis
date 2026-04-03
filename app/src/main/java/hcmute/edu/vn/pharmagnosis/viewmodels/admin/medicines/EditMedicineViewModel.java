package hcmute.edu.vn.pharmagnosis.viewmodels.admin.medicines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.repositories.MedicineRepository;

public class EditMedicineViewModel extends ViewModel {
    private final MedicineRepository repository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public EditMedicineViewModel() {
        repository = new MedicineRepository();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsSuccess() { return isSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Xóa bỏ tham số Uri imageUri, chỉ nhận object Medicine (đã chứa chuỗi ảnh Base64)
    public void updateMedicineToFirebase(Medicine medicine) {
        // Validate cơ bản
        if (medicine == null || medicine.getMedicineId() == null) {
            errorMessage.setValue("Dữ liệu thuốc không hợp lệ!");
            return;
        }

        if (medicine.getMedicineName() == null || medicine.getMedicineName().trim().isEmpty()) {
            errorMessage.setValue("Tên thuốc không được để trống!");
            return;
        }

        isLoading.setValue(true);

        // Gọi thẳng hàm updateMedicine trong Repository để lưu lên Firestore
        repository.updateMedicine(medicine, task -> {
            isLoading.setValue(false);
            if (task != null && task.isSuccessful()) {
                isSuccess.setValue(true); // Cập nhật thành công
            } else {
                errorMessage.setValue("Cập nhật thuốc thất bại. Vui lòng thử lại!");
            }
        });
    }
}