package com.blueeve.hitokoto;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import org.json.JSONObject;

public class HitokotoWidgetProvider extends AppWidgetProvider {
    static final String ACTION_FAV = "com.blueeve.hitokoto.ACTION_FAV";
    static final String ACTION_COPY = "com.blueeve.hitokoto.ACTION_COPY";
    static final String ACTION_REFRESH = "com.blueeve.hitokoto.ACTION_REFRESH";
    static final String ACTION_AUTO_REFRESH = "com.blueeve.hitokoto.ACTION_AUTO_REFRESH";
    static final String EXTRA_HITOKOTO_JSON = "extra_hitokoto_json";
    static final String PREFS_NAME = "widget_prefs";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            scheduleOrCancelRefresh(context, appWidgetId);
        }
    }

    private void scheduleOrCancelRefresh(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long interval = prefs.getLong("refreshInterval_" + appWidgetId, prefs.getLong("refreshInterval_global", 0L));
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, HitokotoWidgetProvider.class);
        intent.setAction(ACTION_AUTO_REFRESH);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(context, appWidgetId + 30000, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        if (interval > 0) {
            long triggerAt = System.currentTimeMillis() + interval;
            alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, triggerAt, interval, pendingIntent);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, HitokotoWidgetProvider.class);
            intent.setAction(ACTION_AUTO_REFRESH);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(context, appWidgetId + 30000, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_hitokoto);
        JSONObject hitokoto = HitokotoProvider.getRandomHitokoto(context);
        String hitokotoJson = hitokoto != null ? hitokoto.toString() : "";
        if (hitokoto != null) {
            String text = hitokoto.optString("hitokoto", "");
            String from = hitokoto.optString("from", "").equals("null") ? "未知来源" : hitokoto.optString("from", "");
            String author = hitokoto.optString("from_who", "").equals("null") ? "未知作者" : hitokoto.optString("from_who", "");
            views.setTextViewText(R.id.tv_hitokoto, text);
            views.setTextViewText(R.id.tv_from, "—— " + from);
            views.setTextViewText(R.id.tv_author, author);
        }
        // 读取设置
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int maxLines = prefs.getInt("maxLines_" + appWidgetId, 4);
        String font = prefs.getString("font_" + appWidgetId, "系统默认");
        String bg = prefs.getString("bg_" + appWidgetId, "白色");
        // 设置最大行数
        views.setInt(R.id.tv_hitokoto, "setMaxLines", maxLines);
        // 设置字体（仅支持系统字体，assets字体需特殊处理，RemoteViews不支持 setTypeface）
        if (!"系统默认".equals(font)) {
            // TODO: RemoteViews 不支持 setTypeface ,GG
        }
        // 设置背景（使用圆角xml）
        int bgResId = R.drawable.bg_widget_white;
        switch (bg) {
            case "白色":
                bgResId = R.drawable.bg_widget_white; break;
            case "黑色":
                bgResId = R.drawable.bg_widget_black; break;
            case "黄纸本":
                bgResId = R.drawable.bg_widget_yellow; break;
            case "透明":
                bgResId = R.drawable.bg_widget_trans; break;
            case "跟随系统":
                int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                bgResId = (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES)
                    ? R.drawable.bg_widget_black : R.drawable.bg_widget_white;
                break;
        }
        views.setInt(R.id.widget_root, "setBackgroundResource", bgResId);
        // TODO: 字体颜色
        // 优先读取单独设置，否则读取全局设置，最后默认黑色
        String fontColor = prefs.getString("fontColor_" + appWidgetId, null);
        if (fontColor == null) fontColor = prefs.getString("fontColor_global", "黑色");
        int textColor = 0xFF222222; // 默认黑色
        switch (fontColor) {
            case "黑色":
                textColor = 0xFF222222; break;
            case "白色":
                textColor = 0xFFFFFFFF; break;
            case "跟随系统":
                int nightModeFlags2 = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                textColor = (nightModeFlags2 == android.content.res.Configuration.UI_MODE_NIGHT_YES) ? 0xFFFFFFFF : 0xFF222222;
                break;
        }
        views.setTextColor(R.id.tv_hitokoto, textColor);
        // 来源显示与颜色
        Boolean showFrom = prefs.contains("showFrom_" + appWidgetId) ? prefs.getBoolean("showFrom_" + appWidgetId, true)
            : prefs.getBoolean("showFrom_global", true);
        String fromColor = prefs.contains("fromColor_" + appWidgetId) ? prefs.getString("fromColor_" + appWidgetId, "灰色")
            : prefs.getString("fromColor_global", "灰色");
        int fromTextColor = 0xFF888888; // 默认灰色
        switch (fromColor) {
            case "灰色":
                fromTextColor = 0xFF888888; break;
            case "白色":
                fromTextColor = 0xFFFFFFFF; break;
            case "黑色":
                fromTextColor = 0xFF222222; break;
        }
        views.setViewVisibility(R.id.tv_from, showFrom ? android.view.View.VISIBLE : android.view.View.GONE);
        views.setTextColor(R.id.tv_from, fromTextColor);
        // 作者显示
        Boolean showAuthor = prefs.contains("showAuthor_" + appWidgetId) ? prefs.getBoolean("showAuthor_" + appWidgetId, true)
            : prefs.getBoolean("showAuthor_global", true);
        views.setViewVisibility(R.id.tv_author, showAuthor ? android.view.View.VISIBLE : android.view.View.GONE);
        // 字体设置（仅支持系统字体，assets和自定义字体需主程序支持）
        if (!"系统默认".equals(font)) {
            // TODO: RemoteViews 不支持 setTypeface，主程序可用
        }
        // 收藏按钮点击事件
        Intent favIntent = new Intent(context, HitokotoWidgetProvider.class);
        favIntent.setAction(ACTION_FAV);
        favIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        favIntent.putExtra(EXTRA_HITOKOTO_JSON, hitokotoJson);
        PendingIntent favPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, favIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btn_fav, favPendingIntent);
        // 复制按钮点击事件
        Intent copyIntent = new Intent(context, HitokotoWidgetProvider.class);
        copyIntent.setAction(ACTION_COPY);
        copyIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        copyIntent.putExtra(EXTRA_HITOKOTO_JSON, hitokotoJson);
        PendingIntent copyPendingIntent = PendingIntent.getBroadcast(context, appWidgetId + 10000, copyIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btn_copy, copyPendingIntent);
        // 空白区域点击切换句子
        Intent refreshIntent = new Intent(context, HitokotoWidgetProvider.class);
        refreshIntent.setAction(ACTION_REFRESH);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, appWidgetId + 20000, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, refreshPendingIntent);
        // 设置图标颜色（与字体颜色一致）
        views.setInt(R.id.btn_fav, "setColorFilter", textColor);
        views.setInt(R.id.btn_copy, "setColorFilter", textColor);
        // TODO: 读取用户设置切换背景色和字体 意义不大QwQ创建多个比较难修改
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (ACTION_FAV.equals(action)) {
            String json = intent.getStringExtra(EXTRA_HITOKOTO_JSON);
            if (json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    boolean added = FavoriteManager.addFavorite(context, obj);
                    String msg = added ? "已收藏" : "已存在于收藏";
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (ACTION_COPY.equals(action)) {
            String json = intent.getStringExtra(EXTRA_HITOKOTO_JSON);
            if (json != null) {
                try {
                    JSONObject obj = new JSONObject(json);
                    String text = obj.optString("hitokoto", "");
                    String from = obj.optString("from", "").equals("null") ? "未知来源" : obj.optString("from", "");
                    String author = obj.optString("from_who", "").equals("null") ? "未知作者" : obj.optString("from_who", "");
                    ClipboardUtil.copy(context, text + (from.isEmpty() ? "" : ("\n—— " + from)) + (author.isEmpty() ? "" : ("  " + author)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (ACTION_REFRESH.equals(action)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        } else if (ACTION_AUTO_REFRESH.equals(action)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }
}
