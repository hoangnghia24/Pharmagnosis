package hcmute.edu.vn.pharmagnosis.viewmodels.user; // Sửa đường dẫn nếu bạn để trong thư mục con

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.repositories.NewsRepository;

public class UserDashboardViewModel extends ViewModel {

    private NewsRepository newsRepository;
    private LiveData<List<HealthNews>> newsLiveData;

    public UserDashboardViewModel() {
        newsRepository = new NewsRepository();
        // Ngay khi màn hình Trang chủ vừa mở lên, nó sẽ tự động chạy lệnh đi lấy tin tức
        newsLiveData = newsRepository.getNewsFromFirebase();
    }

    // Giao diện (Fragment) sẽ gọi hàm này để lấy danh sách bài báo
    public LiveData<List<HealthNews>> getNewsLiveData() {
        return newsLiveData;
    }
}