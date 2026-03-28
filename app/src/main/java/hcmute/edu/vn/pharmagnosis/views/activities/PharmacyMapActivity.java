package hcmute.edu.vn.pharmagnosis.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Pharmacy;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;
import hcmute.edu.vn.pharmagnosis.utils.LocationUtils;
import hcmute.edu.vn.pharmagnosis.viewmodels.user.PharmacyMapViewModel;

public class PharmacyMapActivity extends AppCompatActivity {

    private MapView mapView;
    private PharmacyMapViewModel viewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView tvPharmacyName, tvPharmacyAddress;
    private View dimView;
    private Button btnRadius;

    private Pharmacy currentSelectedPharmacy = null;
    private MyLocationNewOverlay myLocationOverlay;
    private int currentRadius = 2000;

    // Mã request code để xin quyền
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_pharmacy_map);

        // Ánh xạ View
        mapView = findViewById(R.id.mapView);
        View bottomSheet = findViewById(R.id.bottomSheet);
        dimView = findViewById(R.id.dimView);
        tvPharmacyName = findViewById(R.id.tvPharmacyName);
        tvPharmacyAddress = findViewById(R.id.tvPharmacyAddress);
        btnRadius = findViewById(R.id.btnRadius);
        FloatingActionButton btnBackMap = findViewById(R.id.btnBackMap);
        Button btnDirections = findViewById(R.id.btnDirections);

        // Setup các nút bấm cơ bản
        btnBackMap.setOnClickListener(v -> finish());
        setupBottomSheet(bottomSheet);
        setupDirectionsButton(btnDirections);

        // Cấu hình bản đồ cơ bản
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        // Thiết lập tọa độ neo tạm thời trên đất liền (tránh bị ra biển)
        mapView.getController().setCenter(new GeoPoint(10.762622, 106.660172));

        viewModel = new ViewModelProvider(this).get(PharmacyMapViewModel.class);

        // --- KIỂM TRA VÀ XIN QUYỀN VỊ TRÍ TRƯỚC KHI CHẠY BẢN ĐỒ ---
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, bật Popup xin quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Nếu đã có quyền rồi, khởi động GPS ngay
            startLocationAndMap();
        }
    }

    // --- HÀM XỬ LÝ KẾT QUẢ KHI NGƯỜI DÙNG BẤM "CHO PHÉP" HOẶC "TỪ CHỐI" ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng bấm Cho phép -> Khởi động GPS
                startLocationAndMap();
            } else {
                // Người dùng bấm Từ chối
                Toast.makeText(this, "Bạn cần cấp quyền vị trí để tìm nhà thuốc gần đây!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // --- HÀM KHỞI ĐỘNG GPS VÀ TẢI DỮ LIỆU ---
    private void startLocationAndMap() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        Toast.makeText(this, "Đang định vị GPS...", Toast.LENGTH_SHORT).show();

        // Chờ GPS chốt vị trí rồi tự động gọi API
        myLocationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
            GeoPoint myLocation = myLocationOverlay.getMyLocation();
            if (myLocation != null) {
                mapView.getController().animateTo(myLocation);
                loadPharmacies(myLocation.getLatitude(), myLocation.getLongitude(), currentRadius);
            }
        }));

        // Xử lý nút chọn bán kính
        btnRadius.setOnClickListener(v -> {
            // 1. Tạo một ô nhập liệu (EditText)
            final android.widget.EditText input = new android.widget.EditText(this);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER); // Chỉ cho phép nhập số
            input.setHint("Ví dụ: 1500"); // Gợi ý cho người dùng

            // Căn lề một chút cho ô nhập liệu đẹp hơn
            android.widget.FrameLayout container = new android.widget.FrameLayout(this);
            android.widget.FrameLayout.LayoutParams params = new  android.widget.FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(50, 0, 50, 0); // Cách lề trái phải 50px
            input.setLayoutParams(params);
            container.addView(input);

            // 2. Tạo Dialog hiển thị ô nhập liệu
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Tùy chỉnh bán kính")
                    .setMessage("Nhập bán kính tìm kiếm (tính bằng mét):")
                    .setView(container)
                    .setPositiveButton("Tìm kiếm", (dialog, which) -> {
                        String radiusStr = input.getText().toString().trim();

                        if (!radiusStr.isEmpty()) {
                            try {
                                int newRadius = Integer.parseInt(radiusStr);

                                // Kiểm tra giới hạn: Chỉ cho tìm từ 100m đến 20,000m (20km) để API không bị quá tải
                                if (newRadius >= 100 && newRadius <= 20000) {
                                    currentRadius = newRadius;

                                    // Đổi text cho nút hiển thị cho đẹp (nếu >= 1000 thì hiện km, ngược lại hiện mét)
                                    if (currentRadius >= 1000) {
                                        // Ví dụ: 1500m -> 1.5 km
                                        btnRadius.setText("Bán kính: " + (currentRadius / 1000.0) + " km");
                                    } else {
                                        btnRadius.setText("Bán kính: " + currentRadius + " m");
                                    }

                                    // Gọi hàm tìm kiếm với bán kính mới
                                    GeoPoint myLocation = myLocationOverlay.getMyLocation();
                                    if (myLocation != null) {
                                        loadPharmacies(myLocation.getLatitude(), myLocation.getLongitude(), currentRadius);
                                    } else {
                                        Toast.makeText(this, "Chưa chốt được vị trí GPS, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Vui lòng nhập bán kính từ 100 đến 20000 mét!", Toast.LENGTH_LONG).show();
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Số nhập vào không hợp lệ!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Bạn chưa nhập bán kính!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.cancel())
                    .show();
        });
    }

    private void loadPharmacies(double lat, double lon, int radius) {
        Toast.makeText(this, "Đang quét nhà thuốc trong " + radius + "m...", Toast.LENGTH_SHORT).show();

        viewModel.fetchPharmacies(lat, lon, radius).observe(this, pharmacies -> {
            if (pharmacies != null) {
                mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);

                for (Pharmacy p : pharmacies) {
                    Marker marker = new Marker(mapView);
                    marker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                    marker.setTitle(p.getName());

                    // Nhớ tự bổ sung file ic_pharmacy.xml vào drawable nhé
                    // marker.setIcon(getResources().getDrawable(R.drawable.ic_pharmacy));

                    marker.setOnMarkerClickListener((m, mv) -> {
                        currentSelectedPharmacy = p;

                        if (p.getAddressText() == null || p.getAddressText().isEmpty()) {
                            tvPharmacyAddress.setText("Đang tải địa chỉ...");
                            String address = LocationUtils.getAddressFromLatLng(PharmacyMapActivity.this, p.getLatitude(), p.getLongitude());
                            p.setAddressText(address);
                        }

                        tvPharmacyName.setText(p.getName());
                        tvPharmacyAddress.setText(p.getAddressText());
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        return true;
                    });

                    mapView.getOverlays().add(marker);
                }
                mapView.invalidate();
                Toast.makeText(this, "Đã tìm thấy " + pharmacies.size() + " nhà thuốc!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomSheet(View bottomSheet) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(20);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {}
            @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                dimView.setAlpha(slideOffset * 0.7f);
                dimView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupDirectionsButton(Button btnDirections) {
        btnDirections.setOnClickListener(v -> {
            if (currentSelectedPharmacy != null) {

                // --- 1. LƯU LẠI LỊCH SỬ TRƯỚC KHI CHUYỂN QUA BẢN ĐỒ ---
                SearchRecord record = new SearchRecord();

                // Lấy ID user (Nếu bạn đã làm chức năng đăng nhập Firebase Auth thì dùng dòng dưới)
                // String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
                record.setUserId("user_id_tam_thoi"); // Tạm thời để hardcode nếu chưa làm login

                record.setKeyword(currentSelectedPharmacy.getName()); // Lưu tên nhà thuốc
                record.setSearchDate(new java.util.Date()); // Lưu thời gian hiện tại
                record.setItemId(currentSelectedPharmacy.getPharmacyId()); // Lưu ID nhà thuốc

                // Lưu loại tìm kiếm (Bạn tự sửa PHARMACY thành tên giá trị có trong ESearchType của bạn nhé)
                record.setSearchType(hcmute.edu.vn.pharmagnosis.ENUM.ESearchType.PHARMACY);

                // Gọi ViewModel để đẩy lên Firebase
                viewModel.saveSearchRecord(record);
                // -------------------------------------------------------


                // --- 2. CODE MỞ GOOGLE MAPS NHƯ CŨ ---
                String uri = "google.navigation:q=" + currentSelectedPharmacy.getLatitude() + "," + currentSelectedPharmacy.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    String webUri = "https://www.google.com/maps/dir/?api=1&destination=" + currentSelectedPharmacy.getLatitude() + "," + currentSelectedPharmacy.getLongitude();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                    startActivity(webIntent);
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn một nhà thuốc trước", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
}