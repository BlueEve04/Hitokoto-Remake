package com.blueeve.hitokoto;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private static final String PREF_NAME = "app_settings";
    private static final String KEY_COLORFUL_CARD = "colorful_card";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_FONT_TYPE = "font_type";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_COLOR_STYLE = "color_style"; // 0=莫奈柔和, 1=鲜艳

    public static boolean isColorfulCardEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_COLORFUL_CARD, true); // 默认开启彩色卡片
    }

    public static void setColorfulCardEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_COLORFUL_CARD, enabled).apply();
    }

    public static int getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, -1); // -1 表示跟随系统
    }

    public static void setThemeMode(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public static int getFontType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_FONT_TYPE, 0); // 0=系统字体, 1=hrd, 2=hsmx, 3=sst
    }

    public static void setFontType(Context context, int type) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_FONT_TYPE, type).apply();
    }

    public static int getFontSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_FONT_SIZE, 18); // 默认18sp
    }

    public static void setFontSize(Context context, int size) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_FONT_SIZE, size).apply();
    }

    public static int getColorStyle(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_COLOR_STYLE, 0); // 默认莫奈柔和
    }

    public static void setColorStyle(Context context, int style) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_COLOR_STYLE, style).apply();
    }
}
