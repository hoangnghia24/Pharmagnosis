package hcmute.edu.vn.pharmagnosis.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import hcmute.edu.vn.pharmagnosis.R;

public class EditMedicineFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imgBack = view.findViewById(R.id.img_back);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSave = view.findViewById(R.id.btn_save);

        // Nút Back trên Header
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        // Nút Hủy
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        // Nút Lưu
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                // TODO: Xử lý logic lưu dữ liệu lên Firebase ở đây
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        }
    }
}