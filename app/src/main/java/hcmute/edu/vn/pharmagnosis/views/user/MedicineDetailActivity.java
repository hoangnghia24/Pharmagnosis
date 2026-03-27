package hcmute.edu.vn.pharmagnosis.views.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Medicine;

public class MedicineDetailActivity extends AppCompatActivity {

    private ImageView imgMedicine;
    private ImageView imgBack;
    private TextView txtTitle;
    private TextView txtCongDung;
    private TextView txtsideEffects;
    private View layoutWarningBox;

    private TabLayout tabLayoutMedicine;
    private TextView txtSectionTitle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        // 1. ÁNH XẠ VIEW
        txtsideEffects = findViewById(R.id.txtWarningText);
        layoutWarningBox = findViewById(R.id.layoutWarningBox);
        imgBack = findViewById(R.id.imgBack);
        txtTitle = findViewById(R.id.txtMedicineName);
        txtCongDung = findViewById(R.id.txtMedicineDetailContent);
        imgMedicine = findViewById(R.id.imgMedicine);

        //  Ánh xạ thanh Tab và Tiêu đề mục
        tabLayoutMedicine = findViewById(R.id.tabLayoutMedicine);
        txtSectionTitle = findViewById(R.id.txtSectionTitle);

        imgBack.setOnClickListener(v -> finish());

        Medicine medicine = (Medicine) getIntent().getSerializableExtra("MEDICINE_OBJ");

        if (medicine != null) {
            // Đắp dữ liệu mặc định ban đầu lên giao diện
            if (txtTitle != null) {
                txtTitle.setText(medicine.getMedicineName());
            }
            if (txtCongDung != null) {
                txtCongDung.setText(medicine.getIndications());
            }
            if (medicine.getImage() != null && !medicine.getImage().isEmpty()) {
                com.bumptech.glide.Glide.with(this)
                        .load(medicine.getImage())
                        .into(imgMedicine);
            }

            if (txtsideEffects != null && layoutWarningBox != null) {
                if (medicine.getSideEffects() != null && !medicine.getSideEffects().isEmpty()) {
                    layoutWarningBox.setVisibility(View.VISIBLE);
                    StringBuilder warningContent = new StringBuilder();
                    for (String effect : medicine.getSideEffects()) {
                        warningContent.append("• ").append(effect).append("\n");
                    }
                    txtsideEffects.setText(warningContent.toString().trim());
                } else {
                    layoutWarningBox.setVisibility(View.GONE);
                }
            }

            if (tabLayoutMedicine != null) {
                tabLayoutMedicine.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        switch (tab.getPosition()) {
                            case 0: // Bấm vào "Chỉ định"
                                if (txtSectionTitle != null) txtSectionTitle.setText("Chỉ định / Công dụng");
                                if (txtCongDung != null) txtCongDung.setText(medicine.getIndications());
                                break;

                            case 1: // Bấm vào "Liều dùng"
                                if (txtSectionTitle != null) txtSectionTitle.setText("Liều lượng & Cách dùng");
                                if (txtCongDung != null) txtCongDung.setText("Dữ liệu liều dùng đang được cập nhật...");
                                break;

                            case 2: // Bấm vào "Chống chỉ định"
                                if (txtSectionTitle != null) txtSectionTitle.setText("Chống chỉ định");
                                if (txtCongDung != null) txtCongDung.setText(formatListToString(medicine.getContraindications()));
                                break;

                            case 3: // Bấm vào "Tác dụng phụ"
                                if (txtSectionTitle != null) txtSectionTitle.setText("Tác dụng phụ có thể gặp");
                                if (txtCongDung != null) txtCongDung.setText(formatListToString(medicine.getSideEffects()));
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });
            }
        }
    }

    private String formatListToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "Đang cập nhật dữ liệu.";
        }
        StringBuilder builder = new StringBuilder();
        for (String item : list) {
            builder.append("• ").append(item).append("\n\n");
        }
        return builder.toString().trim();
    }
}