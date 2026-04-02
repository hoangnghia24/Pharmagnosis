package hcmute.edu.vn.pharmagnosis.views.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.NewsAdapter;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.UserDashboardViewModel;

public class AllNewsFragment extends Fragment {

    private RecyclerView rvAllNews;
    private ImageView imgBack;
    private NewsAdapter newsAdapter;
    private UserDashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rvAllNews = view.findViewById(R.id.rv_all_news);
        imgBack = view.findViewById(R.id.img_back);
        
        imgBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        rvAllNews.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel = new ViewModelProvider(this).get(UserDashboardViewModel.class);
        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), (List<HealthNews> newsList) -> {
            if (newsList != null) {
                newsAdapter = new NewsAdapter(newsList) {
                    @NonNull
                    @Override
                    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
                        ViewGroup.LayoutParams lp = view.getLayoutParams();
                        if (lp != null) {
                            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            view.setLayoutParams(lp);
                        }
                        return new NewsViewHolder(view);
                    }
                };
                rvAllNews.setAdapter(newsAdapter);
            }
        });
    }
}