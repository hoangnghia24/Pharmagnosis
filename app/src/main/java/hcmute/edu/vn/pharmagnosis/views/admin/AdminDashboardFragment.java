package hcmute.edu.vn.pharmagnosis.views.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import hcmute.edu.vn.pharmagnosis.MainActivity;
import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.views.activities.LoginActivity;
import hcmute.edu.vn.pharmagnosis.views.activities.PharmacyMapActivity;
import hcmute.edu.vn.pharmagnosis.views.admin.medicines.AddMedicineFragment;
import hcmute.edu.vn.pharmagnosis.views.admin.medicines.ManageMedicinesFragment;
import hcmute.edu.vn.pharmagnosis.views.admin.news.AddNewsFragment;
import hcmute.edu.vn.pharmagnosis.views.admin.news.ManageNewsFragment;

public class AdminDashboardFragment extends AppCompatActivity {

    private DrawerLayout drawerLayoutAdmin;
    private View itemDashboard;
    private View itemManageDrugs;
    private BottomNavigationView bottomNavAdmin;
    private View itemManageNews;
    private MaterialCardView btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // 1. Ánh xạ các thành phần chính
        drawerLayoutAdmin = findViewById(R.id.drawer_layout_admin);
        bottomNavAdmin = findViewById(R.id.bottom_nav_admin);
        bottomNavAdmin.setItemIconTintList(null); // Mặc định chọn "Tổng quan"


        // 2. Thiết lập sự kiện cho Sidebar và Bottom Nav
        setupSidebarActions();
        setupBottomNavigation();

        // 3. Hiển thị trang Tổng quan mặc định khi vừa mở App
        if (savedInstanceState == null) {
            replaceFragment(new DashboardFragment());
            highlightSidebarItem(itemDashboard); // Làm sáng mục Sidebar
        }

        setupOnBackPressed();
    }

    // ================= XỬ LÝ BOTTOM NAVIGATION =================
    private void setupBottomNavigation() {
        bottomNavAdmin.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_overview) {
                android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    // ================= XỬ LÝ SIDEBAR =================
    private void setupSidebarActions() {
        itemDashboard = findViewById(R.id.item_dashboard);
        itemManageDrugs = findViewById(R.id.item_manage_drugs);
        itemManageNews = findViewById(R.id.item_manage_news);
        btnLogout = findViewById(R.id.btn_logout);
        if (itemDashboard != null) {
            itemDashboard.setOnClickListener(v -> {
                replaceFragment(new DashboardFragment());
                highlightSidebarItem(itemDashboard);
                closeSidebar();
            });
        }

        if (itemManageDrugs != null) {
            itemManageDrugs.setOnClickListener(v -> {
                replaceFragment(new ManageMedicinesFragment());
                highlightSidebarItem(itemManageDrugs);
                closeSidebar();
            });
        }
        if(itemManageNews != null) {
            itemManageNews.setOnClickListener(v -> {
                replaceFragment(new ManageNewsFragment());
                highlightSidebarItem(itemManageNews);
                closeSidebar();
            });
        }
        if(btnLogout != null){
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

    }

    private void highlightSidebarItem(View selectedItem) {
        int defaultColor = Color.TRANSPARENT;
        int activeColor = Color.parseColor("#1E3A5F");
        resetItemColor(itemDashboard, defaultColor);
        resetItemColor(itemManageDrugs, defaultColor);
        resetItemColor(itemManageNews, defaultColor);
        resetItemColor(selectedItem, activeColor);
    }

    private void resetItemColor(View item, int color) {
        if (item != null) {
            if (item instanceof MaterialCardView) {
                ((MaterialCardView) item).setCardBackgroundColor(color);
            } else {
                item.setBackgroundColor(color);
            }
        }
    }

    // ================= CÁC HÀM PHỤ TRỢ CHUNG =================
    public void replaceFragment(androidx.fragment.app.Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        // Nếu là các màn hình con (Thêm, Sửa) thì truyền true để lưu lịch sử
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    // Hàm cũ giữ nguyên để dùng cho các mục trên Sidebar (không cần Backstack)
    public void replaceFragment(androidx.fragment.app.Fragment fragment) {
        replaceFragment(fragment, false);
    }

    public void openSidebar() {
        if (drawerLayoutAdmin != null) {
            drawerLayoutAdmin.openDrawer(GravityCompat.START);
        }
    }

    private void closeSidebar() {
        if (drawerLayoutAdmin != null && drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutAdmin.closeDrawer(GravityCompat.START);
        }
    }

    private void setupOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayoutAdmin != null && drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
                    drawerLayoutAdmin.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}