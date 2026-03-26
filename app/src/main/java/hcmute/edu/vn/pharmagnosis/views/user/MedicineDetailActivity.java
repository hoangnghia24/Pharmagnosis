package hcmute.edu.vn.pharmagnosis.views.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import hcmute.edu.vn.pharmagnosis.R;

public class MedicineDetailActivity extends AppCompatActivity {
    private ImageView imgMedicine;
    private ImageView imgBack;
    // Khai báo thêm các thẻ hiển thị chữ
    private TextView txtTitle;
    private TextView txtCongDung;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        // ÁNH XẠ VIEW (Bạn nhớ sửa R.id... cho khớp với ID trong file XML của bạn nhé)
        txtTitle = findViewById(R.id.txtMedicineName); // Đổi ID này nếu file XML bạn đặt tên khác
        txtCongDung = findViewById(R.id.txtMedicineDetailContent); // Đổi ID này nếu file XML bạn đặt tên khác
        imgMedicine = findViewById(R.id.imgMedicine); // Tôi đang giả định ID trong XML của bạn là 'imgMedicineCover'. Nếu bạn đặt tên khác, hãy sửa lại cho khớp nhé!
        // NHẬN VALI HÀNH LÝ
        hcmute.edu.vn.pharmagnosis.models.Medicine medicine =
                (hcmute.edu.vn.pharmagnosis.models.Medicine) getIntent().getSerializableExtra("MEDICINE_OBJ");

        if (medicine != null) {
            // Đắp dữ liệu thật lên giao diện!
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
        }
    }
}