package hcmute.edu.vn.pharmagnosis.views.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Pharmacy;
import hcmute.edu.vn.pharmagnosis.utils.LocationUtils;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.PharmacyMapViewModel;

public class PharmacyMapActivity extends AppCompatActivity {

    private MapView mapView;
    private PharmacyMapViewModel viewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView tvPharmacyName, tvPharmacyAddress;
    private View dimView;

    // Lưu nhà thuốc đang được chọn để dùng cho việc Chỉ đường
    private Pharmacy currentSelectedPharmacy = null;

    private final double CENTER_LAT = 10.762622;
    private final double CENTER_LON = 106.660172;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Cấu hình bản đồ OSMDroid (Bắt buộc phải có trước setContentView)
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_pharmacy_map);

        // 2. Ánh xạ các View
        mapView = findViewById(R.id.mapView);
        View bottomSheet = findViewById(R.id.bottomSheet);
        dimView = findViewById(R.id.dimView);
        tvPharmacyName = findViewById(R.id.tvPharmacyName);
        tvPharmacyAddress = findViewById(R.id.tvPharmacyAddress);

        FloatingActionButton btnBackMap = findViewById(R.id.btnBackMap);
        Button btnDirections = findViewById(R.id.btnDirections);

        // 3. Xử lý sự kiện nút Thoát
        btnBackMap.setOnClickListener(v -> finish());

        // 4. Xử lý sự kiện nút Chỉ đường (Mở Google Maps)
        btnDirections.setOnClickListener(v -> {
            if (currentSelectedPharmacy != null) {
                String uri = "google.navigation:q=" + currentSelectedPharmacy.getLatitude() + "," + currentSelectedPharmacy.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps"); // Bắt buộc mở bằng Google Maps

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // Dự phòng nếu máy chưa cài app Google Maps thì mở bằng trình duyệt web
                    String webUri = "https://www.google.com/maps/dir/?api=1&destination=" + currentSelectedPharmacy.getLatitude() + "," + currentSelectedPharmacy.getLongitude();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                    startActivity(webIntent);
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn một nhà thuốc trước", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Cấu hình hiệu ứng trượt của Bottom Sheet và làm mờ (Dim) bản đồ
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // Thu nhỏ lúc mới vào
        bottomSheetBehavior.setPeekHeight(20); // Để lộ 20dp cho người dùng biết có thể kéo

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Tăng độ mờ dựa trên mức độ trượt của Bottom Sheet (max 70% mờ)
                dimView.setAlpha(slideOffset * 0.7f);
                dimView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
            }
        });

        // 6. Cấu hình các thông số cho MapView
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(CENTER_LAT, CENTER_LON));

        // 7. Khởi tạo ViewModel và Load dữ liệu từ API
        viewModel = new ViewModelProvider(this).get(PharmacyMapViewModel.class);
        Toast.makeText(this, "Đang quét nhà thuốc xung quanh...", Toast.LENGTH_SHORT).show();

        viewModel.fetchPharmacies(CENTER_LAT, CENTER_LON).observe(this, pharmacies -> {
            if (pharmacies != null && !pharmacies.isEmpty()) {
                mapView.getOverlays().clear();

                for (Pharmacy p : pharmacies) {
                    Marker marker = new Marker(mapView);
                    marker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                    marker.setTitle(p.getName());

                    // Xử lý sự kiện click vào từng Icon nhà thuốc trên bản đồ
                    marker.setOnMarkerClickListener((m, mv) -> {
                        currentSelectedPharmacy = p; // Lưu lại để dùng cho nút Chỉ đường

                        // Lazy load: Chỉ dịch địa chỉ nếu trước đó chưa dịch
                        if (p.getAddressText() == null || p.getAddressText().isEmpty()) {
                            tvPharmacyAddress.setText("Đang tải địa chỉ...");
                            String address = LocationUtils.getAddressFromLatLng(PharmacyMapActivity.this, p.getLatitude(), p.getLongitude());
                            p.setAddressText(address);
                        }

                        // Cập nhật thông tin lên Bottom Sheet
                        tvPharmacyName.setText(p.getName());
                        tvPharmacyAddress.setText(p.getAddressText());

                        // Bật Bottom Sheet lên hết cỡ
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        return true;
                    });

                    mapView.getOverlays().add(marker);
                }
                mapView.invalidate(); // Vẽ lại bản đồ để hiện Marker
                Toast.makeText(this, "Tìm thấy " + pharmacies.size() + " nhà thuốc!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hai hàm bắt buộc để bản đồ không bị lỗi khi chuyển sang app khác
    @Override
    protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override
    protected void onPause() { super.onPause(); mapView.onPause(); }
}