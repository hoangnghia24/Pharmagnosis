package hcmute.edu.vn.pharmagnosis.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hcmute.edu.vn.pharmagnosis.models.User;
import hcmute.edu.vn.pharmagnosis.repositories.UserRepository;

public class ProfileViewModel extends ViewModel {
    private final UserRepository repository;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public ProfileViewModel() {
        repository = new UserRepository();
    }

    public LiveData<User> getUserLiveData() { return userLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getMessage() { return message; }

    // Gọi khi mở màn hình Profile
    public void fetchUser() {
        isLoading.setValue(true);
        repository.getUserProfile(new UserRepository.UserDataCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                message.setValue(errorMessage);
            }
        });
    }

    // Gọi khi bấm nút "Tính nhanh" ở màn hình BMI
    public void saveBmiRecord(float height, float weight, float bmi) {
        isLoading.setValue(true);
        repository.updateBmiAndHistory(height, weight, bmi, new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                message.setValue("Lưu kết quả đo thành công!");
                fetchUser(); // Cập nhật lại dữ liệu mới nhất
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                message.setValue("Lỗi: " + errorMessage);
            }
        });
    }

    public void saveProfileAndHistory(String fullName, String gender, String bloodType, java.util.Date dob, String avatarBase64, float height, float weight, float bmi) {
        isLoading.setValue(true);
        repository.saveProfileWithHistory(fullName, gender, bloodType, dob, avatarBase64, height, weight, bmi, new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                message.setValue("Cập nhật hồ sơ thành công!");
                fetchUser();
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                message.setValue("Lỗi: " + errorMessage);
            }
        });
    }

    public void clearMessage() {
        message.setValue(null);
    }

    // Hàm gọi cập nhật dị ứng
    public void updateAllergiesList(java.util.List<hcmute.edu.vn.pharmagnosis.models.Allergy> allergies) {
        repository.updateAllergies(allergies, new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                fetchUser(); // Cập nhật thành công thì tải lại data cho UI
            }
            @Override
            public void onFailure(String errorMessage) {
                message.setValue("Lỗi cập nhật dị ứng: " + errorMessage);
            }
        });
    }

    public void savePersonalProfile(String fullName, String gender, String bloodType, java.util.Date dob, String avatarBase64) {
        isLoading.setValue(true);
        repository.updatePersonalProfile(fullName, gender, bloodType, dob, avatarBase64, new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                message.setValue("Cập nhật hồ sơ thành công!");
                fetchUser();
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                message.setValue("Lỗi: " + errorMessage);
            }
        });
    }

    public void saveHealthStats(double weight, double bmi) {
        repository.updateHealthStats(weight, bmi, new UserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                fetchUser(); // Tải lại để các màn hình khác (Màn 1) tự cập nhật theo
            }
            @Override
            public void onFailure(String errorMessage) {
                message.setValue(errorMessage);
            }
        });
    }
}