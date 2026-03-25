package hcmute.edu.vn.pharmagnosis.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import hcmute.edu.vn.pharmagnosis.R;

// Tên class viết theo kiểu PascalCase, là Cụm danh từ theo quy chuẩn
public class ManageMedicinesFragment extends Fragment { // [cite: 3, 4]

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_medicines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Biến viết theo kiểu camelCase [cite: 15]
        ImageView imgMenu = view.findViewById(R.id.img_menu);
        ImageView imgAddMedicine = view.findViewById(R.id.img_add_medicine);

        // Mở menu sidebar
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                ((AdminDashboardFragment) requireActivity()).openSidebar();
            });
        }

        // Bấm dấu + để qua trang Thêm Thuốc
        if (imgAddMedicine != null) {
            imgAddMedicine.setOnClickListener(v -> {
                // Gọi hàm điều hướng đã được public ở Bước 1
                ((AdminDashboardFragment) requireActivity()).replaceFragment(new AddMedicineFragment());
            });
        }
    }
}