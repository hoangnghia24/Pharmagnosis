package hcmute.edu.vn.pharmagnosis.views.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView; // Đã thêm thư viện ImageView cho bạn

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.MedicineSearchAdapter;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.SearchViewModel;

public class SearchFragment extends Fragment {

    private ImageView imgBack;
    private SearchViewModel searchViewModel;
    private MedicineSearchAdapter searchAdapter;
    private RecyclerView recyclerSearch;
    private EditText edtSearch;
    private android.widget.TextView chipParacetamol, chipIbuprofen, chipMenTieuHoa, chipOresol;
    private java.util.List<hcmute.edu.vn.pharmagnosis.models.Medicine> allMedicines = new java.util.ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Phải NẠP GIAO DIỆN TRƯỚC thì mới có biến 'view' để dùng
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // 2. Bây giờ mới đi tìm các thành phần trên giao diện
        imgBack = view.findViewById(R.id.imgBack);
        recyclerSearch = view.findViewById(R.id.recyclerSearchHistory);
        edtSearch = view.findViewById(R.id.edtSearchInput);
        chipParacetamol = view.findViewById(R.id.chipParacetamol);
        chipIbuprofen = view.findViewById(R.id.chipIbuprofen);
        // Chú ý: Bạn nhớ kiểm tra lại ID của nút Men tiêu hóa xem có đúng là chipMenTieuHoa không nhé
        chipMenTieuHoa = view.findViewById(R.id.chipMenTieuHoa);
        chipOresol = view.findViewById(R.id.chipOresol);

        // Bắt sự kiện click để nhảy cóc sang trang Chi tiết
        if (chipParacetamol != null) {
            chipParacetamol.setOnClickListener(v -> openMedicineDetailFast("Paracetamol"));
        }
        if (chipIbuprofen != null) {
            chipIbuprofen.setOnClickListener(v -> openMedicineDetailFast("Ibuprofen"));
        }
        if (chipMenTieuHoa != null) {
            chipMenTieuHoa.setOnClickListener(v -> openMedicineDetailFast("Men"));
        }
        if (chipOresol != null) {
            chipOresol.setOnClickListener(v -> openMedicineDetailFast("Oresol"));
        }
        // 3. Cài đặt sự kiện bấm nút quay lại
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> {
                // Lệnh lùi về màn hình trước đó
                requireActivity().onBackPressed();
            });
        }

        // 4. Gọi các hàm cài đặt danh sách và tìm kiếm
        setupRecyclerView();
        setupViewModel();
        setupSearchLogic();

        return view; // Trả về giao diện đã hoàn thiện
    }

    private void setupRecyclerView() {
        // Danh sách thuốc sẽ cuộn dọc
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        // Ban đầu khởi tạo Adapter với danh sách rỗng (đợi Firebase tải về)
        searchAdapter = new MedicineSearchAdapter(new java.util.ArrayList<>());
        recyclerSearch.setAdapter(searchAdapter);
    }

    private void setupViewModel() {
        // Gọi Bếp trưởng
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Lắng nghe dữ liệu từ kho Firebase
        searchViewModel.getMedicinesLiveData().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines != null) {
                // Bơm toàn bộ thuốc vào Adapter
                allMedicines = medicines;
                searchAdapter.setMedicines(medicines);
            }
        });
    }

    private void setupSearchLogic() {
        // Lắng nghe TỪNG KÝ TỰ người dùng gõ vào thanh tìm kiếm
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Truyền chữ người dùng vừa gõ vào "Bộ lọc" của Adapter
                    if (searchAdapter != null) {
                        searchAdapter.getFilter().filter(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    private void openMedicineDetailFast(String keyword) {
        if (allMedicines == null || allMedicines.isEmpty()) {
            android.widget.Toast.makeText(getContext(), "Đang tải dữ liệu, vui lòng đợi chút...", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        hcmute.edu.vn.pharmagnosis.models.Medicine foundMedicine = null;
        for (hcmute.edu.vn.pharmagnosis.models.Medicine m : allMedicines) {
            // Tìm thuốc có tên hoặc tên thương mại chứa từ khóa
            if (m.getMedicineName().toLowerCase().contains(keyword.toLowerCase()) ||
                    (m.getTradeName() != null && m.getTradeName().toLowerCase().contains(keyword.toLowerCase()))) {
                foundMedicine = m;
                break; // Tìm thấy phát là dừng luôn
            }
        }

        if (foundMedicine != null) {
            // Đóng gói và chuyển trang y hệt bên Adapter
            android.content.Intent intent = new android.content.Intent(getContext(), hcmute.edu.vn.pharmagnosis.views.user.MedicineDetailActivity.class);
            intent.putExtra("MEDICINE_OBJ", (java.io.Serializable) foundMedicine);
            startActivity(intent);
        } else {
            android.widget.Toast.makeText(getContext(), "Không tìm thấy thuốc này trong kho!", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}