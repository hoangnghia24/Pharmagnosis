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
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<SearchRecord> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(SearchRecord record);
        void onDeleteClick(SearchRecord record);
    }

    public SearchHistoryAdapter(List<SearchRecord> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    public void setHistoryList(List<SearchRecord> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchRecord record = historyList.get(position);
        holder.txtKeyword.setText(record.getKeyword());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHistoryClick(record);
        });
        
        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(record);
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtKeyword;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKeyword = itemView.findViewById(R.id.txtHistoryKeyword);
            imgDelete = itemView.findViewById(R.id.imgDeleteHistory);
        }
    }
}
