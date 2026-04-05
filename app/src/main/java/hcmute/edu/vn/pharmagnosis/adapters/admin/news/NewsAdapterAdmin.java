package hcmute.edu.vn.pharmagnosis.adapters.admin.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;

public class NewsAdapterAdmin extends RecyclerView.Adapter<NewsAdapterAdmin.NewsViewHolder> {

    private List<HealthNews> newsList;
    private OnNewsItemClickListener listener;

    // 1. Tạo Interface để Fragment bên ngoài lắng nghe sự kiện click
    public interface OnNewsItemClickListener {
        void onEditClick(HealthNews news);
        void onDeleteClick(HealthNews news);
        void onItemClick(HealthNews news); // Dành cho việc xem chi tiết
    }

    // 2. Cập nhật Constructor để nhận Listener
    public NewsAdapterAdmin(List<HealthNews> newsList, OnNewsItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }
    public NewsAdapterAdmin(List<HealthNews> newsList) {
        this.newsList = newsList;
    }
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_admin, parent, false);
        return new NewsViewHolder(view);
    }
    public void setNewsList(List<HealthNews> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged(); // Yêu cầu RecyclerView vẽ lại giao diện với data mới
    }
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        HealthNews news = newsList.get(position);
        if (news == null) return;

        // Gắn dữ liệu Text
        holder.txtNewsTitle.setText(news.getTitle());

        // Gắn ngày tháng (nếu có)
        if (news.getPublishedDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.txtNewsDate.setText(sdf.format(news.getPublishedDate()));
        } else {
            holder.txtNewsDate.setText("Tin tức Y tế");
        }

        // 3. Gắn sự kiện Click cho từng thành phần
        // Click vào Nút Sửa
        holder.imgEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(news);
        });

        // Click vào Nút Xóa
        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(news);
        });

        // Click vào toàn bộ item (Để xem chi tiết)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(news);
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNewsTitle;
        private TextView txtNewsDate;
        private ImageView imgEdit;
        private ImageView imgDelete;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNewsTitle = itemView.findViewById(R.id.tv_news_title);
            txtNewsDate = itemView.findViewById(R.id.tv_news_date);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
    public void removeNews(HealthNews newsToRemove) {
        for (int i = 0; i < newsList.size(); i++) {
            // Tìm bài viết có ID trùng với bài viết vừa xóa
            if (newsList.get(i).getNewId() != null &&
                    newsList.get(i).getNewId().equals(newsToRemove.getNewId())) {

                newsList.remove(i); // Xóa khỏi danh sách dữ liệu
                notifyItemRemoved(i); // Hiệu ứng thu gọn item biến mất
                break;
            }
        }
    }
}