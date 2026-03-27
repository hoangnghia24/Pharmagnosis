package hcmute.edu.vn.pharmagnosis.views.admin.medicines;

import android.net.Uri;
import android.os.Bundle;
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
    private Button btnCancel, btnSave;

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

        // Nếu bạn chưa tạo EditMedicineViewModel, bạn có thể comment dòng dưới lại tạm thời
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

        // 2. Load ảnh bằng Glide
        String imageUrl = currentMedicine.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgMedicinePhoto);
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
        btnSave.setOnClickListener(v -> onSaveButtonClicked());
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

        // Gọi ViewModel để lưu lên Firebase
        if (viewModel != null) {
            viewModel.updateMedicineToFirebase(newImageUri, currentMedicine);
        } else {
            Toast.makeText(requireContext(), "Dữ liệu đã sẵn sàng để cập nhật!", Toast.LENGTH_SHORT).show();
            // Nếu chưa có viewmodel, tạm thời tự back về
            // requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }
}