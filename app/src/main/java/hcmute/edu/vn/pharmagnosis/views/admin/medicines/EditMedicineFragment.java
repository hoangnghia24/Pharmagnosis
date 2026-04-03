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

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.viewmodels.admin.medicines.EditMedicineViewModel;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class EditMedicineFragment extends Fragment {

    private EditMedicineViewModel viewModel;
    private Medicine currentMedicine;
    private Uri newImageUri = null; // Lưu uri nếu người dùng chọn ảnh mới

    // Khai báo các Views
    private ImageView imgMenu, imgMedicinePhoto;
    private EditText etMedicineName, etCompany, etInstructions;
    private Spinner spinnerDosageForm, spinnerTargetAudience;
    private Button btnCancel, btnEdit;

    // Khai báo các Views cho ChipGroup (Hoạt chất, Chống chỉ định, Tác dụng phụ)
    private EditText etActiveIngredientInput, etContraindicationInput, etSideEffectInput;
    private Button btnAddActiveIngredient, btnAddContraindication, btnAddSideEffect;
    private ChipGroup cgActiveIngredients, cgContraindications, cgSideEffects;

    // Danh sách lưu trữ dữ liệu tạm thời cho Chip
    private final List<String> activeIngredientsList = new ArrayList<>();
    private final List<String> contraindicationsList = new ArrayList<>();
    private final List<String> sideEffectsList = new ArrayList<>();

    // Bộ chọn ảnh từ Gallery
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    newImageUri = uri;
                    imgMedicinePhoto.setImageURI(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(EditMedicineViewModel.class);

        initializeViews(view);
        setupSpinners();
        setupEvents();

        // Lấy dữ liệu được truyền sang từ Bundle
        if (getArguments() != null) {
            String medicineJson = getArguments().getString("medicine_json");
            if (medicineJson != null && !medicineJson.isEmpty()) {
                Gson gson = new Gson();
                currentMedicine = gson.fromJson(medicineJson, Medicine.class);
                fillDataToUI(); // Đổ dữ liệu lên giao diện
            }
        }

        // Lắng nghe trạng thái từ ViewModel
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
        btnEdit = view.findViewById(R.id.btn_edit);
    }

    private void setupSpinners() {
        String[] dosageForms = {"Viên nén", "Viên nang", "Siro", "Dung dịch tiêm", "Kem bôi", "Dung dịch"};
        String[] targetAudiences = {"Người lớn", "Trẻ em", "Phụ nữ có thai", "Mọi đối tượng"};

        ArrayAdapter<String> dosageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, dosageForms);
        spinnerDosageForm.setAdapter(dosageAdapter);

        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, targetAudiences);
        spinnerTargetAudience.setAdapter(targetAdapter);
    }

    private void fillDataToUI() {
        if (currentMedicine == null) return;

        // 1. Đổ dữ liệu Text
        etMedicineName.setText(currentMedicine.getMedicineName());
        etCompany.setText(currentMedicine.getTradeName());
        etInstructions.setText(currentMedicine.getIndications());

        // 2. Load ảnh (Hỗ trợ cả URL cũ và Base64 mới)
        String imageString = currentMedicine.getImage();
        if (imageString != null && !imageString.isEmpty()) {
            try {
                if (imageString.startsWith("http")) {
                    // Nếu dữ liệu trên Firebase vẫn là link URL cũ, dùng Glide
                    Glide.with(this)
                            .load(imageString)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(imgMedicinePhoto);
                } else {
                    // Nếu là dữ liệu Base64 mới, tự động giải mã ra Bitmap
                    byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgMedicinePhoto.setImageBitmap(decodedByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Đổ dữ liệu Spinner
        setSpinnerSelection(spinnerDosageForm, currentMedicine.getDosageForm());
        setSpinnerSelection(spinnerTargetAudience, currentMedicine.getTargetUsers());

        // 4. Đổ dữ liệu vào ChipGroups
        loadChips(currentMedicine.getActiveIngredient(), cgActiveIngredients, activeIngredientsList);
        loadChips(currentMedicine.getContraindications(), cgContraindications, contraindicationsList);
        loadChips(currentMedicine.getSideEffects(), cgSideEffects, sideEffectsList);
    }

    // Hàm hỗ trợ set Spinner
    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    // Hàm hỗ trợ load Chip cũ lên giao diện
    private void loadChips(List<String> sourceList, ChipGroup chipGroup, List<String> targetList) {
        if (sourceList != null) {
            chipGroup.removeAllViews();
            targetList.clear();
            targetList.addAll(sourceList);
            for (String text : targetList) {
                addChipToGroup(text, chipGroup, targetList);
            }
        }
    }

    private void setupEvents() {
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).openSidebar());
        }

        imgMedicinePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnCancel.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Xử lý Thêm Chip mới
        setupAddChipEvent(btnAddActiveIngredient, etActiveIngredientInput, cgActiveIngredients, activeIngredientsList);
        setupAddChipEvent(btnAddContraindication, etContraindicationInput, cgContraindications, contraindicationsList);
        setupAddChipEvent(btnAddSideEffect, etSideEffectInput, cgSideEffects, sideEffectsList);

        // Nút Lưu (Cập nhật)
        btnEdit.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void setupAddChipEvent(Button btnAdd, EditText inputField, ChipGroup chipGroup, List<String> dataList) {
        btnAdd.setOnClickListener(v -> {
            String text = inputField.getText().toString().trim();
            if (!text.isEmpty()) {
                dataList.add(text);
                addChipToGroup(text, chipGroup, dataList);
                inputField.setText("");
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
        if (currentMedicine == null) return;

        // Cập nhật dữ liệu từ UI vào Object
        currentMedicine.setMedicineName(etMedicineName.getText().toString().trim());
        currentMedicine.setTradeName(etCompany.getText().toString().trim());
        currentMedicine.setIndications(etInstructions.getText().toString().trim());
        currentMedicine.setDosageForm(spinnerDosageForm.getSelectedItem().toString());
        currentMedicine.setTargetUsers(spinnerTargetAudience.getSelectedItem().toString());

        currentMedicine.setActiveIngredient(new ArrayList<>(activeIngredientsList));
        currentMedicine.setContraindications(new ArrayList<>(contraindicationsList));
        currentMedicine.setSideEffects(new ArrayList<>(sideEffectsList));

        // Nếu người dùng có chọn ảnh mới, thực hiện mã hóa ảnh
        if (newImageUri != null) {
            String base64Image = encodeImageToBase64(newImageUri);
            if (base64Image != null) {
                currentMedicine.setImage(base64Image); // Cập nhật chuỗi Base64 mới vào model
            }
        }

        // Gọi ViewModel để lưu lên Firebase (Chỉ truyền Model, không cần truyền URI nữa)
        if (viewModel != null) {
            viewModel.updateMedicineToFirebase(currentMedicine);
        }
    }

    // --- HÀM Lắng nghe trạng thái từ ViewModel ---
    private void observeViewModel() {
        if (viewModel == null) return;

        // Lắng nghe trạng thái loading để vô hiệu hóa nút bấm
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnEdit.setEnabled(!isLoading);
            btnEdit.setText(isLoading ? "Đang cập nhật..." : "Cập nhật");
        });

        // Lắng nghe trạng thái thành công
        viewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Cập nhật thuốc thành công!", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed(); // Trở về danh sách
            }
        });

        // Lắng nghe trạng thái lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}