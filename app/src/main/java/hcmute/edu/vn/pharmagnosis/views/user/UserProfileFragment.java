package hcmute.edu.vn.pharmagnosis.views.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.BmiRecord;
import hcmute.edu.vn.pharmagnosis.viewmodels.ProfileViewModel;
import hcmute.edu.vn.pharmagnosis.views.activities.LoginActivity;

import android.content.Intent;

// --- CÁC THƯ VIỆN ĐƯỢC THÊM VÀO ---
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
// ----------------------------------

public class UserProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.fetchUser();

        profileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                TextView tvName = view.findViewById(R.id.tv_name);
                TextView tvAgeGender = view.findViewById(R.id.tv_age_gender);
                TextView tvBlood = view.findViewById(R.id.tv_blood);
                TextView tvAllergy = view.findViewById(R.id.tv_allergy);
                ImageView imgAvatarDisplay = view.findViewById(R.id.img_avatar_display);

                TextView tvProfileHeight = view.findViewById(R.id.tv_profile_height);
                TextView tvProfileWeight = view.findViewById(R.id.tv_profile_weight);
                TextView tvBmiScore = view.findViewById(R.id.tv_bmi_score);
                TextView tvBmiStatus = view.findViewById(R.id.tv_bmi_status);
                ImageView icPointer = view.findViewById(R.id.ic_pointer);

                if (tvName != null && user.getFullName() != null) tvName.setText(user.getFullName());
                if (tvBlood != null && user.getBloodType() != null) tvBlood.setText("Nhóm máu: " + user.getBloodType());

                if (tvAgeGender != null) {
                    String genderStr = (user.getGender() != null && user.getGender().name().equals("MALE")) ? "Nam" : "Nữ";
                    String ageStr = "Chưa rõ";

                    if (user.getDob() != null) {
                        Calendar dob = Calendar.getInstance();
                        dob.setTime(user.getDob());
                        Calendar today = Calendar.getInstance();

                        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                            age--;
                        }
                        ageStr = age + " tuổi";
                    }
                    tvAgeGender.setText(genderStr + " • " + ageStr);
                }

                if (tvAllergy != null) {
                    if (user.getAllergies() != null && !user.getAllergies().isEmpty()) {
                        StringBuilder allergyText = new StringBuilder("Dị ứng: ");
                        for (int i = 0; i < user.getAllergies().size(); i++) {
                            allergyText.append(user.getAllergies().get(i).getAllergenName());
                            if (i < user.getAllergies().size() - 1) allergyText.append(", ");
                        }
                        tvAllergy.setText(allergyText.toString());
                    } else {
                        tvAllergy.setText("Dị ứng: Không có");
                    }
                }

                if (imgAvatarDisplay != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(user.getAvatar(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgAvatarDisplay.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // THAY THẾ BẰNG ĐOẠN NÀY
                BmiRecord latestRecord = getLatestBmiRecord(user.getBmiHistory());

                if (latestRecord != null) {
                    float latestHeight = latestRecord.getHeight();
                    float latestWeight = latestRecord.getWeight();
                    float latestBmi = latestRecord.getBmi();

                    // 1. Hiển thị chiều cao mới nhất
                    if (tvProfileHeight != null && latestHeight > 0) {
                        tvProfileHeight.setText(String.format(Locale.getDefault(), "%.0f", latestHeight));
                    }

                    // 2. Hiển thị cân nặng mới nhất và vẽ biểu đồ
                    if (tvProfileWeight != null && latestWeight > 0) {
                        tvProfileWeight.setText(String.format(Locale.getDefault(), "%.1f", latestWeight));

                        // Gọi hàm vẽ biểu đồ
                        loadRealWeightHistory(view);
                    }

                    // 3. Hiển thị điểm số và trạng thái BMI mới nhất
                    if (tvBmiScore != null && tvBmiStatus != null && icPointer != null && latestBmi > 0) {
                        tvBmiScore.setText(String.format(Locale.getDefault(), "%.1f", latestBmi));

                        String status;
                        int color;
                        float bias;

                        if (latestBmi < 18.5) {
                            status = "Gầy"; color = 0xFF38BDF8; bias = 0.12f;
                        } else if (latestBmi < 25) {
                            status = "Bình thường"; color = 0xFF22C55E; bias = 0.38f;
                        } else if (latestBmi < 30) {
                            status = "Thừa cân"; color = 0xFFFACC15; bias = 0.63f;
                        } else {
                            status = "Béo phì"; color = 0xFFEF4444; bias = 0.88f;
                        }

                        tvBmiStatus.setText(status);
                        tvBmiStatus.setTextColor(color);

                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) icPointer.getLayoutParams();
                        params.horizontalBias = bias;
                        icPointer.setLayoutParams(params);
                    }
                } else {
                    // Tùy chọn: Xử lý giao diện khi người dùng chưa có lịch sử đo nào
                    if (tvProfileHeight != null) tvProfileHeight.setText("--");
                    if (tvProfileWeight != null) tvProfileWeight.setText("--");
                    if (tvBmiScore != null) tvBmiScore.setText("--");
                    if (tvBmiStatus != null) {
                        tvBmiStatus.setText("Chưa có dữ liệu");
                        tvBmiStatus.setTextColor(0xFF64748B); // Màu xám nhạt
                    }
                }
            }

            View btnLogout = view.findViewById(R.id.btn_logout);
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                });
            }
        });

        View cvBmi = view.findViewById(R.id.cv_bmi);
        if (cvBmi != null) {
            cvBmi.setOnClickListener(v -> {
                BmiCalculatorFragment bmiFragment = new BmiCalculatorFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, bmiFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        View cvMedicalDetails = view.findViewById(R.id.cv_medical_details);
        if (cvMedicalDetails != null) {
            cvMedicalDetails.setOnClickListener(v -> {
                MedicalProfileFragment medicalFragment = new MedicalProfileFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, medicalFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }
    }


    // 1. Hàm kết nối Firebase lấy lịch sử Cân nặng từ nhánh BmiHistory
    private void loadRealWeightHistory(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid).child("bmiHistory");

        // Lấy 6 lần đo gần nhất
        historyRef.orderByKey().limitToLast(6).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries = new ArrayList<>();
                final ArrayList<String> dateLabels = new ArrayList<>();
                int index = 0;

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM", Locale.getDefault());

                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Long timestamp = data.child("timestamp").getValue(Long.class);
                        Double weightDouble = data.child("weight").getValue(Double.class);

                        if (timestamp != null && weightDouble != null) {
                            float weight = weightDouble.floatValue();
                            entries.add(new Entry(index, weight));
                            dateLabels.add(sdf.format(new java.util.Date(timestamp)));
                            index++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Nếu có từ 1 điểm dữ liệu trở lên thì tiến hành vẽ
                if (!entries.isEmpty()) {
                    drawChart(view, entries, dateLabels);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi lỗi mạng
            }
        });
    }

    // 2. Hàm thực thi vẽ biểu đồ
    private void drawChart(View view, ArrayList<Entry> entries, ArrayList<String> dateLabels) {
        LineChart weightChart = view.findViewById(R.id.weight_chart);
        if (weightChart == null) return;

        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
        dataSet.setColor(android.graphics.Color.parseColor("#1976D2")); // Đường màu xanh dương
        dataSet.setCircleColor(android.graphics.Color.parseColor("#1976D2")); // Dấu chấm màu xanh
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true); // Hiện con số
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(android.graphics.Color.parseColor("#64748B"));

        LineData lineData = new LineData(dataSet);
        weightChart.setData(lineData);

        // Cài đặt trục X (Ngày/Tháng)
        XAxis xAxis = weightChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đẩy trục X xuống dưới
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false); // Ẩn các sọc dọc
        xAxis.setDrawLabels(true); // Ép buộc phải vẽ chữ
        xAxis.setTextColor(android.graphics.Color.parseColor("#64748B")); // Set màu chữ xám
        xAxis.setLabelCount(dateLabels.size(), false); // Ép biểu đồ phải chia ĐỦ số cột bằng số ngày có thật
        xAxis.setAvoidFirstLastClipping(true); // Ngăn không cho chữ ở mép trái/phải bị cắt lẹm

        weightChart.setExtraBottomOffset(25f); // Nâng đáy lên 25f (thay vì 15f) cho không gian rộng rãi hẳn

        // Dọn dẹp giao diện tổng thể
        weightChart.getAxisRight().setEnabled(false); // Ẩn trục Y bên phải
        weightChart.getDescription().setEnabled(false); // Ẩn chữ góc phải
        weightChart.setTouchEnabled(true);
        weightChart.setDragEnabled(true);
        weightChart.setScaleEnabled(false); // Khóa zoom tránh vỡ layout

        weightChart.animateX(1000); // Hiệu ứng chạy từ trái sang phải
        weightChart.invalidate(); // Vẽ lại biểu đồ
    }
    // Hàm hỗ trợ lấy bản ghi BMI mới nhất từ Map
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