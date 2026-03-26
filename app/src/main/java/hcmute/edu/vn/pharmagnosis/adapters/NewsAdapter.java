package hcmute.edu.vn.pharmagnosis.adapters;

import android.content.Context;
import android.content.Intent;
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

        // 1. ĐÃ SỬA: Gắn dữ liệu vào đúng các View mà XML của bạn đang có
        holder.txtNewsTitle.setText(news.getTitle());
        holder.txtNewsDate.setText("Tin tức Y tế"); // Tạm gán chữ này vào vị trí ngày đăng

        // 2. ĐÃ SỬA: Tạm vô hiệu hóa tính năng load ảnh bằng Glide.
        // Vì trong file item_news.xml của bạn không có ImageView (ảnh bìa) nên gọi lệnh này app sẽ văng.
        /*
        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(news.getImage())
                .into(holder.imgNews);
        */

        // Bắt sự kiện Click vào thẻ tin tức (Giữ nguyên của bạn)
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, hcmute.edu.vn.pharmagnosis.views.user.NewsDetailActivity.class);
            intent.putExtra("NEWS_TITLE", news.getTitle());
            intent.putExtra("NEWS_CONTENT", news.getContent());
            intent.putExtra("NEWS_IMAGE", news.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        // ĐÃ SỬA: Khai báo đúng các thành phần có trong file item_news.xml của bạn
        private TextView txtNewsTitle;
        private TextView txtNewsDate;
        private ImageView imgEdit;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            // ĐÃ SỬA: Ánh xạ khớp 100% với ID trong file XML để hết bị văng app
            txtNewsTitle = itemView.findViewById(R.id.tv_news_title);
            txtNewsDate = itemView.findViewById(R.id.tv_news_date);
            imgEdit = itemView.findViewById(R.id.img_edit);
        }
    }
}