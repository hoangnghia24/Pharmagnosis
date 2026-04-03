package hcmute.edu.vn.pharmagnosis.views.user;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.BmiRecord;
import hcmute.edu.vn.pharmagnosis.viewmodels.ProfileViewModel;

public class BmiCalculatorFragment extends Fragment {

    private EditText edtHeight, edtWeight;
    private TextView tvSummaryHeight, tvSummaryWeight, tvResultStatus, tvResultScore;
    private ImageView icPointer, btnBack;
    private MaterialButton btnCalculate;
    private ProfileViewModel profileViewModel;

    // Biến cờ để tránh load lại dữ liệu cũ khi người dùng đang bấm tính nháp
    private boolean isInitialLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bmi_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Ánh xạ
        btnBack = view.findViewById(R.id.btn_back);
        edtHeight = view.findViewById(R.id.edt_height_calc);
        edtWeight = view.findViewById(R.id.edt_weight_calc);
        btnCalculate = view.findViewById(R.id.btn_calculate_fast);

        tvSummaryHeight = view.findViewById(R.id.tv_summary_height);
        tvSummaryWeight = view.findViewById(R.id.tv_summary_weight);
        tvResultStatus = view.findViewById(R.id.tv_result_status_box);
        tvResultScore = view.findViewById(R.id.tv_result_score_box);
        icPointer = view.findViewById(R.id.ic_pointer);

        // Nút Back
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Nút Tính toán (CHỈ TÍNH UI, KHÔNG LƯU DB NỮA)
        btnCalculate.setOnClickListener(v -> calculateBMI());

        // Load dữ liệu cũ từ Firebase lên để điền sẵn vào ô
        observeData();
    }

    private void observeData() {
        profileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null && isInitialLoad) {

                // Lấy bản ghi BMI mới nhất từ lịch sử
                BmiRecord latestRecord = getLatestBmiRecord(user.getBmiHistory());

                if (latestRecord != null) {
                    float latestHeight = latestRecord.getHeight();
                    float latestWeight = latestRecord.getWeight();
                    float latestBmi = latestRecord.getBmi();

                    // Điền sẵn số liệu thật vào 2 ô nhập liệu nếu có
                    if (latestHeight > 0) {
                        edtHeight.setText(String.format(Locale.getDefault(), "%.0f", latestHeight));
                        tvSummaryHeight.setText(String.format(Locale.getDefault(), "%.0f cm", latestHeight));
                    }
                    if (latestWeight > 0) {
                        edtWeight.setText(String.format(Locale.getDefault(), "%.1f", latestWeight));
                    }

                    // Hiển thị kết quả thật lên UI lần đầu tiên
                    if (latestBmi > 0) {
                        updateBMIUI(latestWeight, latestBmi);
                    }
                }

                isInitialLoad = false; // Đánh dấu đã load xong lần đầu
            }
        });
    }

    private void calculateBMI() {
        String strHeight = edtHeight.getText().toString();
        String strWeight = edtWeight.getText().toString();

        if (strHeight.isEmpty() || strWeight.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ chỉ số!", Toast.LENGTH_SHORT).show();
            return;
        }

        double heightCm = Double.parseDouble(strHeight);
        double weightKg = Double.parseDouble(strWeight);

        if (heightCm <= 0 || weightKg <= 0) return;

        // Công thức: BMI = Cân nặng (kg) / (Chiều cao (m) ^ 2)
        double heightM = heightCm / 100;
        double bmi = weightKg / (heightM * heightM);

        // CHỈ CẬP NHẬT GIAO DIỆN (TÍNH NHÁP), KHÔNG GỌI HÀM LƯU FIREBASE
        updateBMIUI(weightKg, bmi);
        tvSummaryHeight.setText(String.format(Locale.getDefault(), "%.0f cm", heightCm));
    }

    private void updateBMIUI(double weight, double bmi) {
        tvSummaryWeight.setText(String.format(Locale.getDefault(), "%.1f kg", weight));
        tvResultScore.setText(String.format(Locale.getDefault(), "Chỉ số BMI: %.1f", bmi));

        String status;
        int color;
        int boxBgColor;
        float bias;

        if (bmi < 18.5) {
            status = "Tình trạng: Gầy";
            color = 0xFF38BDF8;
            boxBgColor = 0xFFF0F9FF;
            bias = 0.12f;
        } else if (bmi < 25) {
            status = "Tình trạng: Bình thường";
            color = 0xFF22C55E;
            boxBgColor = 0xFFF0FDF4;
            bias = 0.38f;
        } else if (bmi < 30) {
            status = "Tình trạng: Thừa cân";
            color = 0xFFFACC15;
            boxBgColor = 0xFFFEFCE8;
            bias = 0.63f;
        } else {
            status = "Cảnh báo: Béo phì";
            color = 0xFFEF4444;
            boxBgColor = 0xFFFEF2F2;
            bias = 0.88f;
        }

        tvResultStatus.setText(status);
        tvResultStatus.setTextColor(color);
        tvResultStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(boxBgColor));

        // Di chuyển mũi tên (Pointer)
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) icPointer.getLayoutParams();
        params.horizontalBias = bias;
        icPointer.setLayoutParams(params);
    }
    private BmiRecord getLatestBmiRecord(java.util.Map<String, BmiRecord> bmiHistory) {
        if (bmiHistory == null || bmiHistory.isEmpty()) {
            return null;
        }

        BmiRecord latestRecord = null;
        for (BmiRecord record : bmiHistory.values()) {
            if (latestRecord == null || record.getTimestamp() > latestRecord.getTimestamp()) {
                latestRecord = record;
            }
        }
        return latestRecord;
    }
}