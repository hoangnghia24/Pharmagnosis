package hcmute.edu.vn.pharmagnosis.adapters.admin.medicines;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Medicine;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList = new ArrayList<>();
    private final OnMedicineActionListener actionListener;

    public interface OnMedicineActionListener {
        void onEditClick(Medicine medicine);
        void onDeleteClick(Medicine medicine);
        void onItemClick(Medicine medicine);
    }

    public MedicineAdapter(OnMedicineActionListener actionListener) {
        this.actionListener = actionListener;
    }

    // Hàm gán dữ liệu
    public void setMedicineList(List<Medicine> medicineList) {
        this.medicineList = medicineList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvMedicineName.setText(medicine.getMedicineName());
        holder.tvDosageForm.setText(medicine.getDosageForm());

        // --- XỬ LÝ HIỂN THỊ ẢNH (HỖ TRỢ CẢ URL VÀ BASE64) ---
        String imageString = medicine.getImage();
        if (imageString != null && !imageString.isEmpty()) {
            try {
                if (imageString.startsWith("http")) {
                    // Nếu dữ liệu cũ trên Firebase là link URL, dùng Glide
                    Glide.with(holder.itemView.getContext())
                            .load(imageString)
                            .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh hiển thị tạm
                            .into(holder.imgThumbnail);
                } else {
                    // Nếu là chuỗi Base64 mới, giải mã ra Bitmap và hiển thị
                    byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.imgThumbnail.setImageBitmap(decodedByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.imgThumbnail.setImageResource(android.R.drawable.ic_menu_gallery); // Set ảnh mặc định nếu lỗi
            }
        } else {
            holder.imgThumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // --- XỬ LÝ SỰ KIỆN CLICK ---
        holder.imgEdit.setOnClickListener(v -> actionListener.onEditClick(medicine));
        holder.imgDelete.setOnClickListener(v -> actionListener.onDeleteClick(medicine));
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onItemClick(medicine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList != null ? medicineList.size() : 0;
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosageForm;
        ImageView imgThumbnail, imgEdit, imgDelete;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tv_item_medicine_name);
            tvDosageForm = itemView.findViewById(R.id.tv_item_dosage_form);
            imgThumbnail = itemView.findViewById(R.id.img_medicine_thumbnail);
            imgEdit = itemView.findViewById(R.id.img_btn_edit);
            imgDelete = itemView.findViewById(R.id.img_btn_delete);
        }
    }
}