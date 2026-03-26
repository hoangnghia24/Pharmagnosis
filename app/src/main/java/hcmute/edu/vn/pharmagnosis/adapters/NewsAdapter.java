package hcmute.edu.vn.pharmagnosis.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.models.HealthNews;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<HealthNews> newsList;

    public NewsAdapter(List<HealthNews> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        HealthNews news = newsList.get(position);
        if (news == null) return;

        // Gắn dữ liệu Title từ file HealthNews
        holder.txtNewsTitle.setText(news.getTitle());
        holder.txtNewsTag.setText("Tin tức Y tế");

        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(news.getImage())
                .into(holder.imgNews);
        // THÊM ĐOẠN CODE NÀY ĐỂ BẮT SỰ KIỆN CLICK VÀO THẺ TIN TỨC
        holder.itemView.setOnClickListener(v -> {
            // Lấy Context từ View hiện tại
            android.content.Context context = v.getContext();

            // Tạo Intent để chuyển sang màn hình Chi tiết thuốc (MedicineDetailActivity)
            // Nếu sau này bạn có trang NewsDetailActivity, chỉ cần đổi tên class ở dòng dưới là xong
            android.content.Intent intent = new android.content.Intent(context, hcmute.edu.vn.pharmagnosis.views.user.NewsDetailActivity.class);
            intent.putExtra("NEWS_TITLE", news.getTitle());
            intent.putExtra("NEWS_CONTENT", news.getContent());
            intent.putExtra("NEWS_IMAGE", news.getImage());
            // Lệnh bắt đầu mở màn hình mới
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgNews;
        private TextView txtNewsTag;
        private TextView txtNewsTitle;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNews = itemView.findViewById(R.id.imgNews);
            txtNewsTag = itemView.findViewById(R.id.txtNewsTag);
            txtNewsTitle = itemView.findViewById(R.id.txtNewsTitle);
        }
    }
}