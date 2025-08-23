package com.blueeve.hitokoto;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final List<SettingsItem> items;
    private final Context context;

    public SettingsAdapter(Context context, List<SettingsItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type.ordinal();
    }

    @NonNull
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ViewHolder holder, int position) {
        SettingsItem item = items.get(position);
        holder.leftIcon.setImageResource(item.leftIconRes);
        // 设置左侧图标深浅色适配
        int iconTint = ContextCompat.getColor(context, R.color.iconTint);
        holder.leftIcon.setImageTintList(ColorStateList.valueOf(iconTint));
        holder.title.setText(item.title);
        holder.subtitle.setText(item.subtitle);

        holder.rightIcon.setVisibility(View.GONE);
        holder.switchView.setVisibility(View.GONE);
        holder.dropdown.setVisibility(View.GONE);

        if (item.type == SettingsItem.Type.NORMAL) {
            holder.rightIcon.setVisibility(View.VISIBLE);
            holder.rightIcon.setImageResource(item.rightIconRes);

            // 设置右侧图标颜色，支持深浅色切换
            int color = ContextCompat.getColor(context, R.color.TextSecondary);
            holder.rightIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            holder.itemView.setOnClickListener(v -> {
                if (item.onClickListener != null) item.onClickListener.onClick();
            });
        } else if (item.type == SettingsItem.Type.SWITCH) {
            holder.switchView.setVisibility(View.VISIBLE);
            holder.switchView.setChecked(item.switchChecked);
            holder.switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.switchChecked = isChecked;
                if (item.onSwitchChangedListener != null) item.onSwitchChangedListener.onChanged(isChecked);
            });
            holder.itemView.setOnClickListener(v -> holder.switchView.toggle());
        } else if (item.type == SettingsItem.Type.DROPDOWN) {
            holder.dropdown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, item.dropdownOptions);
            holder.dropdown.setAdapter(adapter);
            holder.dropdown.setSelection(item.dropdownSelected, false);
            holder.dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (item.dropdownSelected != pos) {
                        item.dropdownSelected = pos;
                        if (item.onDropdownChangedListener != null) item.onDropdownChangedListener.onChanged(pos);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView leftIcon;
        TextView title;
        TextView subtitle;
        ImageView rightIcon;
        Switch switchView;
        Spinner dropdown;

        ViewHolder(View itemView) {
            super(itemView);
            leftIcon = itemView.findViewById(R.id.setting_left_icon);
            title = itemView.findViewById(R.id.setting_title);
            subtitle = itemView.findViewById(R.id.setting_subtitle);
            rightIcon = itemView.findViewById(R.id.setting_right_icon);
            switchView = itemView.findViewById(R.id.setting_switch);
            dropdown = itemView.findViewById(R.id.setting_dropdown);
        }
    }
}
