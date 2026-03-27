package hcmute.edu.vn.pharmagnosis.viewmodels.admin.news;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.repositories.NewsRepository;
// Trong file EditNewsViewModel.java của bạn

public class EditNewsViewModel extends ViewModel {
    private final NewsRepository repository = new NewsRepository(); // Cần import NewsRepository

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<HealthNews> currentNews = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsSuccess() { return isSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<HealthNews> getCurrentNews() { return currentNews; }

    // 1. Hàm tải dữ liệu bài viết cũ để hiển thị lên Form
    public void loadNews(String newsId) {
        if (newsId == null || newsId.isEmpty()) return;

        isLoading.setValue(true);
        // Cần đảm bảo Repository có hàm getNewsById
        repository.getNewsById(newsId, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                HealthNews news = task.getResult().toObject(HealthNews.class);
                currentNews.setValue(news);
            } else {
                errorMessage.setValue("Không thể tải dữ liệu bài viết!");
            }
        });
    }

    // 2. Hàm cập nhật bài viết (SỬ DỤNG CÁCH BASE64)
    public void updateNews(HealthNews updatedNews) {
        // Validate dữ liệu cơ bản
        if (updatedNews.getTitle() == null || updatedNews.getTitle().trim().isEmpty()) {
            errorMessage.setValue("Tiêu đề không được để trống!");
            return;
        }
        if (updatedNews.getNewId() == null || updatedNews.getNewId().isEmpty()) {
            errorMessage.setValue("Lỗi không xác định ID bài viết!");
            return;
        }

        isLoading.setValue(true);

        // Gọi thẳng hàm update vào Firestore (Vì ảnh đã được Fragment chuyển thành Base64 và gán vào updatedNews rồi)
        repository.updateNewsToFirestore(updatedNews, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                isSuccess.setValue(true);
            } else {
                errorMessage.setValue("Cập nhật bài viết thất bại. Vui lòng thử lại!");
            }
        });
    }
}