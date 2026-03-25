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

public class AddDiseaseFragment extends Fragment { // Đổi tên class cho EditDiseaseFragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_disease, container, false); // Trỏ đúng file layout tương ứng
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnCancel = view.findViewById(R.id.btn_cancel);
        ImageView imgMenu = view.findViewById(R.id.img_menu);
        View.OnClickListener goBack = v -> requireActivity().getOnBackPressedDispatcher().onBackPressed();


        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                // Gọi hàm openSidebar() từ Activity cha
                ((AdminDashboardFragment) requireActivity()).openSidebar();
            });
        }
        if (btnCancel != null) btnCancel.setOnClickListener(goBack);
    }
}