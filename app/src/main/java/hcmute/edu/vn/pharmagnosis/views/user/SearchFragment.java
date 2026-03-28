package hcmute.edu.vn.pharmagnosis.views.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.MedicineSearchAdapter;
import hcmute.edu.vn.pharmagnosis.adapters.SearchHistoryAdapter;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.SearchViewModel;

public class SearchFragment extends Fragment {

    private String selectedDosage = "";
    private String selectedTarget = "";
    private ImageView imgBack;
    private ImageView imgFilter;
    private ImageView imgClearSearch;
    private SearchViewModel searchViewModel;
    
    private MedicineSearchAdapter searchAdapter;
    private RecyclerView recyclerSearchResults;
    
    private SearchHistoryAdapter historyAdapter;
    private RecyclerView recyclerSearchHistory;
    private View scrollInitialState;
    private TextView txtHistoryTitle;

    private EditText edtSearch;
    private com.google.android.material.chip.Chip chipParacetamol, chipIbuprofen, chipMenTieuHoa, chipOresol;
    private List<Medicine> allMedicines = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupRecyclerViews();
        setupViewModel();
        setupListeners();
        setupSearchLogic();

        return view;
    }

    private void initViews(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgFilter = view.findViewById(R.id.imgFilter);
        imgClearSearch = view.findViewById(R.id.imgClearSearch);
        edtSearch = view.findViewById(R.id.edtSearchInput);
        
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults);
        recyclerSearchHistory = view.findViewById(R.id.recyclerSearchHistory);
        scrollInitialState = view.findViewById(R.id.scrollInitialState);
        txtHistoryTitle = view.findViewById(R.id.txtHistoryTitle);

        chipParacetamol = view.findViewById(R.id.chipParacetamol);
        chipIbuprofen = view.findViewById(R.id.chipIbuprofen);
        chipMenTieuHoa = view.findViewById(R.id.chipMenTieuHoa);
        chipOresol = view.findViewById(R.id.chipOresol);
    }

    private void setupRecyclerViews() {
        // Kết quả tìm kiếm
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new MedicineSearchAdapter(new ArrayList<>());
        recyclerSearchResults.setAdapter(searchAdapter);

        // Lịch sử tìm kiếm
        recyclerSearchHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SearchHistoryAdapter(new ArrayList<>(), new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(SearchRecord record) {
                edtSearch.setText(record.getKeyword());
                edtSearch.setSelection(record.getKeyword().length());
                performSearch(record.getKeyword());
            }

            @Override
            public void onDeleteClick(SearchRecord record) {
                searchViewModel.deleteHistoryRecord(record);
            }
        });
        recyclerSearchHistory.setAdapter(historyAdapter);
    }

    private void setupViewModel() {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        
        // Quan sát danh sách thuốc
        searchViewModel.getMedicinesLiveData().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines != null) {
                allMedicines = medicines;
                searchAdapter.setMedicines(medicines);
            }
        });

        // Quan sát lịch sử tìm kiếm
        searchViewModel.getHistoryLiveData().observe(getViewLifecycleOwner(), history -> {
            if (history != null && !history.isEmpty()) {
                historyAdapter.setHistoryList(history);
                txtHistoryTitle.setVisibility(View.VISIBLE);
                recyclerSearchHistory.setVisibility(View.VISIBLE);
            } else {
                txtHistoryTitle.setVisibility(View.GONE);
                recyclerSearchHistory.setVisibility(View.GONE);
            }
        });
    }

    private void setupListeners() {
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        if (imgFilter != null) {
            imgFilter.setOnClickListener(v -> showFilterBottomSheet());
        }

        if (imgClearSearch != null) {
            imgClearSearch.setOnClickListener(v -> {
                edtSearch.setText("");
                toggleUI(false);
            });
        }

        if (chipParacetamol != null) chipParacetamol.setOnClickListener(v -> onChipClick("Paracetamol"));
        if (chipIbuprofen != null) chipIbuprofen.setOnClickListener(v -> onChipClick("Ibuprofen"));
        if (chipMenTieuHoa != null) chipMenTieuHoa.setOnClickListener(v -> onChipClick("Men tiêu hóa"));
        if (chipOresol != null) chipOresol.setOnClickListener(v -> onChipClick("Oresol"));
    }

    private void onChipClick(String keyword) {
        edtSearch.setText(keyword);
        edtSearch.setSelection(keyword.length());
        performSearch(keyword);
    }

    private void setupSearchLogic() {
        imgClearSearch.setVisibility(View.GONE);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    toggleUI(false);
                    imgClearSearch.setVisibility(View.GONE);
                } else {
                    toggleUI(true);
                    imgClearSearch.setVisibility(View.VISIBLE);
                    searchAdapter.applyAdvancedFilter(query, selectedDosage, selectedTarget);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lưu lịch sử khi người dùng nhấn Enter trên bàn phím
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchViewModel.addSearchHistory(query);
                }
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        toggleUI(true);
        searchAdapter.applyAdvancedFilter(query, selectedDosage, selectedTarget);
        searchViewModel.addSearchHistory(query);
    }

    private void toggleUI(boolean isSearching) {
        if (isSearching) {
            scrollInitialState.setVisibility(View.GONE);
            recyclerSearchResults.setVisibility(View.VISIBLE);
        } else {
            scrollInitialState.setVisibility(View.VISIBLE);
            recyclerSearchResults.setVisibility(View.GONE);
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

            selectedDosage = selectedDosageId != View.NO_ID ?
                    ((com.google.android.material.chip.Chip) sheetView.findViewById(selectedDosageId)).getText().toString().trim() : "";

            selectedTarget = selectedTargetId != View.NO_ID ?
                    ((com.google.android.material.chip.Chip) sheetView.findViewById(selectedTargetId)).getText().toString().trim() : "";

            String currentKeyword = edtSearch.getText().toString();
            if (!currentKeyword.isEmpty()) {
                toggleUI(true);
                searchAdapter.applyAdvancedFilter(currentKeyword, selectedDosage, selectedTarget);
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
