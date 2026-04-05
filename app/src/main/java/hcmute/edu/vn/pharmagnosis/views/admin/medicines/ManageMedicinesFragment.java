package hcmute.edu.vn.pharmagnosis.views.admin.medicines;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.admin.medicines.MedicineAdapter;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.repositories.MedicineRepository;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class ManageMedicinesFragment extends Fragment {

    private MedicineAdapter adapter;
    private MedicineRepository repository;
    private List<Medicine> originalMedicineList = new ArrayList<>(); // Lưu danh sách gốc để tìm kiếm

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_medicines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new MedicineRepository();

        ImageView imgMenu = view.findViewById(R.id.img_menu);
        ImageView imgAddMedicine = view.findViewById(R.id.img_add_medicine);
        RecyclerView rvMedicines = view.findViewById(R.id.rv_medicines);
        EditText etSearchMedicine = view.findViewById(R.id.et_search_medicine); // Lấy đúng ID từ XML

        // Thiết lập RecyclerView
        rvMedicines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicineAdapter(new MedicineAdapter.OnMedicineActionListener() {
            @Override
            public void onEditClick(Medicine medicine) {
                // 1. Chuyển đối tượng Medicine thành chuỗi JSON bằng Gson
                Gson gson = new Gson();
                String medicineJson = gson.toJson(medicine);

                // 2. Đóng gói vào Bundle
                Bundle bundle = new Bundle();
                bundle.putString("medicine_json", medicineJson);

                // 3. Khởi tạo Fragment và gắn Bundle chứa dữ liệu vào
                EditMedicineFragment editFragment = new EditMedicineFragment();
                editFragment.setArguments(bundle); // DÒNG QUAN TRỌNG ĐÃ ĐƯỢC THÊM LẠI

                // 4. Chuyển màn hình
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onDeleteClick(Medicine medicine) {
                showDeleteConfirmDialog(medicine);
            }

            @Override
            public void onItemClick(Medicine medicine) {
                android.content.Intent intent = new android.content.Intent(getContext(), hcmute.edu.vn.pharmagnosis.views.user.MedicineDetailActivity.class);
                intent.putExtra("medicine_json", new Gson().toJson(medicine));
                startActivity(intent);
            }
        });
        rvMedicines.setAdapter(adapter);

        // Tải dữ liệu
        loadMedicines();

        // Thiết lập tìm kiếm
        if (etSearchMedicine != null) {
            setupSearch(etSearchMedicine);
        }

        // Sự kiện các nút
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).openSidebar());
        }

        if (imgAddMedicine != null) {
            imgAddMedicine.setOnClickListener(v -> {
                ((AdminDashboardFragment) requireActivity()).replaceFragment(new AddMedicineFragment(), true);
            });
        }
    }

    private void loadMedicines() {
        repository.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines != null) {
                originalMedicineList = medicines; // Lưu lại data gốc
                adapter.setMedicineList(medicines); // Hiển thị data
            }
        });
    }

    private void setupSearch(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedicines(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterMedicines(String text) {
        List<Medicine> filteredList = new ArrayList<>();
        String searchKeyword = removeAccents(text);

        for (Medicine medicine : originalMedicineList) {
            if (medicine.getMedicineName() != null) {
                String medicineName = removeAccents(medicine.getMedicineName());
                if (medicineName.contains(searchKeyword)) {
                    filteredList.add(medicine);
                }
            }
        }
        adapter.setMedicineList(filteredList);
    }

    // Hàm hỗ trợ bỏ dấu tiếng Việt
    private String removeAccents(String str) {
        if (str == null) return "";
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("")
                    .replace('đ', 'd').replace('Đ', 'D')
                    .toLowerCase();
        } catch (Exception e) {
            return str.toLowerCase();
        }
    }

    private void showDeleteConfirmDialog(Medicine medicine) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa thuốc")
                .setMessage("Bạn có chắc chắn muốn xóa thuốc " + medicine.getMedicineName() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    repository.deleteMedicine(medicine.getMedicineId(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                            loadMedicines();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}