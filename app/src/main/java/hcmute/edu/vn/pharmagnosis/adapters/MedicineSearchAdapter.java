package hcmute.edu.vn.pharmagnosis.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.views.user.MedicineDetailActivity;

public class MedicineSearchAdapter extends RecyclerView.Adapter<MedicineSearchAdapter.MedicineViewHolder> implements Filterable {

    private List<Medicine> medicineListFull; // Kho chứa toàn bộ thuốc kéo từ Firebase về
    private List<Medicine> medicineListFiltered; // Danh sách thuốc đang hiển thị (sau khi lọc)
    private String currentKeyword = "";
    private String currentDosageFilter = "";
    private String currentTargetFilter = "";

    public void applyAdvancedFilter(String keyword, String dosage, String target) {
        this.currentKeyword = keyword != null ? keyword.toLowerCase().trim() : "";
        this.currentDosageFilter = dosage != null ? dosage : "";
        this.currentTargetFilter = target != null ? target : "";

        List<Medicine> filteredList = new java.util.ArrayList<>();

        // Nếu cả 3 đều rỗng (chưa gõ tìm kiếm, chưa chọn lọc) -> Trả về danh sách rỗng (hoặc full tùy bạn)
        if (currentKeyword.isEmpty() && currentDosageFilter.isEmpty() && currentTargetFilter.isEmpty()) {
            this.medicineListFiltered = new java.util.ArrayList<>(); // Để trống màn hình
            notifyDataSetChanged();
            return;
        }

        // Duyệt qua toàn bộ kho thuốc
        for (Medicine m : medicineListFull) {
            boolean matchKeyword = currentKeyword.isEmpty() ||
                    m.getMedicineName().toLowerCase().contains(currentKeyword) ||
                    (m.getTradeName() != null && m.getTradeName().toLowerCase().contains(currentKeyword));

            boolean matchDosage = currentDosageFilter.isEmpty() ||
                    (m.getDosageForm() != null && m.getDosageForm().equals(currentDosageFilter));

            boolean matchTarget = currentTargetFilter.isEmpty() ||
                    (m.getTargetUsers() != null && m.getTargetUsers().equals(currentTargetFilter));

            if (matchKeyword && matchDosage && matchTarget) {
                filteredList.add(m);
            }
        }

        this.medicineListFiltered = filteredList;
        notifyDataSetChanged();
    }

    public MedicineSearchAdapter(List<Medicine> medicineList) {
        this.medicineListFull = new ArrayList<>(medicineList);
        this.medicineListFiltered = new ArrayList<>(medicineList); // Ban đầu chưa gõ gì thì hiển thị tất cả
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicineListFull = new ArrayList<>(medicines);
        this.medicineListFiltered = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineListFiltered.get(position);
        if (medicine == null) return;
        holder.txtMedicineName.setText(medicine.getMedicineName());

        if (medicine.getTradeName() != null && !medicine.getTradeName().isEmpty()) {
            holder.txttradeName.setText("NSX: " + medicine.getTradeName());
        } else {
            holder.txttradeName.setText("NSX: Đang cập nhật");
        }

        if (medicine.getActiveIngredient() != null && !medicine.getActiveIngredient().isEmpty()) {
            holder.txtTag1.setText(medicine.getActiveIngredient().get(0));
            holder.txtTag1.setVisibility(View.VISIBLE);
        } else {
            holder.txtTag1.setVisibility(View.GONE);
        }

        if (medicine.getIndications() != null && !medicine.getIndications().isEmpty()) {
            holder.txtTag2.setText(medicine.getIndications());
            holder.txtTag2.setVisibility(View.VISIBLE);
        } else {
            if (medicine.getTradeName() != null && !medicine.getTradeName().isEmpty()) {
                holder.txtTag2.setText(medicine.getTradeName());
                holder.txtTag2.setVisibility(View.VISIBLE);
            } else {
                holder.txtTag2.setVisibility(View.GONE);
            }
        }

        // --- XỬ LÝ HIỂN THỊ ẢNH (HỖ TRỢ CẢ URL VÀ BASE64) ---
        String imageString = medicine.getImage();
        if (imageString != null && !imageString.isEmpty()) {
            try {
                if (imageString.startsWith("http")) {
                    // Nếu dữ liệu cũ trên Firebase là link URL, dùng Glide
                    Glide.with(holder.itemView.getContext())
                            .load(imageString)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(holder.imgMedicine);
                } else {
                    // Nếu là chuỗi Base64 mới, tự động giải mã ra Bitmap và hiển thị
                    byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.imgMedicine.setImageBitmap(decodedByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.imgMedicine.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.imgMedicine.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, MedicineDetailActivity.class);
            String medicineJson = new com.google.gson.Gson().toJson(medicine);
            intent.putExtra("medicine_json", medicineJson);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return medicineListFiltered != null ? medicineListFiltered.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Medicine> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // Trống thì không làm gì hoặc addAll tùy logic của bạn
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

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
                if (results.values != null) {
                    medicineListFiltered.addAll((List) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedicine;
        TextView txtMedicineName;
        TextView txttradeName;
        TextView txtTag1;
        TextView txtTag2;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMedicine = itemView.findViewById(R.id.imgMedicine);
            txtMedicineName = itemView.findViewById(R.id.txtMedicineName);
            txttradeName = itemView.findViewById(R.id.txttradeName);
            txtTag1 = itemView.findViewById(R.id.txtTag1);
            txtTag2 = itemView.findViewById(R.id.txtTag2);
        }
    }
}