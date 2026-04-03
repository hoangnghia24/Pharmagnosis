package hcmute.edu.vn.pharmagnosis.viewmodels.admin.medicines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.repositories.MedicineRepository;

public class AddMedicineViewModel extends ViewModel {
    private final MedicineRepository repository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AddMedicineViewModel() {
        repository = new MedicineRepository();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsSuccess() { return isSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Xóa tham số Uri, chỉ nhận vào model Medicine (đã chứa sẵn ảnh Base64)
    public void saveMedicineToFirebase(Medicine medicine) {
        // Validate cơ bản
        if (medicine.getMedicineName() == null || medicine.getMedicineName().trim().isEmpty()) {
            errorMessage.setValue("Tên thuốc không được để trống!");
            return;
        }

        isLoading.setValue(true);

        // Đổi tên hàm gọi xuống Repository thành saveMedicine (hoặc tên tương đương trong source của bạn)
        // Vì lúc này ta chỉ cần lưu data vào Firestore/Realtime Database chứ không cần upload file lên Storage nữa
        repository.saveMedicine(medicine, task -> {
            isLoading.setValue(false);
            if (task != null && task.isSuccessful()) {
                isSuccess.setValue(true);
            } else {
                errorMessage.setValue("Lưu thuốc thất bại. Vui lòng thử lại!");
            }
        });
    }
}