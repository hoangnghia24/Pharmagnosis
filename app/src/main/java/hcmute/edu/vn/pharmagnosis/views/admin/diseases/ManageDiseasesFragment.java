package hcmute.edu.vn.pharmagnosis.views.admin.diseases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class ManageDiseasesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_diseases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imgMenu = view.findViewById(R.id.img_menu);
        ImageView imgAddDisease = view.findViewById(R.id.img_add_disease);
        RecyclerView rvDiseases = view.findViewById(R.id.rv_diseases);

        rvDiseases.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                ((AdminDashboardFragment) requireActivity()).openSidebar();
            });
        }
        if (imgAddDisease != null) {
            imgAddDisease.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).replaceFragment(new AddDiseaseFragment(), true));
        }
    }
}