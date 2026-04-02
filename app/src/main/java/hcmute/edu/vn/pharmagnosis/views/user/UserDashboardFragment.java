package hcmute.edu.vn.pharmagnosis.views.user;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.adapters.NewsAdapter;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;
import hcmute.edu.vn.pharmagnosis.viewmodels.ProfileViewModel;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.UserDashboardViewModel;
import hcmute.edu.vn.pharmagnosis.views.activities.PharmacyMapActivity;

public class UserDashboardFragment extends Fragment {

    private EditText edtSearch;
    private CardView cardPharmacy, cardBmi;
    private TextView txtViewAllNews;
    private RecyclerView recyclerNews;
    
    // User Profile Views
    private TextView txtUserName;
    private ImageView imgAvatar;
    
    // BMI Views
    private TextView txtBmiValue, txtBmiStatus;

    private NewsAdapter newsAdapter;
    private ProfileViewModel profileViewModel;

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
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        setupListeners();
        setupRecyclerView();
        observeUserData();
    }

    private void initViews(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        cardPharmacy = view.findViewById(R.id.cardPharmacy);
        cardBmi = view.findViewById(R.id.cardBmi);
        txtViewAllNews = view.findViewById(R.id.txtViewAllNews);
        recyclerNews = view.findViewById(R.id.recyclerNews);
        
        // Ánh xạ Profile views
        txtUserName = view.findViewById(R.id.txtUserName);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        
        txtBmiValue = view.findViewById(R.id.txtBmiValue);
        txtBmiStatus = view.findViewById(R.id.txtBmiStatus);
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

        cardPharmacy.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), PharmacyMapActivity.class);
            startActivity(intent);
        });

        cardBmi.setOnClickListener(v -> {
            BmiCalculatorFragment bmiFragment = new BmiCalculatorFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, bmiFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        txtViewAllNews.setOnClickListener(v -> {
            AllNewsFragment allNewsFragment = new AllNewsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, allNewsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void observeUserData() {
        profileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Cập nhật tên người dùng
                if (user.getFullNAme() != null && !user.getFullNAme().isEmpty()) {
                    txtUserName.setText(user.getFullNAme());
                }
                
                // Cập nhật Avatar (nếu có)
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    Glide.with(this).load(user.getAvatar()).into(imgAvatar);
                }

                // Cập nhật BMI
                if (user.getBmi() > 0) updateBmiDisplay(user.getBmi());
            }
        });
    }

    private void updateBmiDisplay(double bmi) {
        txtBmiValue.setText(String.format(Locale.getDefault(), "%.1f", bmi));
        String status; int color; int bgColor;
        if (bmi < 18.5) { status = "Gầy"; color = 0xFF0369A1; bgColor = 0xFFE0F2FE; }
        else if (bmi < 25) { status = "Bình thường"; color = 0xFF15803D; bgColor = 0xFFDCFCE7; }
        else if (bmi < 30) { status = "Thừa cân"; color = 0xFFA16207; bgColor = 0xFFFEF9C3; }
        else { status = "Béo phì"; color = 0xFFB91C1C; bgColor = 0xFFFEE2E2; }
        txtBmiStatus.setText(status);
        txtBmiStatus.setTextColor(color);
        txtBmiStatus.setBackgroundTintList(ColorStateList.valueOf(bgColor));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerNews.setLayoutManager(layoutManager);
        UserDashboardViewModel viewModel = new ViewModelProvider(this).get(UserDashboardViewModel.class);
        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), (List<HealthNews> newsList) -> {
            if (newsList != null && !newsList.isEmpty()) {
                newsAdapter = new NewsAdapter(newsList);
                recyclerNews.setAdapter(newsAdapter);
            }
        });
    }
}