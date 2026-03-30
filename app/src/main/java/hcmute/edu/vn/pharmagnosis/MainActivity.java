package hcmute.edu.vn.pharmagnosis;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hcmute.edu.vn.pharmagnosis.views.activities.PharmacyMapActivity;
import hcmute.edu.vn.pharmagnosis.views.user.UserDashboardFragment;
import hcmute.edu.vn.pharmagnosis.views.user.UserProfileFragment;

public class MainActivity extends AppCompatActivity {

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment homeFragment;
    private Fragment profileFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo các Fragment 1 lần duy nhất
        homeFragment = new UserDashboardFragment();
        profileFragment = new UserProfileFragment();
        activeFragment = homeFragment;

        // Thêm cả 2 Fragment vào Container, nhưng ẩn Profile đi
        fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragment, "2").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);

        // Xử lý sự kiện click trên thanh Nav
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;

            } else if (itemId == R.id.nav_search) {
                android.content.Intent intent = new android.content.Intent(this, PharmacyMapActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                activeFragment = profileFragment;
                return true;
            }

            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}