package hcmute.edu.vn.pharmagnosis.viewmodels.user;

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
        newsLiveData = newsRepository.getNewsFromFirebase();
    }

    public LiveData<List<HealthNews>> getNewsLiveData() {
        return newsLiveData;
    }
}