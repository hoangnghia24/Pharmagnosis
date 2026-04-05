package hcmute.edu.vn.pharmagnosis.views.admin.news;

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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.admin.news.NewsAdapterAdmin;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.repositories.NewsRepository;
import hcmute.edu.vn.pharmagnosis.views.admin.AdminDashboardFragment;

public class ManageNewsFragment extends Fragment {

    private NewsRepository repository;
    private RecyclerView rvNews;
    private NewsAdapterAdmin adapter;
    private List<HealthNews> originalNewsList = new ArrayList<>(); // Lưu danh sách gốc để tìm kiếm

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
        EditText etSearchNews = view.findViewById(R.id.et_search_news); // Lấy đúng ID từ XML
        rvNews = view.findViewById(R.id.rv_news);

        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khởi tạo Adapter 1 lần duy nhất
        setupAdapter();

        // Mở sidebar
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).openSidebar());
        }

        // Chuyển sang trang Thêm Tin Tức
        if (imgAddNews != null) {
            imgAddNews.setOnClickListener(v -> ((AdminDashboardFragment) requireActivity()).replaceFragment(new AddNewsFragment(), true));
        }

        // Thiết lập sự kiện gõ phím tìm kiếm
        if (etSearchNews != null) {
            setupSearch(etSearchNews);
        }

        loadNewsData();
    }

    private void setupAdapter() {
        adapter = new NewsAdapterAdmin(new ArrayList<>(), new NewsAdapterAdmin.OnNewsItemClickListener() {
            @Override
            public void onEditClick(HealthNews news) {
                EditNewsFragment editFragment = new EditNewsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_news", news);
                editFragment.setArguments(bundle);
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
                                    adapter.removeNews(news);
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
                android.content.Intent intent = new android.content.Intent(getContext(), hcmute.edu.vn.pharmagnosis.views.user.NewsDetailActivity.class);
                intent.putExtra("NEWS_TITLE", news.getTitle());
                intent.putExtra("NEWS_CONTENT", news.getContent());
                intent.putExtra("NEWS_IMAGE", news.getImage());
                startActivity(intent);
            }
        });
        rvNews.setAdapter(adapter);
    }

    private void loadNewsData() {
        repository.getNewsFromFirebase().observe(getViewLifecycleOwner(), newsList -> {
            if (newsList != null) {
                originalNewsList = newsList; // Lưu data gốc
                adapter.setNewsList(newsList); // Cập nhật lên UI
            }
        });
    }

    private void setupSearch(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNews(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterNews(String text) {
        List<HealthNews> filteredList = new ArrayList<>();
        String searchKeyword = removeAccents(text);

        for (HealthNews news : originalNewsList) {
            if (news.getTitle() != null) {
                String newsTitle = removeAccents(news.getTitle());
                if (newsTitle.contains(searchKeyword)) {
                    filteredList.add(news);
                }
            }
        }

        if (adapter != null) {
            adapter.setNewsList(filteredList);
        }
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
}