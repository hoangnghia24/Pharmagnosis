package hcmute.edu.vn.pharmagnosis.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import hcmute.edu.vn.pharmagnosis.repositories.AuthRepository;

public class RegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRegisterSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RegisterViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsRegisterSuccess() { return isRegisterSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void handleRegister(String name, String email, String password, String confirmPassword, boolean isTermsAccepted) {
        // Validation logic
        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập họ tên!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Mật khẩu xác nhận không khớp!");
            return;
        }

        if (!isTermsAccepted) {
            errorMessage.setValue("Bạn cần đồng ý với các Điều khoản & Chính sách!");
            return;
        }

        isLoading.setValue(true);

        // CẬP NHẬT: Truyền name vào hàm registerUser
        authRepository.registerUser(name, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                isRegisterSuccess.setValue(true);
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                errorMessage.setValue(errorMsg);
            }
        });
    }
}