package hcmute.edu.vn.pharmagnosis.views.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.MedicineSearchAdapter;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.SearchViewModel;

public class SearchFragment extends Fragment {

    private String selectedDosage = "";
    private String selectedTarget = "";
    private ImageView imgBack;
    private ImageView imgFilter;
    private ImageView imgClearSearch;
    private SearchViewModel searchViewModel;
    private MedicineSearchAdapter searchAdapter;
    private RecyclerView recyclerSearch;
    private EditText edtSearch;
    private com.google.android.material.chip.Chip chipParacetamol, chipIbuprofen, chipMenTieuHoa, chipOresol;
    private java.util.List<hcmute.edu.vn.pharmagnosis.models.Medicine> allMedicines = new java.util.ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupListeners();
        setupRecyclerView();
        setupViewModel();
        setupSearchLogic();

        return view;
    }

    private void initViews(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgFilter = view.findViewById(R.id.imgFilter);
        imgClearSearch = view.findViewById(R.id.imgClearSearch);
        recyclerSearch = view.findViewById(R.id.recyclerSearchHistory);
        edtSearch = view.findViewById(R.id.edtSearchInput);

        chipParacetamol = view.findViewById(R.id.chipParacetamol);
        chipIbuprofen = view.findViewById(R.id.chipIbuprofen);
        chipMenTieuHoa = view.findViewById(R.id.chipMenTieuHoa);
        chipOresol = view.findViewById(R.id.chipOresol);
    }

    private void setupListeners() {
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        if (imgFilter != null) {
            imgFilter.setOnClickListener(v -> {
                showFilterBottomSheet();
            });
        }

        if (imgClearSearch != null) {
            imgClearSearch.setOnClickListener(v -> {
                if (edtSearch != null) {
                    edtSearch.setText("");
                }
            });
        }

        if (chipParacetamol != null) chipParacetamol.setOnClickListener(v -> openMedicineDetailFast("Paracetamol"));
        if (chipIbuprofen != null) chipIbuprofen.setOnClickListener(v -> openMedicineDetailFast("Ibuprofen"));
        if (chipMenTieuHoa != null) chipMenTieuHoa.setOnClickListener(v -> openMedicineDetailFast("Men"));
        if (chipOresol != null) chipOresol.setOnClickListener(v -> openMedicineDetailFast("Oresol"));
    }


    private void setupRecyclerView() {
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new MedicineSearchAdapter(new java.util.ArrayList<>());
        recyclerSearch.setAdapter(searchAdapter);
    }

    private void setupViewModel() {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchViewModel.getMedicinesLiveData().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines != null) {
                allMedicines = medicines;
                searchAdapter.setMedicines(medicines);
            }
        });
    }

    private void setupSearchLogic() {
        if (edtSearch != null) {
            // Hiển thị/Ẩn nút xóa
            imgClearSearch.setVisibility(View.GONE); 

            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (searchAdapter != null) {
                        searchAdapter.applyAdvancedFilter(s.toString(), selectedDosage, selectedTarget);
                    }
                    
                    // Hiển thị nút xóa nếu có chữ, ẩn nếu rỗng
                    if (s.length() > 0) {
                        imgClearSearch.setVisibility(View.VISIBLE);
                    } else {
                        imgClearSearch.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void openMedicineDetailFast(String keyword) {
        if (allMedicines == null || allMedicines.isEmpty()) {
            android.widget.Toast.makeText(getContext(), "Đang tải dữ liệu...", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        hcmute.edu.vn.pharmagnosis.models.Medicine foundMedicine = null;
        for (hcmute.edu.vn.pharmagnosis.models.Medicine m : allMedicines) {
            if (m.getMedicineName().toLowerCase().contains(keyword.toLowerCase()) ||
                    (m.getTradeName() != null && m.getTradeName().toLowerCase().contains(keyword.toLowerCase()))) {
                foundMedicine = m;
                break;
            }
        }

        if (foundMedicine != null) {
            android.content.Intent intent = new android.content.Intent(getContext(), MedicineDetailActivity.class);
            intent.putExtra("MEDICINE_OBJ", (java.io.Serializable) foundMedicine);
            startActivity(intent);
        } else {
            android.widget.Toast.makeText(getContext(), "Không tìm thấy thuốc!", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    private void showFilterBottomSheet() {
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_filter, null);
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(sheetView);

        com.google.android.material.chip.ChipGroup chipGroupDosage = sheetView.findViewById(R.id.chipGroupForm);
        com.google.android.material.chip.ChipGroup chipGroupTarget = sheetView.findViewById(R.id.chipGroupAudience);
        android.widget.Button btnApply = sheetView.findViewById(R.id.btnApplyFilter);
        android.widget.TextView txtReset = sheetView.findViewById(R.id.txtResetFilter);

        btnApply.setOnClickListener(v -> {

            int selectedDosageId = chipGroupDosage.getCheckedChipId();
            int selectedTargetId = chipGroupTarget.getCheckedChipId();

            selectedDosage = selectedDosageId != android.view.View.NO_ID ?
                    ((com.google.android.material.chip.Chip) sheetView.findViewById(selectedDosageId)).getText().toString().trim() : "";

            selectedTarget = selectedTargetId != android.view.View.NO_ID ?
                    ((com.google.android.material.chip.Chip) sheetView.findViewById(selectedTargetId)).getText().toString().trim() : "";

            String currentKeyword = edtSearch.getText().toString();
            searchAdapter.applyAdvancedFilter(currentKeyword, selectedDosage, selectedTarget);

            if (searchAdapter.getItemCount() == 0) {
                android.widget.Toast.makeText(getContext(), "Không có thuốc nào phù hợp với bộ lọc này!", android.widget.Toast.LENGTH_SHORT).show();
            }

            bottomSheetDialog.dismiss();
        });

        txtReset.setOnClickListener(v -> {
            chipGroupDosage.clearCheck();
            chipGroupTarget.clearCheck();
            selectedDosage = "";
            selectedTarget = "";
        });

        bottomSheetDialog.show();
    }
}