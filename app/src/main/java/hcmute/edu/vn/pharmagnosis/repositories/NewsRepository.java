package hcmute.edu.vn.pharmagnosis.repositories;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;

public class NewsRepository {

    private FirebaseFirestore db;

    public NewsRepository() {
        // Khởi tạo kết nối với Firestore
        db = FirebaseFirestore.getInstance();
    }

    // Hàm này sẽ lên Firebase lấy tin tức và trả về một LiveData
    public MutableLiveData<List<HealthNews>> getNewsFromFirebase() {
        MutableLiveData<List<HealthNews>> newsLiveData = new MutableLiveData<>();

        // Trỏ vào bảng "health_news" mà bạn vừa tạo trên web
        db.collection("health_news")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<HealthNews> newsList = new ArrayList<>();
                        // Duyệt qua từng bài báo lấy được
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Firebase tự động ép kiểu dữ liệu thành class HealthNews cực kỳ vi diệu!
                            HealthNews news = document.toObject(HealthNews.class);
                            newsList.add(news);
                        }
                        // Gửi danh sách này vào LiveData
                        newsLiveData.setValue(newsList);
                    } else {
                        Log.e("FirebaseError", "Lỗi khi tải tin tức: ", task.getException());
                        // Nếu lỗi thì trả về danh sách rỗng để app không bị crash
                        newsLiveData.setValue(new ArrayList<>());
                    }
                });

        return newsLiveData;
    }
}