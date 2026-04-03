package hcmute.edu.vn.pharmagnosis.views.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Allergy;
import hcmute.edu.vn.pharmagnosis.viewmodels.ProfileViewModel;

public class MedicalProfileFragment extends Fragment {

    private EditText edtFullName;
    private TextView tvGenderVal, tvDobVal, tvBloodVal, btnSave;
    private LinearLayout llAllergiesContainer, btnAddAllergy;
    private ImageView imgAvatarPreview;
    private View btnPickImage;

    // --- KHAI BÁO THÊM 2 Ô CHIỀU CAO & CÂN NẶNG ---
    private EditText edtHeightProfile, edtWeightProfile;

    private ProfileViewModel profileViewModel;

    private String selectedGenderEnum = "MALE";
    private String selectedBloodType = "O+";
    private List<Allergy> currentAllergies = new ArrayList<>();

    // Biến lưu trữ chuỗi ảnh đã được mã hóa
    private String base64Avatar = null;

    // BỘ CHỌN ẢNH TỪ THƯ VIỆN ĐIỆN THOẠI
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processSelectedImage(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Ánh xạ View
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnSave = view.findViewById(R.id.btn_save);
        edtFullName = view.findViewById(R.id.edt_fullname);
        tvGenderVal = view.findViewById(R.id.tv_gender_val);
        tvDobVal = view.findViewById(R.id.tv_dob_val);
        tvBloodVal = view.findViewById(R.id.tv_blood_val);
        llAllergiesContainer = view.findViewById(R.id.ll_allergies_container);
        btnAddAllergy = view.findViewById(R.id.btn_add_allergy);

        // Khung ảnh
        imgAvatarPreview = view.findViewById(R.id.img_avatar_preview);
        btnPickImage = view.findViewById(R.id.btn_pick_image);

        // --- ÁNH XẠ Ô CHIỀU CAO VÀ CÂN NẶNG ---
        edtHeightProfile = view.findViewById(R.id.edt_height_profile);
        edtWeightProfile = view.findViewById(R.id.edt_weight_profile);

        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Bắt sự kiện chọn ảnh
        if (btnPickImage != null) {
            btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        setupSelectionDialogs(view);
        setupSaveButton();
        setupAllergyLogic();
        observeViewModel();
    }

    // --- LOGIC XỬ LÝ NÉN VÀ MÃ HÓA ẢNH BASE64 ---
    private void processSelectedImage(Uri uri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Cắt nhỏ ảnh lại (Max 400x400) để Firebase không bị quá tải
            int maxSize = 400;
            float ratio = Math.min((float) maxSize / bitmap.getWidth(), (float) maxSize / bitmap.getHeight());
            int width = Math.round((float) ratio * bitmap.getWidth());
            int height = Math.round((float) ratio * bitmap.getHeight());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

            // Hiển thị lên UI
            imgAvatarPreview.setImageBitmap(resizedBitmap);

            // Nén thành chuỗi Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // Chất lượng 70%
            byte[] imageBytes = baos.toByteArray();
            base64Avatar = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi khi xử lý ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    // Giải mã Base64 thành hình ảnh để hiển thị ảnh cũ lúc vừa mở trang
    private void decodeBase64AndDisplay(String base64Str) {
        try {
            byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgAvatarPreview.setImageBitmap(decodedByte);
        } catch (Exception e) { e.printStackTrace(); }
    }
    // ---------------------------------------------

    private void observeViewModel() {
        profileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (user.getFullName() != null) edtFullName.setText(user.getFullName());
                if (user.getGender() != null) {
                    selectedGenderEnum = user.getGender().name();
                    tvGenderVal.setText(selectedGenderEnum.equals("MALE") ? "Nam" : "Nữ");
                }
                if (user.getBloodType() != null) {
                    selectedBloodType = user.getBloodType();
                    tvBloodVal.setText(selectedBloodType);
                }
                if (user.getDob() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvDobVal.setText(sdf.format(user.getDob()));
                }
                if (user.getAllergies() != null) {
                    currentAllergies = user.getAllergies();
                } else {
                    currentAllergies = new ArrayList<>();
                }
                renderAllergies();

                // Hiển thị ảnh Avatar cũ nếu có
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    base64Avatar = user.getAvatar();
                    decodeBase64AndDisplay(base64Avatar);
                }

                // --- ĐIỀN SẴN CHIỀU CAO VÀ CÂN NẶNG LÊN UI ---
                if (edtHeightProfile != null && user.getHeight() > 0) {
                    edtHeightProfile.setText(String.format(Locale.getDefault(), "%.0f", user.getHeight()));
                }
                if (edtWeightProfile != null && user.getWeight() > 0) {
                    edtWeightProfile.setText(String.format(Locale.getDefault(), "%.1f", user.getWeight()));
                }
            }
        });

        profileViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                profileViewModel.clearMessage();
                if (message.contains("Cập nhật hồ sơ thành công")) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    private void setupAllergyLogic() {
        if (btnAddAllergy != null) btnAddAllergy.setOnClickListener(v -> showAddAllergyDialog());
    }

    private void showAddAllergyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm Dị ứng mới");
        final EditText input = new EditText(requireContext());
        input.setHint("Nhập tên thuốc hoặc thực phẩm...");
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String allergyName = input.getText().toString().trim();
            if (!allergyName.isEmpty()) {
                Allergy newAllergy = new Allergy(String.valueOf(System.currentTimeMillis()), allergyName, null, null);
                currentAllergies.add(newAllergy);
                profileViewModel.updateAllergiesList(currentAllergies);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void renderAllergies() {
        if (llAllergiesContainer == null) return;
        llAllergiesContainer.removeAllViews();
        for (int i = 0; i < currentAllergies.size(); i++) {
            Allergy allergy = currentAllergies.get(i);
            final int indexToRemove = i;
            View allergyView = LayoutInflater.from(getContext()).inflate(R.layout.item_allergy_tag, llAllergiesContainer, false);
            TextView tvName = allergyView.findViewById(R.id.tv_allergy_name);
            TextView btnRemove = allergyView.findViewById(R.id.btn_remove_allergy);

            tvName.setText(allergy.getAllergenName());
            btnRemove.setOnClickListener(v -> {
                currentAllergies.remove(indexToRemove);
                profileViewModel.updateAllergiesList(currentAllergies);
            });
            llAllergiesContainer.addView(allergyView);
        }
    }

    private void setupSelectionDialogs(View parentView) {
        if (tvGenderVal != null) {
            View cardGender = (View) tvGenderVal.getParent().getParent();
            cardGender.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(requireContext(), tvGenderVal);
                popup.getMenu().add("Nam"); popup.getMenu().add("Nữ");
                popup.setOnMenuItemClickListener(item -> {
                    tvGenderVal.setText(item.getTitle());
                    selectedGenderEnum = item.getTitle().toString().equals("Nam") ? "MALE" : "FEMALE";
                    return true;
                });
                popup.show();
            });
        }

        if (tvBloodVal != null) {
            View cardBlood = (View) tvBloodVal.getParent().getParent();
            cardBlood.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(requireContext(), tvBloodVal);
                String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
                for (String type : bloodTypes) popup.getMenu().add(type);
                popup.setOnMenuItemClickListener(item -> {
                    tvBloodVal.setText(item.getTitle());
                    selectedBloodType = item.getTitle().toString();
                    return true;
                });
                popup.show();
            });
        }

        if (tvDobVal != null) {
            View cardDob = (View) tvDobVal.getParent().getParent();
            cardDob.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    tvDobVal.setText(formattedDate);
                }, calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            });
        }
    }

    private void setupSaveButton() {
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String fullName = edtFullName.getText().toString().trim();
                if (fullName.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập họ tên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- LOGIC LẤY VÀ TÍNH BMI TỪ CÂN NẶNG & CHIỀU CAO ---
                String strHeight = edtHeightProfile != null ? edtHeightProfile.getText().toString().trim() : "";
                String strWeight = edtWeightProfile != null ? edtWeightProfile.getText().toString().trim() : "";

                float height = 0;
                float weight = 0;
                float bmi = 0;

                try {
                    if (!strHeight.isEmpty()) height = Float.parseFloat(strHeight);
                    if (!strWeight.isEmpty()) weight = Float.parseFloat(strWeight);

                    if (height > 0 && weight > 0) {
                        float heightM = height / 100f;
                        bmi = weight / (heightM * heightM);
                    }
                } catch (Exception ignored) {}

                Date dobDate = null;
                try {
                    dobDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(tvDobVal.getText().toString());
                } catch (Exception ignored) {}

                // GỌI HÀM VIEW MODEL MỚI ĐỂ LƯU TẤT CẢ VÀO FIREBASE
                profileViewModel.saveProfileAndHistory(fullName, selectedGenderEnum, selectedBloodType, dobDate, base64Avatar, height, weight, bmi);
            });
        }
    }
}