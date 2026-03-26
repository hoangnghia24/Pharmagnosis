package hcmute.edu.vn.pharmagnosis.views.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.NewsAdapter;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.UserDashboardViewModel;

public class UserDashboardFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    // 1. Khai báo các thành phần giao diện
    private EditText edtSearch;
    private CardView cardPharmacy;
    private CardView cardDisease;
    private CardView cardBmi;
    private CardView cardSchedule;
    private TextView txtViewAllNews;
    private RecyclerView recyclerNews;

    private NewsAdapter newsAdapter;
    private List<HealthNews> mockNewsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp giao diện fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initViews(view);
        
        // Sửa lỗi: Phải gọi dòng này sau khi đã findViewById (trong initViews)
        if (bottomNavigationView != null) {
            bottomNavigationView.setItemIconTintList(null);
        }
        
        setupListeners();
        setupRecyclerView();

        return view;
    }

    // Ánh xạ View
    private void initViews(View view) {
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        edtSearch = view.findViewById(R.id.edtSearch);
        cardPharmacy = view.findViewById(R.id.cardPharmacy);
        cardDisease = view.findViewById(R.id.cardDisease);
        cardBmi = view.findViewById(R.id.cardBmi);
        cardSchedule = view.findViewById(R.id.cardSchedule);
        txtViewAllNews = view.findViewById(R.id.txtViewAllNews);
        recyclerNews = view.findViewById(R.id.recyclerNews);
    }

    // Cài đặt sự kiện Click
    private void setupListeners() {
        // --- XỬ LÝ THANH TÌM KIẾM ---
        // Ngăn bàn phím tự động bật lên ở màn hình Trang chủ
        edtSearch.setFocusable(false);
        edtSearch.setClickable(true);

        // Chuyển sang SearchFragment khi bấm vào ô tìm kiếm
        edtSearch.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // LƯU Ý: R.id.fragment_container là ID của thẻ FrameLayout chứa Fragment trong activity_main.xml
            // Nếu bạn đặt tên khác, hãy sửa lại chỗ này nhé!
            transaction.replace(R.id.fragment_container, searchFragment);
            transaction.addToBackStack(null); // Cho phép bấm nút Back để quay lại trang chủ
            transaction.commit();
        });

        // --- XỬ LÝ CÁC NÚT KHÁC (Để sẵn khung chờ bạn code sau) ---
        cardPharmacy.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở bản đồ Nhà thuốc", Toast.LENGTH_SHORT).show();
        });

        cardDisease.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở danh sách Bệnh lý", Toast.LENGTH_SHORT).show();
        });

        cardBmi.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở máy tính BMI", Toast.LENGTH_SHORT).show();
        });

        txtViewAllNews.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Xem tất cả tin tức", Toast.LENGTH_SHORT).show();
        });
    }

    // Cài đặt danh sách Tin tức
    private void setupRecyclerView() {
        // Cài đặt chiều lướt ngang cho RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerNews.setLayoutManager(layoutManager);

        // Khởi tạo Bếp trưởng (ViewModel)
        UserDashboardViewModel viewModel = new androidx.lifecycle.ViewModelProvider(this).get(UserDashboardViewModel.class);

        // "Lắng nghe" dữ liệu từ Firebase
        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), newsList -> {
            // Bất cứ khi nào Firebase có dữ liệu (hoặc bạn vừa thêm bài báo mới trên web),
            // đoạn code này sẽ tự động chạy để cập nhật lại giao diện ngay lập tức!
            if (newsList != null && !newsList.isEmpty()) {
                newsAdapter = new NewsAdapter(newsList);
                recyclerNews.setAdapter(newsAdapter);
            }
        });
    }
}