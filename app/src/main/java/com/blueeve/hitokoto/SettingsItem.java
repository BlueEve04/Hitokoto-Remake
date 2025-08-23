package com.blueeve.hitokoto;

public class SettingsItem {
    public enum Type {
        NORMAL, SWITCH, DROPDOWN
    }

    public int leftIconRes;
    public String title;
    public String subtitle;
    public Type type;
    public int rightIconRes; // 仅NORMAL类型有效
    public boolean switchChecked; // 仅SWITCH类型有效
    public String[] dropdownOptions; // 仅DROPDOWN类型有效
    public int dropdownSelected; // 仅DROPDOWN类型有效

    public OnClickListener onClickListener;
    public OnSwitchChangedListener onSwitchChangedListener;
    public OnDropdownChangedListener onDropdownChangedListener;

    public interface OnClickListener {
        void onClick();
    }

    public interface OnSwitchChangedListener {
        void onChanged(boolean checked);
    }

    public interface OnDropdownChangedListener {
        void onChanged(int selectedIndex);
    }

    public SettingsItem(int leftIconRes, String title, String subtitle, Type type) {
        this.leftIconRes = leftIconRes;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
    }
}

