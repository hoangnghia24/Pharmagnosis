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

import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.NewsAdapter;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.UserDashboardViewModel;

public class UserDashboardFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        setupRecyclerView();
    }

    private void initViews(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        cardPharmacy = view.findViewById(R.id.cardPharmacy);
        cardDisease = view.findViewById(R.id.cardDisease);
        cardBmi = view.findViewById(R.id.cardBmi);
        cardSchedule = view.findViewById(R.id.cardSchedule);
        txtViewAllNews = view.findViewById(R.id.txtViewAllNews);
        recyclerNews = view.findViewById(R.id.recyclerNews);
    }

    private void setupListeners() {
        edtSearch.setFocusable(false);
        edtSearch.setClickable(true);

        edtSearch.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, searchFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        cardPharmacy.setOnClickListener(v -> Toast.makeText(getContext(), "Mở bản đồ Nhà thuốc", Toast.LENGTH_SHORT).show());
        cardDisease.setOnClickListener(v -> Toast.makeText(getContext(), "Mở danh sách Bệnh lý", Toast.LENGTH_SHORT).show());

        cardBmi.setOnClickListener(v -> {
            BmiCalculatorFragment bmiFragment = new BmiCalculatorFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, bmiFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        txtViewAllNews.setOnClickListener(v -> Toast.makeText(getContext(), "Xem tất cả tin tức", Toast.LENGTH_SHORT).show());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerNews.setLayoutManager(layoutManager);

        UserDashboardViewModel viewModel = new androidx.lifecycle.ViewModelProvider(this).get(UserDashboardViewModel.class);

        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), newsList -> {
            if (newsList != null && !newsList.isEmpty()) {
                newsAdapter = new NewsAdapter(newsList);
                recyclerNews.setAdapter(newsAdapter);
            }
        });
    }
}