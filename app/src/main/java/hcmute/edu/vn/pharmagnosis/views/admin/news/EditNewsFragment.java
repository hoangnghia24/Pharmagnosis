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

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.viewmodels.admin.news.EditNewsViewModel;

public class EditNewsFragment extends Fragment {
    private Uri selectedImageUri = null; // Uri của ảnh mới nếu người dùng chọn
    private String currentBase64Image = null; // Chuỗi Base64 của ảnh cũ
    private EditText etTitle, etContent;
    private Button btnUpdate;
    private ImageView imgMenu, ivImagePreview;

    private EditNewsViewModel viewModel;
    private HealthNews currentNews; // Chứa dữ liệu bài viết cũ

    // Bộ chọn ảnh từ thư viện
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivImagePreview.setImageURI(uri); // Hiển thị ngay ảnh vừa chọn lên ImageView
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        viewModel = new ViewModelProvider(this).get(EditNewsViewModel.class);

        // 1. Nhận dữ liệu bài viết được truyền sang từ Adapter (Cách phổ biến trong Android)
        if (getArguments() != null && getArguments().containsKey("selected_news")) {
            currentNews = (HealthNews) getArguments().getSerializable("selected_news");
            loadDataToViews();
        } else {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy bài viết!", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }

        setupClickListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        etTitle = view.findViewById(R.id.et_news_title);
        etContent = view.findViewById(R.id.et_news_content);
        btnUpdate = view.findViewById(R.id.btn_edit_news); // Nút cập nhật
        imgMenu = view.findViewById(R.id.img_menu); // Nút quay lại (Back)
        ivImagePreview = view.findViewById(R.id.iv_news_image_preview);
    }

    // 2. Hiển thị dữ liệu cũ lên giao diện
    private void loadDataToViews() {
        if (currentNews != null) {
            etTitle.setText(currentNews.getTitle());
            etContent.setText(currentNews.getContent());
            currentBase64Image = currentNews.getImage(); // Lưu lại chuỗi ảnh cũ

            // Dịch ngược chuỗi Base64 cũ thành ảnh Bitmap để hiển thị
            if (currentBase64Image != null && !currentBase64Image.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(currentBase64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivImagePreview.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Có thể set một ảnh mặc định nếu bị lỗi giải mã
                    // ivImagePreview.setImageResource(R.drawable.ic_placeholder);
                }
            }
        }
    }

    private void setupClickListeners() {
        // Nút quay lại
        imgMenu.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Nhấn vào khung ảnh để mở thư viện chọn ảnh mới
        ivImagePreview.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Bấm nút cập nhật
        btnUpdate.setOnClickListener(v -> handleUpdateNews());
    }

    // 3. Xử lý logic khi bấm Cập nhật (Phân biệt cách cũ vs mới)
    private void handleUpdateNews() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        // Validate cơ bản
        if (title.isEmpty()) {
            etTitle.setError("Tiêu đề không được trống");
            return;
        }
        if (content.isEmpty()) {
            etContent.setError("Nội dung không được trống");
            return;
        }

        // Tạo một đối tượng News mới (hoặc cập nhật trực tiếp currentNews)
        currentNews.setTitle(title);
        currentNews.setContent(content);

        // --- LOGIC XỬ LÝ ẢNH BASE64 ---
        if (selectedImageUri != null) {
            // CÁCH MỚI: Người dùng có chọn ảnh mới
            // Tiến hành nén và mã hóa ảnh mới thành Base64
            String newBase64 = encodeImageToBase64(selectedImageUri);
            currentNews.setImage(newBase64); // Ghi đè chuỗi mới vào
        } else {
            // CÁCH CŨ: Người dùng không chọn ảnh mới
            // Giữ nguyên chuỗi Base64 của ảnh cũ
            currentNews.setImage(currentBase64Image);
        }
        if (viewModel != null) {
            viewModel.updateNews(currentNews);
        }
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnUpdate.setEnabled(!isLoading);
            btnUpdate.setText(isLoading ? "Đang xử lý..." : "Sửa bài viết");
        });

        viewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Hàm chuyển đổi Uri thành chuỗi Base64 (có nén ảnh)
    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Nén ảnh xuống chuẩn JPEG với chất lượng 50%
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
}