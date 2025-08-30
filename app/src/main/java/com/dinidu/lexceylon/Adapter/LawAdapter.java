package com.dinidu.lexceylon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinidu.lexceylon.Model.LawItem;
import com.dinidu.lexceylon.R;
import java.util.ArrayList;
import java.util.List;

public class LawAdapter extends RecyclerView.Adapter<LawAdapter.LawVH> {

    public interface OnItemClick { void onClick(LawItem item); }
    private final List<LawItem> data = new ArrayList<>();
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick click) { this.onItemClick = click; }

    public void submitList(List<LawItem> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LawVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_law, parent, false);
        return new LawVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LawVH h, int pos) {
        LawItem it = data.get(pos);
        h.tvTitle.setText(it.getTitle() != null ? it.getTitle() : "");

        String meta = "";
        if (it.getAct() != null && !it.getAct().isEmpty()) meta += it.getAct();
        if (it.getSection() != null && !it.getSection().isEmpty()) meta += (meta.isEmpty() ? "" : " • ") + it.getSection();
        if (it.getYear() != null) meta += (meta.isEmpty() ? "" : " • ") + String.valueOf(it.getYear());
        h.tvMeta.setText(meta);

        h.tvSnippet.setText(it.getContent() != null ? it.getContent() : "");

        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(it);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class LawVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta, tvSnippet;
        LawVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta  = itemView.findViewById(R.id.tvMeta);
            tvSnippet = itemView.findViewById(R.id.tvSnippet);
        }
    }
}
