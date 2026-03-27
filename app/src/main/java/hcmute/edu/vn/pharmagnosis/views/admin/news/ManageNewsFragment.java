package hcmute.edu.vn.pharmagnosis.views.admin.news;

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

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.NewsAdapter;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.repositories.NewsRepository;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class ManageNewsFragment extends Fragment {

    private NewsRepository repository;
    private RecyclerView rvNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new NewsRepository();

        ImageView imgMenu = view.findViewById(R.id.img_menu);
        ImageView imgAddNews = view.findViewById(R.id.img_add_news);
        rvNews = view.findViewById(R.id.rv_news);

        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Mở sidebar
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).openSidebar());
        }

        // Chuyển sang trang Thêm Tin Tức
        if (imgAddNews != null) {
            imgAddNews.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).replaceFragment(new AddNewsFragment(), true));
        }
        loadNewsData();
    }

    private void loadNewsData() {
        repository.getNewsFromFirebase().observe(getViewLifecycleOwner(), newsList -> {
            if (newsList != null) {
                NewsAdapter adapter = new NewsAdapter(newsList, new NewsAdapter.OnNewsItemClickListener() {
                    @Override
                    public void onEditClick(HealthNews news) {
                        // 1. Khởi tạo Fragment Sửa
                        EditNewsFragment editFragment = new EditNewsFragment();

                        // 2. Gói dữ liệu bài đăng vào Bundle để gửi sang màn hình sửa
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selected_news", news);
                        editFragment.setArguments(bundle);

                        // 3. Sử dụng hàm replaceFragment giống như cách bạn dùng ở nút Thêm Tin Tức
                        ((AdminDashboardFragment) requireActivity()).replaceFragment(editFragment, true);
                    }

                    @Override
                    public void onDeleteClick(HealthNews news) {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận xóa")
                                .setMessage("Bạn có chắc chắn muốn xóa bài viết:\n'" + news.getTitle() + "' không?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Xóa", (dialog, which) -> {

                                    Toast.makeText(getContext(), "Đang xóa...", Toast.LENGTH_SHORT).show();

                                    repository.deleteNews(news, task -> {
                                        if (task != null && task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Đã xóa bài viết thành công!", Toast.LENGTH_SHORT).show();

                                            // CÁCH SỬA: Lấy Adapter trực tiếp từ rvNews và ép kiểu về NewsAdapter
                                            if (rvNews.getAdapter() != null) {
                                                ((NewsAdapter) rvNews.getAdapter()).removeNews(news);
                                            }

                                        } else {
                                            Toast.makeText(getContext(), "Lỗi khi xóa bài viết. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                })
                                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                .show();
                    }

                    @Override
                    public void onItemClick(HealthNews news) {
                        // Logic xem chi tiết y hệt như cũ của bạn
                        android.content.Intent intent = new android.content.Intent(getContext(), hcmute.edu.vn.pharmagnosis.views.user.NewsDetailActivity.class);
                        intent.putExtra("NEWS_TITLE", news.getTitle());
                        intent.putExtra("NEWS_CONTENT", news.getContent());
                        intent.putExtra("NEWS_IMAGE", news.getImage());
                        startActivity(intent);
                    }
                });
                rvNews.setAdapter(adapter);
            }
        });
    }
}