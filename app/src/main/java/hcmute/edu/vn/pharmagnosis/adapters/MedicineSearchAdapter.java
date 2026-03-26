package hcmute.edu.vn.pharmagnosis.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.views.user.MedicineDetailActivity;

// Thêm "implements Filterable" để cấp phép cho Adapter này khả năng tìm kiếm
public class MedicineSearchAdapter extends RecyclerView.Adapter<MedicineSearchAdapter.MedicineViewHolder> implements Filterable {

    private List<Medicine> medicineListFull; // Kho chứa toàn bộ thuốc kéo từ Firebase về
    private List<Medicine> medicineListFiltered; // Danh sách thuốc đang hiển thị (sau khi lọc)

    public MedicineSearchAdapter(List<Medicine> medicineList) {
        this.medicineListFull = new ArrayList<>(medicineList);
        this.medicineListFiltered = new ArrayList<>(medicineList); // Ban đầu chưa gõ gì thì hiển thị tất cả
    }

    // Cập nhật lại kho dữ liệu khi Firebase tải xong
    public void setMedicines(List<Medicine> medicines) {
        this.medicineListFull = new ArrayList<>(medicines);
        this.medicineListFiltered = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp file giao diện XML của bạn vào đây
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineListFiltered.get(position);
        if (medicine == null) return;

        // 1. Gán Tên thuốc
        holder.txtMedicineName.setText(medicine.getMedicineName());


        // 3. Dùng Glide tải ảnh thuốc từ link URL trên Firebase
        if (medicine.getImage() != null && !medicine.getImage().isEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(medicine.getImage())
                    .into(holder.imgMedicine);
        }

        // 4. Bắt sự kiện click để nhảy sang màn hình Chi tiết thuốc
        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            android.content.Intent intent = new android.content.Intent(context, hcmute.edu.vn.pharmagnosis.views.user.MedicineDetailActivity.class);

            // Gói hành lý mang sang trang chi tiết (Sau này mình sẽ bắt bên kia)
            intent.putExtra("MEDICINE_OBJ", (java.io.Serializable) medicine);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return medicineListFiltered != null ? medicineListFiltered.size() : 0;
    }

    // BỘ LỌC TÌM KIẾM THẦN THÁNH Ở ĐÂY
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Medicine> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {

                } else {
                    // Chuyển chữ gõ thành chữ thường để không phân biệt hoa/thường
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    // Duyệt qua kho thuốc, ai có tên khớp thì bỏ vào giỏ hàngPa
                    for (Medicine item : medicineListFull) {
                        if (item.getMedicineName().toLowerCase().contains(filterPattern) ||
                                (item.getTradeName() != null && item.getTradeName().toLowerCase().contains(filterPattern))) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                medicineListFiltered.clear();
                medicineListFiltered.addAll((List) results.values);
                // Lệnh quan trọng nhất: Yêu cầu Adapter in lại danh sách mới lên màn hình
                notifyDataSetChanged();
            }
        };
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedicine;
        TextView txtMedicineName;
        TextView txtManufacturer;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ khớp với ID trong file XML của bạn
            imgMedicine = itemView.findViewById(R.id.imgMedicine);
            txtMedicineName = itemView.findViewById(R.id.txtMedicineName);
            txtManufacturer = itemView.findViewById(R.id.txtManufacturer);
        }
    }
}