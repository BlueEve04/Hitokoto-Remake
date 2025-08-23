package com.blueeve.hitokoto;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WidgetConfigActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText etMaxLines;
    private Spinner spinnerFont, spinnerBg, spinnerFontColor, spinnerFromColor, spinnerRefreshInterval;
    private Button btnSave;
    private Switch switchShowFrom, switchShowAuthor;
    private static final String PREFS_NAME = "widget_prefs";
    private static final String[] BG_OPTIONS = {"白色", "黑色", "跟随系统", "透明","黄纸本"};
    private static final String[] FONT_COLOR_OPTIONS = {"黑色", "白色", "跟随系统"};
    private static final String[] FROM_COLOR_OPTIONS = {"灰色", "白色", "黑色"};
    private static final String[] REFRESH_INTERVAL_OPTIONS = {"15分钟", "30分钟", "1小时", "2小时", "5小时", "12小时", "24小时", "永不自动刷新"};
    private static final long[] REFRESH_INTERVAL_VALUES = {15*60*1000L, 30*60*1000L, 60*60*1000L, 2*60*60*1000L, 5*60*60*1000L, 12*60*60*1000L, 24*60*60*1000L, 0L};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_config);
        etMaxLines = findViewById(R.id.et_max_lines);
        spinnerFont = findViewById(R.id.spinner_font);
        spinnerBg = findViewById(R.id.spinner_bg);
        spinnerFontColor = findViewById(R.id.spinner_font_color);
        spinnerFromColor = findViewById(R.id.spinner_from_color);
        spinnerRefreshInterval = findViewById(R.id.spinner_refresh_interval);
        btnSave = findViewById(R.id.btn_save);
        switchShowFrom = findViewById(R.id.switch_show_from);
        switchShowAuthor = findViewById(R.id.switch_show_author);

        // 获取 appWidgetId
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            // 允许无 appWidgetId 进入全局设置模式
            appWidgetId = -1;
        }

        // 字体选择
        List<String> fontList = new ArrayList<>();
        fontList.add("系统默认");
        try {
            String[] fonts = getAssets().list("");
            if (fonts != null) {
                for (String f : fonts) {
                    if (f.endsWith(".ttf") || f.endsWith(".otf")) fontList.add(f);
                }
            }
        } catch (IOException ignored) {}
        // 字体选择（支持自定义目录）
        java.io.File customFontDir = new java.io.File("/storage/emulated/0/HitokotoFonts/");
        if (customFontDir.exists() && customFontDir.isDirectory()) {
            java.io.File[] files = customFontDir.listFiles();
            if (files != null) {
                for (java.io.File f : files) {
                    if (f.getName().endsWith(".ttf") || f.getName().endsWith(".otf")) {
                        fontList.add("[自定义]" + f.getName());
                    }
                }
            }
        }
        ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fontList);
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFont.setAdapter(fontAdapter);

        // 背景色选择
        ArrayAdapter<String> bgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, BG_OPTIONS);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBg.setAdapter(bgAdapter);
        // 字体颜色选择
        ArrayAdapter<String> fontColorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, FONT_COLOR_OPTIONS);
        fontColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFontColor.setAdapter(fontColorAdapter);
        // 来源字体颜色选择
        ArrayAdapter<String> fromColorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, FROM_COLOR_OPTIONS);
        fromColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromColor.setAdapter(fromColorAdapter);
        // 刷新间隔选择
        ArrayAdapter<String> refreshIntervalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, REFRESH_INTERVAL_OPTIONS);
        refreshIntervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRefreshInterval.setAdapter(refreshIntervalAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig();
            }
        });
        // 设置窗口宽度为屏幕宽度的90%
        android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        params.width = (int) (dm.widthPixels * 0.9);
        getWindow().setAttributes(params);
    }

    private void saveConfig() {
        int maxLines = 4;
        try {
            maxLines = Integer.parseInt(etMaxLines.getText().toString());
        } catch (Exception ignored) {}
        String font = spinnerFont.getSelectedItem().toString();
        String bg = spinnerBg.getSelectedItem().toString();
        String fontColor = spinnerFontColor.getSelectedItem().toString();
        boolean showFrom = switchShowFrom.isChecked();
        String fromColor = spinnerFromColor.getSelectedItem().toString();
        boolean showAuthor = switchShowAuthor.isChecked();
        int refreshIntervalIndex = spinnerRefreshInterval.getSelectedItemPosition();
        long refreshIntervalValue = REFRESH_INTERVAL_VALUES[refreshIntervalIndex];
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (appWidgetId == -1) {
            editor.putInt("maxLines_global", maxLines);
            editor.putString("font_global", font);
            editor.putString("bg_global", bg);
            editor.putString("fontColor_global", fontColor);
            editor.putBoolean("showFrom_global", showFrom);
            editor.putString("fromColor_global", fromColor);
            editor.putBoolean("showAuthor_global", showAuthor);
            editor.putLong("refreshInterval_global", refreshIntervalValue);
            editor.apply();
            android.widget.Toast.makeText(this, "全局设置已保存", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            editor.putInt("maxLines_" + appWidgetId, maxLines);
            editor.putString("font_" + appWidgetId, font);
            editor.putString("bg_" + appWidgetId, bg);
            editor.putString("fontColor_" + appWidgetId, fontColor);
            editor.putBoolean("showFrom_" + appWidgetId, showFrom);
            editor.putString("fromColor_" + appWidgetId, fromColor);
            editor.putBoolean("showAuthor_" + appWidgetId, showAuthor);
            editor.putLong("refreshInterval_" + appWidgetId, refreshIntervalValue);
            editor.apply();
            // 刷新小部件
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            HitokotoWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);
            // 返回结果
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }
}
