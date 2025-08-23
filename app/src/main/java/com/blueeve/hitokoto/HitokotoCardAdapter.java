package com.blueeve.hitokoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HitokotoCardAdapter extends RecyclerView.Adapter<HitokotoCardAdapter.ViewHolder> {
    private List<JSONObject> windowData;
    private List<Integer> windowColors;
    public int windowStartIndex = 0;

    public HitokotoCardAdapter(List<JSONObject> windowData, List<Integer> windowColors) {
        this.windowData = windowData;
        this.windowColors = windowColors;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject obj = windowData.get(position);
        int color = windowColors.get(position);
        String hitokoto = obj.optString("hitokoto", "").equals("null")?("Err!未找到文字!联系作者解决！"):(obj.optString("hitokoto", ""));
        String from = (obj.optString("from", "").equals("null"))?("未知来源"):(obj.optString("from", ""));
        String fromWho = (obj.optString("from_who", "").equals("null"))?("未知作者"):(obj.optString("from_who", ""));
        String creator = obj.optString("creator", "").equals("null")?("未知"):(obj.optString("creator", ""));
        holder.textView.setText(hitokoto);
        holder.fromView.setText(from.isEmpty() ? "" : ("—— " + from));
        holder.fromWhoView.setText(fromWho.isEmpty() ? "" : fromWho);
        holder.creatorView.setText(creator.isEmpty() ? "" : ("录入者: " + creator));
        // 字体设置
        int fontType = SharedPreferenceManager.getFontType(holder.itemView.getContext());
        if (fontType == 1) {
            holder.textView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hrd));
            holder.fromView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hrd));
            holder.fromWhoView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hrd));
            holder.creatorView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hrd));
        } else if (fontType == 2) {
            holder.textView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hsmx));
            holder.fromView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hsmx));
            holder.fromWhoView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hsmx));
            holder.creatorView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.hsmx));
        } else if (fontType == 3) {
            holder.textView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.sst));
            holder.fromView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.sst));
            holder.fromWhoView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.sst));
            holder.creatorView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.sst));
        } else {
            holder.textView.setTypeface(null);
            holder.fromView.setTypeface(null);
            holder.fromWhoView.setTypeface(null);
            holder.creatorView.setTypeface(null);
        }
        // 字体大小设置
        int fontSize = SharedPreferenceManager.getFontSize(holder.itemView.getContext());
        holder.textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, fontSize);
        holder.fromView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, fontSize - 2);
        holder.fromWhoView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, fontSize - 2);
        holder.creatorView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, fontSize - 4);
        holder.setColor(color);
        // 长按弹窗，支持复制
        holder.itemView.setOnLongClickListener(v -> {
            Context context = v.getContext();
            StringBuilder msg = new StringBuilder();
            msg.append(hitokoto);
            if (!from.isEmpty()) msg.append("\n—— ").append(from);
            if (!fromWho.isEmpty()) msg.append("  ").append(fromWho);
            if (!creator.isEmpty()) msg.append("\n录入者: ").append(creator);
            String content = msg.toString();
            DialogueManager.showDialog(context, "每日一言", content, 2, new String[]{"复制", "关闭"}, which -> {
                if (which == 0) {
                    ClipboardUtil.copy(context, content);
                }
            }, null);
            return true;
        });
        // 支持手指滚动内容
        holder.textView.setMovementMethod(new android.text.method.ScrollingMovementMethod());
        holder.textView.setVerticalScrollBarEnabled(true);
        holder.textView.setFocusable(true);
        holder.textView.setFocusableInTouchMode(true);
        holder.textView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & android.view.MotionEvent.ACTION_MASK) == android.view.MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }
    @Override
    public int getItemCount() {
        return windowData.size();
    }
    public void setWindowData(List<JSONObject> windowData, List<Integer> windowColors, int windowStartIndex) {
        this.windowData = windowData;
        this.windowColors = windowColors;
        this.windowStartIndex = windowStartIndex;
    }

    // CardStateProvider接口
    public interface CardStateProvider {
        Integer getTargetColor(int globalIndex);
        boolean isFixed(int globalIndex);
        Float getProgress(int globalIndex);
    }
    private CardStateProvider stateProvider;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, fromView, fromWhoView, creatorView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cardText);
            fromView = itemView.findViewById(R.id.cardFrom);
            fromWhoView = itemView.findViewById(R.id.cardFromWho);
            creatorView = itemView.findViewById(R.id.cardCreator);

        }
        public void setColor(int color) {
            Context context = itemView.getContext();
            boolean colorful = SharedPreferenceManager.isColorfulCardEnabled(context);
            if (colorful) {
                if (itemView instanceof androidx.cardview.widget.CardView) {
                    ((androidx.cardview.widget.CardView) itemView).setCardBackgroundColor(color);
                } else {
                    itemView.setBackgroundColor(color);
                }
            } else {
                int cardBg = ContextCompat.getColor(context, R.color.card_bg);
                if (itemView instanceof androidx.cardview.widget.CardView) {
                    ((androidx.cardview.widget.CardView) itemView).setCardBackgroundColor(cardBg);
                } else {
                    itemView.setBackgroundColor(cardBg);
                }
            }
        }
    }
}
