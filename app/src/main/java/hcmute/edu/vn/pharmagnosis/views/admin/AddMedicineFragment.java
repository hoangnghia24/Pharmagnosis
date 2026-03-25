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

public class AddMedicineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Trỏ tới file layout thêm thuốc
        return inflater.inflate(R.layout.fragment_add_medicine, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ icon menu từ giao diện của Fragment
        ImageView imgMenu = view.findViewById(R.id.img_menu);
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                // Gọi hàm openSidebar() từ Activity cha
                ((AdminDashboardFragment) requireActivity()).openSidebar();
            });
        }
    }
}