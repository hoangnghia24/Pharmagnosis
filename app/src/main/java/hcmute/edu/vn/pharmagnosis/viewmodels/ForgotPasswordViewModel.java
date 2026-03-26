package hcmute.edu.vn.pharmagnosis.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import hcmute.edu.vn.pharmagnosis.repositories.AuthRepository;

public class ForgotPasswordViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isResetSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ForgotPasswordViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsResetSuccess() { return isResetSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void handleResetPassword(String email) {
        isLoading.setValue(true);

        authRepository.resetPassword(email, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                isResetSuccess.setValue(true);
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                errorMessage.setValue(errorMsg);
            }
        });
    }
}