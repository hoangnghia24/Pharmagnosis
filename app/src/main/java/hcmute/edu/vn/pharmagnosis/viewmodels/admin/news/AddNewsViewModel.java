package hcmute.edu.vn.pharmagnosis.viewmodels.admin.news;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.repositories.NewsRepository;

public class AddNewsViewModel extends ViewModel {
    private final NewsRepository repository = new NewsRepository();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsSuccess() { return isSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Đổi tham số, chỉ nhận HealthNews
    public void saveNews(HealthNews news) {
        if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
            errorMessage.setValue("Tiêu đề không được để trống!");
            return;
        }

        isLoading.setValue(true);
        // Gọi thẳng hàm lưu Firestore
        repository.saveNewsToFirestore(news, task -> {
            isLoading.setValue(false);
            if (task != null && task.isSuccessful()) {
                isSuccess.setValue(true);
            } else {
                errorMessage.setValue("Lưu bài viết thất bại. Vui lòng thử lại!");
            }
        });
    }

}