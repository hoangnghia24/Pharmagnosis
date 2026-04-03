package hcmute.edu.vn.pharmagnosis.views.admin.medicines;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.admin.medicines.MedicineAdapter;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.repositories.MedicineRepository;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class ManageMedicinesFragment extends Fragment {

    private MedicineAdapter adapter;
    private MedicineRepository repository;

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

                // 3. Truyền Bundle sang EditMedicineFragment
                EditMedicineFragment editFragment = new EditMedicineFragment();
                editFragment.setArguments(bundle);

                ((AdminDashboardFragment) requireActivity()).replaceFragment(editFragment, true);
            }

            @Override
            public void onDeleteClick(Medicine medicine) {
                showDeleteConfirmDialog(medicine);
            }
            @Override
            public  void onItemClick(Medicine medicine) {
                android.content.Intent intent = new android.content.Intent(getContext(), hcmute.edu.vn.pharmagnosis.views.user.MedicineDetailActivity.class);
                intent.putExtra("medicine_json", new Gson().toJson(medicine));
                startActivity(intent);
            }
        });
        rvMedicines.setAdapter(adapter);

        // Lấy dữ liệu từ Firebase
        loadMedicines();

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
            adapter.setMedicineList(medicines);
        });
    }

    private void showDeleteConfirmDialog(Medicine medicine) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa thuốc")
                .setMessage("Bạn có chắc chắn muốn xóa thuốc " + medicine.getMedicineName() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    repository.deleteMedicine(medicine.getMedicineId(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                            loadMedicines(); // Tải lại danh sách
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}