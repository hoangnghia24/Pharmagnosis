package hcmute.edu.vn.pharmagnosis.views.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButtonToggleGroup;

import hcmute.edu.vn.pharmagnosis.ENUM.ESearchType;
import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.viewmodels.admin.AdminDashboardViewmodels;

public class DashboardFragment extends Fragment {

    private AdminDashboardViewmodels viewModel;
    private BarChart chart;
    private AutoCompleteTextView spinnerMonths;
    private MaterialButtonToggleGroup toggleTypeGroup;

    private int currentMonthIndex = 0; // Mặc định là 0 (Tất cả các tháng)
    private ESearchType currentSearchType = ESearchType.MEDICINE; // Mặc định là xem Top Thuốc

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Xử lý sự kiện mở Sidebar (Menu)
        ImageView imgMenu = view.findViewById(R.id.img_menu);
        if (imgMenu != null) {
            imgMenu.setOnClickListener(v -> {
                if (requireActivity() instanceof AdminDashboardFragment) {
                    ((AdminDashboardFragment) requireActivity()).openSidebar();
                }
            });
        }

        // Khởi chạy tính năng biểu đồ Top 10
        setupChartFeature(view);
    }

    private void setupChartFeature(View view) {
        chart = view.findViewById(R.id.top_search_bar_chart);
        spinnerMonths = view.findViewById(R.id.spinner_months);
        toggleTypeGroup = view.findViewById(R.id.toggle_type_filter);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(AdminDashboardViewmodels.class);

        // --- 1. XỬ LÝ CHỌN THÁNG (Dropdown) ---
        String[] months = {
                "Tất cả các tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9",
                "Tháng 10", "Tháng 11", "Tháng 12"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, months);
        if (spinnerMonths != null) {
            spinnerMonths.setAdapter(adapter);
            spinnerMonths.setOnItemClickListener((parent, v, position, id) -> {
                currentMonthIndex = position;
                // Gọi Reprocess thay vì Fetch lại mạng
                viewModel.reprocessData(currentMonthIndex, currentSearchType);
            });
        }

        // --- 2. XỬ LÝ CHỌN LOẠI (Thuốc / Nhà thuốc) ---
        if (toggleTypeGroup != null) {
            toggleTypeGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.btn_type_drug) {
                        currentSearchType = ESearchType.MEDICINE;
                    } else if (checkedId == R.id.btn_type_pharmacy) {
                        currentSearchType = ESearchType.PHARMACY;
                    }
                    // Gọi Reprocess để vẽ lại biểu đồ ngay lập tức
                    viewModel.reprocessData(currentMonthIndex, currentSearchType);
                }
            });
        }

        // --- 3. QUAN SÁT DỮ LIỆU TỪ VIEWMODEL VÀ VẼ BARCHART ---
        viewModel.getChartDataLiveData().observe(getViewLifecycleOwner(), chartData -> {
            if (chartData != null) {
                drawTop10BarChart(chartData);
            }
        });

        // --- 4. TẢI DỮ LIỆU LẦN ĐẦU TIÊN (Khi vừa mở app) ---
        viewModel.fetchTopSearchData(currentMonthIndex, currentSearchType);
    }

    private void drawTop10BarChart(AdminDashboardViewmodels.TopSearchData chartData) {
        if (chart == null) return;

        // Xóa sạch dữ liệu cũ mỗi khi vẽ mới
        chart.clear();

        // Xử lý khi không có dữ liệu
        if (chartData.entries.isEmpty()) {
            chart.setNoDataText("Không có dữ liệu tìm kiếm trong thời gian này");
            chart.setNoDataTextColor(Color.parseColor("#64748B"));
            chart.invalidate();
            return;
        }

        // Tạo Label cho chú thích (Legend)
        String labelText = (chartData.currentType == ESearchType.MEDICINE) ? "Lượt tìm Thuốc" : "Lượt tìm Nhà thuốc";
        BarDataSet dataSet = new BarDataSet(chartData.entries, labelText);

        // Đổi màu cột theo loại dữ liệu
        if (chartData.currentType == ESearchType.MEDICINE) {
            dataSet.setColor(Color.parseColor("#1976D2")); // Màu xanh dương
        } else {
            dataSet.setColor(Color.parseColor("#FF9800")); // Màu cam
        }

        // Cấu hình con số hiển thị trên đỉnh cột (Ép về số nguyên)
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.parseColor("#475569"));
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0f) return ""; // Ẩn số 0 cho đỡ rối
                return String.valueOf((int) value);
            }
        });

        // Cấu hình độ rộng của cột
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        chart.setData(barData);

        // --- CẤU HÌNH TRỤC X (Hiển thị Từ khóa Top 10) ---
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(chartData.labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Ép bước nhảy là 1 để hiển thị đủ nhãn
        xAxis.setDrawGridLines(false); // Bỏ các đường kẻ dọc mờ
        xAxis.setLabelRotationAngle(-45f); // Xoay chữ 45 độ
        xAxis.setTextColor(Color.parseColor("#64748B"));

        // --- CẤU HÌNH TRỤC Y BÊN TRÁI (Lượt tìm kiếm) ---
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f); // Bắt đầu từ 0
        yAxisLeft.setGranularity(1f); // Chỉ nhảy số nguyên
        yAxisLeft.setTextColor(Color.parseColor("#64748B"));
        yAxisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Số nguyên trên trục Y
            }
        });

        // Tùy chỉnh làm sạch giao diện
        chart.getAxisRight().setEnabled(false); // Tắt hoàn toàn trục Y bên phải
        chart.getDescription().setEnabled(false); // Tắt chữ "Description Label" ở góc phải dưới
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // Tăng khoảng trống ở đáy để chữ xoay nghiêng không bị cắt mất
        chart.setExtraBottomOffset(40f);

        // Thêm hiệu ứng cột chạy từ dưới lên trong 1 giây
        chart.animateY(1000);
        chart.invalidate(); // Bắt buộc gọi để cập nhật UI
    }
}