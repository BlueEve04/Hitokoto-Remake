package com.blueeve.hitokoto;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<JSONObject> data;
    private Context context;
    public FavoriteAdapter(Context context, List<JSONObject> data) {
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject obj = data.get(position);
        holder.tvIndex.setText(String.valueOf(position + 1));
        String hitokoto = obj.optString("hitokoto", "");
        String from = obj.optString("from", "").equals("null") ? "未知来源" : obj.optString("from", "");
        String fromWho = obj.optString("from_who", "").equals("null") ? "未知作者" : obj.optString("from_who", "");
        String creator = obj.optString("creator", "").equals("null") ? "未知" : obj.optString("creator", "");
        String author = fromWho.isEmpty() ? (creator.isEmpty() ? "" : creator) : fromWho;
        holder.tvContent.setText(hitokoto);
        holder.tvContent.setSelected(true); // 跑马灯
        holder.tvAuthor.setText(author);
        holder.itemView.setOnClickListener(v -> {
            StringBuilder msg = new StringBuilder();
            msg.append(hitokoto);
            if (!from.isEmpty()) msg.append("\n—— ").append(from);
            if (!fromWho.isEmpty()) msg.append("  ").append(fromWho);
            if (!creator.isEmpty()) msg.append("\n录入者: ").append(creator);
            String content = msg.toString();
            new AlertDialog.Builder(context)
                .setTitle("收藏详情")
                .setMessage(content)
                .setPositiveButton("复制", (dialog, which) -> ClipboardUtil.copy(context, content))
                .setNegativeButton("关闭", null)
                .show();
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvContent, tvAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
