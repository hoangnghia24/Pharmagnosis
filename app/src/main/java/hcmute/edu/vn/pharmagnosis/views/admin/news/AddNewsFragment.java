package hcmute.edu.vn.pharmagnosis.views.admin.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.viewmodels.admin.news.AddNewsViewModel;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class AddNewsFragment extends Fragment {
    private ImageView imgNewsPhoto;
    private EditText etTitle, etContent;
    private Button btnSave;
    private ImageView imgMenu, ivImagePreview;
    private AddNewsViewModel viewModel;

    // Biến lưu URI ảnh được chọn
    private Uri selectedImageUri = null;

    // Bộ chọn ảnh từ thư viện
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivImagePreview.setImageURI(uri); // Hiển thị ảnh lên giao diện
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_news, container, false);
    }


    private void initViews(View view) {
        etTitle = view.findViewById(R.id.et_news_title);
        etContent = view.findViewById(R.id.et_news_content);
        btnSave = view.findViewById(R.id.btn_save_news);
        imgMenu = view.findViewById(R.id.img_menu);
        // Lưu ý: Đảm bảo trong fragment_add_news.xml của bạn có ImageView mang id iv_news_image_preview
        ivImagePreview = view.findViewById(R.id.iv_news_image_preview);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(AddNewsViewModel.class);

        // 2. Ánh xạ các View (kiểm tra lại ID cho khớp với file XML của bạn)
        Button btnSaveNews = view.findViewById(R.id.btn_save_news);
        // Giả sử bạn có et_news_title, nếu ID khác hãy đổi lại
        etTitle = view.findViewById(R.id.et_news_title);
        etContent = view.findViewById(R.id.et_news_content);
        imgNewsPhoto = view.findViewById(R.id.iv_news_image_preview);

        // Mở thư viện ảnh khi nhấn vào ImageView
        imgNewsPhoto.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
        // 3. Lắng nghe sự kiện click của nút Đăng bài
        btnSaveNews.setOnClickListener(v -> {
            handleSaveNews();
        });

        // 4. BẮT BUỘC: Gọi hàm lắng nghe ViewModel
        observeViewModel();
    }
    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Nén ảnh xuống chuẩn JPEG với chất lượng 50% để tránh vượt quá 1MB của Firestore
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();

            // Trả về chuỗi Base64
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Cập nhật lại nút bấm Lưu
    private void handleSaveNews() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        HealthNews news = new HealthNews();
        news.setTitle(title);
        news.setContent(content);
        news.setPublishedDate(new Date());

        // Nếu có chọn ảnh thì mã hóa thành Base64
        if (selectedImageUri != null) {
            String base64Image = encodeImageToBase64(selectedImageUri);
            news.setImage(base64Image); // Lưu chuỗi dài này vào trường image
        }

        // Gọi ViewModel (Lúc này không cần truyền imageUri nữa vì đã lưu trong news)
        viewModel.saveNews(news);
    }
    private void setupClickListeners() {
        // Nút Menu
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                if (getActivity() instanceof AdminDashboardFragment) {
                    ((AdminDashboardFragment) getActivity()).openSidebar();
                }
            });
        }

        // Chọn ảnh
        if (ivImagePreview != null) {
            ivImagePreview.setOnClickListener(v -> {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            });
        }

        // Nút Đăng bài
        btnSave.setOnClickListener(v -> handleSaveNews());
    }
    private void observeViewModel() {
        // 1. Lắng nghe trạng thái thành công
        viewModel.getIsSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                // Lùi về màn hình trước đó
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // 2. Lắng nghe thông báo lỗi (Rất quan trọng để biết vì sao không lưu được)
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // 3. Lắng nghe trạng thái Loading để khóa nút tránh user bấm nhiều lần
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Button btnSaveNews = requireView().findViewById(R.id.btn_save_news);
            btnSaveNews.setEnabled(!isLoading);
            btnSaveNews.setText(isLoading ? "Đang tải lên..." : "Đăng bài viết");
        });
    }
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri; // Lưu lại Uri để gửi lên Firebase
                    imgNewsPhoto.setImageURI(uri); // Hiển thị ảnh vừa chọn lên giao diện
                }
            });
}