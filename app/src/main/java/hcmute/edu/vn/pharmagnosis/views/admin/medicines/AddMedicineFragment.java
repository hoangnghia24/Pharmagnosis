package hcmute.edu.vn.pharmagnosis.views.admin.medicines;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.viewmodels.admin.medicines.AddMedicineViewModel;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class AddMedicineFragment extends Fragment {

    private AddMedicineViewModel viewModel;
    private Uri selectedImageUri = null;

    // Khai báo các Views
    private ImageView imgMedicinePhoto, imgMenu;
    private EditText etMedicineName, etCompany, etInstructions;
    private Spinner spinnerDosageForm, spinnerTargetAudience;
    private Button btnCancel, btnSave;

    // Các view cho List<String>
    private EditText etActiveIngredientInput, etContraindicationInput, etSideEffectInput;
    private Button btnAddActiveIngredient, btnAddContraindication, btnAddSideEffect;
    private ChipGroup cgActiveIngredients, cgContraindications, cgSideEffects;

    private final List<String> activeIngredientsList = new ArrayList<>();
    private final List<String> contraindicationsList = new ArrayList<>();
    private final List<String> sideEffectsList = new ArrayList<>();

    // Bộ chọn ảnh từ thư viện thiết bị
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgMedicinePhoto.setImageURI(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(AddMedicineViewModel.class);

        initializeViews(view);
        setupSpinners();
        setupEvents();
        observeViewModel();
    }

    private void initializeViews(View view) {
        imgMenu = view.findViewById(R.id.img_menu);
        imgMedicinePhoto = view.findViewById(R.id.img_medicine_photo);
        etMedicineName = view.findViewById(R.id.et_medicine_name);
        etCompany = view.findViewById(R.id.et_company);
        etInstructions = view.findViewById(R.id.et_instructions);

        spinnerDosageForm = view.findViewById(R.id.spinner_dosage_form);
        spinnerTargetAudience = view.findViewById(R.id.spinner_target_audience);

        etActiveIngredientInput = view.findViewById(R.id.et_active_ingredient_input);
        btnAddActiveIngredient = view.findViewById(R.id.btn_add_active_ingredient);
        cgActiveIngredients = view.findViewById(R.id.cg_active_ingredients);

        etContraindicationInput = view.findViewById(R.id.et_contraindication_input);
        btnAddContraindication = view.findViewById(R.id.btn_add_contraindication);
        cgContraindications = view.findViewById(R.id.cg_contraindications);

        etSideEffectInput = view.findViewById(R.id.et_side_effect_input);
        btnAddSideEffect = view.findViewById(R.id.btn_add_side_effect);
        cgSideEffects = view.findViewById(R.id.cg_side_effects);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSave = view.findViewById(R.id.btn_save);
    }

    private void setupSpinners() {
        // Mock data cho Spinner, bạn có thể chuyển vào strings.xml sau
        String[] dosageForms = {"Viên nén", "Viên nang", "Siro", "Dung dịch tiêm", "Kem bôi"};
        String[] targetAudiences = {"Người lớn", "Trẻ em", "Phụ nữ có thai", "Mọi đối tượng"};

        ArrayAdapter<String> dosageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, dosageForms);
        spinnerDosageForm.setAdapter(dosageAdapter);

        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, targetAudiences);
        spinnerTargetAudience.setAdapter(targetAdapter);
    }

    private void setupEvents() {
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).openSidebar());
        }

        btnCancel.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Chọn ảnh
        imgMedicinePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Cài đặt nút thêm cho các List<String>
        setupAddChipEvent(btnAddActiveIngredient, etActiveIngredientInput, cgActiveIngredients, activeIngredientsList);
        setupAddChipEvent(btnAddContraindication, etContraindicationInput, cgContraindications, contraindicationsList);
        setupAddChipEvent(btnAddSideEffect, etSideEffectInput, cgSideEffects, sideEffectsList);

        // Nút Lưu
        btnSave.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void setupAddChipEvent(Button btnAdd, EditText inputField, ChipGroup chipGroup, List<String> dataList) {
        btnAdd.setOnClickListener(v -> {
            String text = inputField.getText().toString().trim();
            if (!text.isEmpty()) {
                dataList.add(text);
                addChipToGroup(text, chipGroup, dataList);
                inputField.setText(""); // Xóa rỗng ô nhập
            }
        });
    }

    private void addChipToGroup(String text, ChipGroup chipGroup, List<String> dataList) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            dataList.remove(text);
        });
        chipGroup.addView(chip);
    }

    // Hàm mã hóa ảnh sang Base64
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

    private void onSaveButtonClicked() {
        // Gán dữ liệu vào model Medicine
        Medicine medicine = new Medicine();
        medicine.setMedicineName(etMedicineName.getText().toString().trim());
        medicine.setTradeName(etCompany.getText().toString().trim());
        medicine.setIndications(etInstructions.getText().toString().trim());
        medicine.setDosageForm(spinnerDosageForm.getSelectedItem().toString());
        medicine.setTargetUsers(spinnerTargetAudience.getSelectedItem().toString());

        // Gán các List
        medicine.setActiveIngredient(new ArrayList<>(activeIngredientsList));
        medicine.setContraindications(new ArrayList<>(contraindicationsList));
        medicine.setSideEffects(new ArrayList<>(sideEffectsList));

        // Xử lý mã hóa ảnh sang Base64 nếu có chọn ảnh
        if (selectedImageUri != null) {
            String base64Image = encodeImageToBase64(selectedImageUri);
            // Giả sử model Medicine có thuộc tính lưu ảnh dạng String là setImageBase64 hoặc tương tự
            // BẠN CẦN ĐỔI TÊN HÀM NÀY CHO KHỚP VỚI MODEL BÊN BẠN (VD: setImage, setImageUrl, v.v.)
            medicine.setImage(base64Image);
        }

        // Gọi ViewModel xử lý
        // BẠN CẦN CẬP NHẬT LẠI HÀM NÀY TRONG AddMedicineViewModel ĐỂ CHỈ NHẬN VÀO (medicine)
        viewModel.saveMedicineToFirebase(medicine);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnSave.setEnabled(!isLoading);
            btnSave.setText(isLoading ? "Đang lưu..." : "Lưu");
        });

        viewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Thêm thuốc thành công!", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed(); // Quay lại màn hình trước
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}