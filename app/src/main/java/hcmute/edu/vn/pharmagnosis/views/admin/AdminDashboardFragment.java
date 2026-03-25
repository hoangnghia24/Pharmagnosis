package hcmute.edu.vn.pharmagnosis.views.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import hcmute.edu.vn.pharmagnosis.R;

public class AdminDashboardFragment extends AppCompatActivity {

    private DrawerLayout drawerLayoutAdmin;
    private View itemDashboard;
    private View itemManageDrugs;
    private BottomNavigationView bottomNavAdmin;

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
                replaceFragment(new DashboardFragment());
                highlightSidebarItem(itemDashboard); // Đồng bộ độ sáng với Sidebar
                return true;
            } else if (itemId == R.id.nav_data) {
                // TODO: Tạo DataFragment và gọi ở đây
                Toast.makeText(this, "Chuyển đến Dữ liệu", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_requests) {
                // TODO: Tạo RequestFragment và gọi ở đây
                Toast.makeText(this, "Chuyển đến Yêu cầu", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_system) {
                // TODO: Tạo SystemFragment và gọi ở đây
                Toast.makeText(this, "Chuyển đến Hệ thống", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    // ================= XỬ LÝ SIDEBAR =================
    private void setupSidebarActions() {
        itemDashboard = findViewById(R.id.item_dashboard);
        itemManageDrugs = findViewById(R.id.item_manage_drugs);

        if (itemDashboard != null) {
            itemDashboard.setOnClickListener(v -> {
                replaceFragment(new DashboardFragment());
                highlightSidebarItem(itemDashboard);
                // Đồng bộ chọn lại tab "Tổng quan" trên Bottom Nav
                bottomNavAdmin.setSelectedItemId(R.id.nav_overview);
                closeSidebar();
            });
        }

        if (itemManageDrugs != null) {
            itemManageDrugs.setOnClickListener(v -> {
                replaceFragment(new ManageMedicinesFragment());
                highlightSidebarItem(itemManageDrugs);
                // Bỏ chọn tất cả trên Bottom Nav nếu mục này không có trong Bottom Nav
                // bottomNavAdmin.setSelectedItemId(R.id.invisible_item); (Tùy chọn)
                closeSidebar();
            });
        }
    }

    private void highlightSidebarItem(View selectedItem) {
        int defaultColor = Color.TRANSPARENT;
        int activeColor = Color.parseColor("#1E3A5F");

        resetItemColor(itemDashboard, defaultColor);
        resetItemColor(itemManageDrugs, defaultColor);
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
    public void replaceFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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