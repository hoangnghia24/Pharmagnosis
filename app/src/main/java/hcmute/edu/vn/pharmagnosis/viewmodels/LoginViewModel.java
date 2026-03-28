package hcmute.edu.vn.pharmagnosis.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import hcmute.edu.vn.pharmagnosis.repositories.AuthRepository;
import hcmute.edu.vn.pharmagnosis.repositories.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    // Các biến trạng thái để giao diện quan sát (Bắt đầu bằng is theo quy ước)
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<String> userRole = new MutableLiveData<>();
    public LiveData<String> getUserRole() { return userRole; }

    public LoginViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsLoginSuccess() { return isLoginSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void handleLogin(String email, String password) {
        isLoading.setValue(true); // Bật trạng thái loading

        authRepository.loginUser(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                isLoginSuccess.setValue(true);
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                errorMessage.setValue(errorMsg);
            }
        });
    }
    public void fetchRoleAfterLogin() {
        isLoading.setValue(true);
        authRepository.fetchUserRole(new AuthRepository.RoleCallback() {
            @Override
            public void onRoleFetched(String role) {
                isLoading.setValue(false);
                userRole.setValue(role); // Báo cho LoginActivity biết Role là gì
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi lấy quyền: " + errorMsg);
                userRole.setValue("USER");
            }
        });
    }
}