package hcmute.edu.vn.pharmagnosis.views.admin;

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

public class ManageNewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imgMenu = view.findViewById(R.id.img_menu);
        ImageView imgAddNews = view.findViewById(R.id.img_add_news);
        RecyclerView rvNews = view.findViewById(R.id.rv_news);

        // Chỉ khởi tạo LayoutManager, CHƯA set Adapter theo yêu cầu của bạn
        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Mở sidebar
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                ((AdminDashboardFragment) requireActivity()).openSidebar();
            });
        }

        // Chuyển sang trang Thêm Tin Tức
        if (imgAddNews != null) {
            imgAddNews.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).replaceFragment(new AddNewsFragment(), true));
        }
    }
}