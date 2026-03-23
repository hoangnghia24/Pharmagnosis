package hcmute.edu.vn.pharmagnosis.views.admin;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat; // Cần thiết để định hướng phía mở sidebar
import androidx.drawerlayout.widget.DrawerLayout;

import hcmute.edu.vn.pharmagnosis.R;

public class AdminDashboardFragment extends AppCompatActivity {

    // Khai báo biến theo camelCase như quy ước của bạn
    private ImageView imgSidebar;
    private DrawerLayout drawerLayoutAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đổi sang layout chứa cả sidebar
        setContentView(R.layout.activity_admin_main);

        // 1. Ánh xạ các thành phần
        imgSidebar = findViewById(R.id.img_menu);
        drawerLayoutAdmin = findViewById(R.id.drawer_layout_admin);

        // 2. Thiết lập sự kiện click
        imgSidebar.setOnClickListener(v -> {
            // Mở sidebar từ phía bên trái (START)
            if (drawerLayoutAdmin != null) {
                drawerLayoutAdmin.openDrawer(GravityCompat.START);
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Nếu Sidebar đang mở thì đóng nó lại
                if (drawerLayoutAdmin != null && drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
                    drawerLayoutAdmin.closeDrawer(GravityCompat.START);
                } else {
                    // Nếu Sidebar đã đóng, thực hiện thoát Activity
                    setEnabled(false); // Vô hiệu hóa callback này để gọi lệnh thoát mặc định
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

}