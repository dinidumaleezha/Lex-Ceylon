package com.dinidu.lexceylon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinidu.lexceylon.Model.HistoryModel;
import com.dinidu.lexceylon.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryModel> historyList;
    private List<HistoryModel> fullList; // Original list for filtering

    public HistoryAdapter(List<HistoryModel> historyList) {
        this.historyList = historyList;
        this.fullList = new ArrayList<>(historyList); // Keep full copy
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryModel history = historyList.get(position);
        holder.title.setText(history.getTitle());
        holder.date.setText(history.getDate());

        holder.title.setSelected(false);
        //holder.title.postDelayed(() -> holder.title.setSelected(true), 5000);
        holder.title.setSelected(false);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
        // return Math.min(historyList.size(), 3);
    }

    /** -------------------- REFRESH ORIGINAL LIST -------------------- **/
    public void updateFullList(List<HistoryModel> newList) {
        fullList.clear();
        fullList.addAll(newList);
        historyList.clear();
        historyList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.historyTitle);
            date = itemView.findViewById(R.id.historyDate);
        }
    }
}
