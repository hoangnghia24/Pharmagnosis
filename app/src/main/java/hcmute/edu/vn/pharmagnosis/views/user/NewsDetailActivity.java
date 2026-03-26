package hcmute.edu.vn.pharmagnosis.views.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import hcmute.edu.vn.pharmagnosis.R;

public class NewsDetailActivity extends AppCompatActivity {

    // 1. Khai báo các thành phần giao diện
    private ImageView imgBack;
    private ImageView imgNewsCover;
    private TextView txtNewsTag;
    private TextView txtNewsTitle;
    private TextView txtNewsDateAndSource;
    private TextView txtNewsContent;
    private MaterialButton btnSearchAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initViews();
        setupListeners();

        // Gọi hàm lấy dữ liệu từ Intent để đắp lên giao diện
        loadData();
    }

    // 2. Ánh xạ View chính xác theo ID trong file XML
    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgNewsCover = findViewById(R.id.imgNewsCover);
        txtNewsTag = findViewById(R.id.txtNewsTag);
        txtNewsTitle = findViewById(R.id.txtNewsTitle);
        txtNewsDateAndSource = findViewById(R.id.txtNewsDateAndSource);
        txtNewsContent = findViewById(R.id.txtNewsContent);
        btnSearchAction = findViewById(R.id.btnSearchAction);
    }

    // 3. Cài đặt sự kiện Click
    private void setupListeners() {
        // Nút mũi tên quay lại -> Đóng màn hình này
        imgBack.setOnClickListener(v -> finish());

        // Bấm nút "Tra cứu thuốc" -> Tạm thời lùi về trang chủ
        btnSearchAction.setOnClickListener(v -> {
            finish();
        });
    }

    // 4. Hàm "mở hành lý" (Lấy dữ liệu từ Trang chủ truyền sang)
    private void loadData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Lấy ra đúng những cái tên mà ta đã đóng gói bên NewsAdapter
            String title = intent.getStringExtra("NEWS_TITLE");
            String content = intent.getStringExtra("NEWS_CONTENT");
            String imageUrl = intent.getStringExtra("NEWS_IMAGE");

            // Đắp Tiêu đề lên
            if (title != null) {
                txtNewsTitle.setText(title);
            }

            // Đắp Nội dung lên (Có xử lý xuống dòng cho đẹp)
            if (content != null) {
                txtNewsContent.setText(content.replace("\\n", "\n"));
            }

            // Gọi Shipper Glide tải tấm ảnh bìa cực xịn từ URL nhét vào imgNewsCover
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgNewsCover);
            }
        }
    }
}