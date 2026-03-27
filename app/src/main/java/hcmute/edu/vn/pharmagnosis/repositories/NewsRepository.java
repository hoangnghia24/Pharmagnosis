package hcmute.edu.vn.pharmagnosis.repositories;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hcmute.edu.vn.pharmagnosis.models.HealthNews;

public class NewsRepository {

    private FirebaseFirestore db;
    private final FirebaseStorage storage;
    public NewsRepository() {
        // Khởi tạo kết nối với Firestore
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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
    // Thêm tin tức vào Firestore
    public void addNews(HealthNews news, OnCompleteListener<Void> listener) {
        String docId = db.collection("health_news").document().getId();
        news.setNewId(docId); // Giả sử model HealthNews của bạn có hàm setId()

        db.collection("health_news").document(docId)
                .set(news)
                .addOnCompleteListener(listener);
    }
    public void saveNewsToFirestore(HealthNews news, OnCompleteListener<Void> listener) {
        // Tạo ID mới nếu chưa có
        if (news.getNewId() == null || news.getNewId().isEmpty()) {
            news.setNewId(db.collection("health_news").document().getId());
        }

        db.collection("health_news")
                .document(news.getNewId())
                .set(news)
                .addOnCompleteListener(listener);
    }
    // Hàm xóa tin tức
    public void deleteNews(String newsId, OnCompleteListener<Void> listener) {
        if (newsId != null) {
            db.collection("health_news").document(newsId)
                    .delete()
                    .addOnCompleteListener(listener);
        }
    }
    // 1. Cập nhật nội dung tin tức (khi người dùng KHÔNG đổi ảnh)
    public void updateNews(HealthNews news, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        if (news.getNewId() != null) {
            db.collection("health_news").document(news.getNewId())
                    .set(news)
                    .addOnCompleteListener(listener); // Đã bổ sung dòng này
        }
    }

    // 2. Upload ảnh mới xong rồi mới cập nhật tin tức (khi người dùng CÓ đổi ảnh)
    public void uploadImageAndUpdateNews(Uri imageUri, HealthNews news, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        String fileName = java.util.UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storage.getReference().child("news_images/" + fileName);

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                news.setImage(uri.toString()); // Đặt lại link ảnh mới
                updateNews(news, listener);    // Gọi hàm cập nhật text bên trên
            }).addOnFailureListener(e -> {
                android.util.Log.e("FirebaseError", "Lỗi lấy URL ảnh mới", e);
                listener.onComplete(null);
            });
        }).addOnFailureListener(e -> {
            android.util.Log.e("FirebaseError", "Lỗi upload ảnh mới", e);
            listener.onComplete(null);
        });
    }
    // --- HÀM CHO TÍNH NĂNG XÓA BÀI VIẾT ---
    public void deleteNews(HealthNews news, com.google.android.gms.tasks.OnCompleteListener<Void> listener) {
        if (news.getNewId() == null) return;

        // 1. Xóa dữ liệu bài viết (Document) trong Collection "health_news"
        db.collection("health_news").document(news.getNewId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 2. Nếu xóa Document thành công, tiến hành xóa Ảnh trên Storage
                        if (news.getImage() != null && !news.getImage().isEmpty()) {
                            try {
                                // Lấy reference của ảnh từ URL
                                com.google.firebase.storage.StorageReference imageRef =
                                        com.google.firebase.storage.FirebaseStorage.getInstance().getReferenceFromUrl(news.getImage());

                                // Xóa ảnh (không cần bắt buộc đợi kết quả trả về để tránh UI bị treo)
                                imageRef.delete().addOnFailureListener(e -> {
                                    android.util.Log.e("FirebaseError", "Lỗi xóa ảnh trên Storage: " + e.getMessage());
                                });
                            } catch (Exception e) {
                                android.util.Log.e("FirebaseError", "Link ảnh không hợp lệ: " + e.getMessage());
                            }
                        }
                    }
                    // 3. Trả kết quả về cho Fragment/ViewModel
                    listener.onComplete(task);
                });
    }
    // Trong NewsRepository.java
    public void uploadImageAndSaveNews(Uri imageUri, HealthNews news, OnCompleteListener<Void> listener) {
        if (imageUri != null) {
            // 1. Upload ảnh lên Storage trước
            StorageReference fileRef = storage.getReference().child("news_images/" + UUID.randomUUID().toString());
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // 2. Lấy được link ảnh, gán vào news và lưu Firestore
                    news.setImage(uri.toString());
                    saveNewsToFirestore(news, listener);
                });
            }).addOnFailureListener(e -> listener.onComplete(null)); // Thất bại khi upload ảnh
        } else {
            // Nếu không có ảnh, lưu trực tiếp
            saveNewsToFirestore(news, listener);
        }
    }
    // 1. Hàm lấy chi tiết một bài viết theo ID (để ViewModel load dữ liệu cũ)
    public void getNewsById(String newsId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("health_news")
                .document(newsId)
                .get()
                .addOnCompleteListener(listener);
    }

    // 2. Hàm cập nhật dữ liệu (Sử dụng lệnh .set() hoặc .update())
    public void updateNewsToFirestore(HealthNews news, OnCompleteListener<Void> listener) {
        // Sử dụng .set() với đối tượng đầy đủ sẽ ghi đè tài liệu cũ bằng dữ liệu mới
        db.collection("health_news")
                .document(news.getNewId())
                .set(news) // Ghi đè toàn bộ dữ liệu, bao gồm cả trường 'image' chứa chuỗi Base64 mới
                .addOnCompleteListener(listener);
    }
}