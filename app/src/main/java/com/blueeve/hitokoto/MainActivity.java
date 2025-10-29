package com.blueeve.hitokoto;
//黄令东齐伋体
import static com.blueeve.hitokoto.SharedPreferenceManager.updatelevel;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HitokotoCardAdapter adapter;
    private LinearLayoutManager layoutManager;
    // 缓存池
    private Map<Integer, org.json.JSONObject> contentCache = new HashMap<>();
    private Map<Integer, Integer> colorCache = new HashMap<>();
    // 当前窗口维护的index列
    private LinkedList<Integer> windowIndices = new LinkedList<>();
    private int lastCenter; // 用于永续滑动的中心卡片索引记录



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 沉浸式透明系统栏设置
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                // 深色模式下自动适配状态栏/导航栏图标颜色
                int appearance = 0;
                int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
                    // 日间模式，使用深色图标
                    appearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                }
                controller.setSystemBarsAppearance(appearance, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            }
        }
        else {
            window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
            // 兼容旧版，深色模式下状态栏/导航栏图标自动适配
            if ((getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }
//         读取主题设置并应用，保证重启后主题生效
        int themeMode = SharedPreferenceManager.getThemeMode(this);
        if (themeMode == -1) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (themeMode == android.app.UiModeManager.MODE_NIGHT_NO) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themeMode == android.app.UiModeManager.MODE_NIGHT_YES) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        }
        int isOpened=SharedPreferenceManager.updatelevel(this);
        if(isOpened==0){
            Toast.makeText(this, "欢迎使用一言！", Toast.LENGTH_LONG).show();
            SharedPreferenceManager.setKeyUpdateLevel(this,1);
        }else{
            Toast.makeText(this , "欢迎回来！-TEST", Toast.LENGTH_SHORT).show();
        }
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HitokotoCardAdapter(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

//        for (int i = 1; i <= 10; i++) {
//            if (!contentCache.containsKey(i)) contentCache.put(i, HitokotoProvider.getRandomHitokoto(this));
//            if (!colorCache.containsKey(i)) colorCache.put(i, getRandomMonetColor());
//        }
//        updateWindowData();
//        adapter.notifyDataSetChanged();
        for (int i = 0; i < 10; i++) {
            contentCache.put(i, HitokotoProvider.getRandomHitokoto(this));
            colorCache.put(i, getRandomMonetColor());
            windowIndices.add(i);
        }
        updateWindowData();
        adapter.notifyDataSetChanged();

        // 记录上一次中心卡片位置，初始为窗口中间
        lastCenter = windowIndices.size() / 2;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int first = layoutManager.findFirstVisibleItemPosition();
                int last = layoutManager.findLastVisibleItemPosition();
                int center = first + (last - first) / 2;
                android.util.Log.d("CARD", "onScrolled: first=" + first + ", last=" + last + ", center=" + center + ", lastCenter=" + lastCenter);
                if (center != lastCenter) {
                    recyclerView.post(() -> {
                        int newIdx = windowIndices.getLast() + 1;
                        android.util.Log.d("CARD", "onScrolled(post): center changed, append card index=" + newIdx);
                        windowIndices.addLast(newIdx);
                        if (!contentCache.containsKey(newIdx)) {
                            android.util.Log.d("CARD", "Load new card content for index=" + newIdx);
                            contentCache.put(newIdx, HitokotoProvider.getRandomHitokoto(MainActivity.this));
                            colorCache.put(newIdx, getRandomMonetColor());
                        }
                        updateWindowData();
                        android.util.Log.d("CARD", "Adapter notifyDataSetChanged, total cards=" + windowIndices.size());
                        adapter.notifyDataSetChanged();
                        lastCenter = center;
                    });
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                android.util.Log.d("CARD", "onScrollStateChanged: newState=" + newState);
            }
        });
        ImageView btnAction1 = findViewById(R.id.btnAction1);
        ImageView btnAction2 = findViewById(R.id.btnAction2);
        ImageView btnAction3 = findViewById(R.id.btnAction3);
        btnAction1.setOnClickListener(v -> {
            // 打开全屏文件Fragment
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new FavFragment())
                .addToBackStack(null)
                .commit();
        });
        btnAction2.setOnClickListener(v -> {
            // 收藏当前中心卡片
            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();
            int center = first + (last - first) / 2;
            if (center >= 0 && center < windowIndices.size()) {
                int globalIdx = windowIndices.get(center);
                org.json.JSONObject obj = contentCache.get(globalIdx);
                if (obj != null) {
                    boolean added = FavoriteManager.addFavorite(this, obj);
                    if (added) {
                        Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnAction3.setOnClickListener(v -> {
            // 显示设置Fragment
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit();
        });

        // 监听返回键，关闭Fragment时隐藏容器
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
            }
        });
//         动态设置标题字体大小，宽度的7%作为sp（可根据实际需求调整）
        android.widget.TextView titleTextView = findViewById(R.id.titleTextView);
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        float titleSp = Math.max(22, Math.min(48, screenWidth * 0.17f / scaledDensity));
        titleTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, titleSp);
    }

    // 更新adapter的数据
    private void updateWindowData() {
        android.util.Log.d("CARD", "updateWindowData: windowIndices=" + windowIndices.toString());
        List<org.json.JSONObject> windowData = new ArrayList<>();
        List<Integer> windowColors = new ArrayList<>();
        for (int idx : windowIndices) {
            windowData.add(contentCache.get(idx));
            windowColors.add(colorCache.get(idx));
        }
        adapter.setWindowData(windowData, windowColors, windowIndices.getFirst());
    }


    // 设置页切换彩色卡片后刷新卡片背景
    public void refreshCards() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // 字体切换后刷新~~全局~~
    public void refreshFont() {
        // 刷新主界面卡片字体
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
//        // 刷新设置页字体
//        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        if (f != null && f.getView() != null) {
//            applyFontToViewTree(f.getView());
//        }
//        // 刷新主界面其它控件字体
//        View root = findViewById(android.R.id.content);
//        if (root != null) {
//            applyFontToViewTree(root);
//        }
    }


    private void applyFontToViewTree(View view) {
        int fontType = SharedPreferenceManager.getFontType(this);
        android.graphics.Typeface tf = null;
        if (fontType == 1) tf = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.hrd);
        else if (fontType == 2) tf = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.hsmx);
        else if (fontType == 3) tf = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.sst);
        if (view instanceof android.widget.TextView && tf != null) {
            ((android.widget.TextView) view).setTypeface(tf);
        }
        if (view instanceof android.view.ViewGroup) {
            for (int i = 0; i < ((android.view.ViewGroup) view).getChildCount(); i++) {
                applyFontToViewTree(((android.view.ViewGroup) view).getChildAt(i));
            }
        }
    }

    // 莫奈色生成接口，支持深浅色和配色风格啊啊啊啊啊啊我爱Monet
    public int getRandomMonetColor() {
        int[] paletteMonet = {0xFFE5F1DF, 0xFFE7DFF1, 0xFFF1DFE0, 0xFFDFF1ED, 0xFFE1DFF1, 0xFFF0DFF1};
        int[] paletteVivid = {0xFFF44336, 0xFFE91E63, 0xFFFFEB3B, 0xFF00E676, 0xFF00B0FF, 0xFFFF5722};
        int colorStyle = SharedPreferenceManager.getColorStyle(this);
        int[] palette = (colorStyle == 1) ? paletteVivid : paletteMonet;
        int color = palette[(int)(Math.random() * palette.length)];
        // 判断模式
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            // 降低亮度
            float[] hsv = new float[3];
            android.graphics.Color.colorToHSV(color, hsv);
            hsv[2] = hsv[2] * 0.3f; // 降低亮度
            color = android.graphics.Color.HSVToColor(android.graphics.Color.alpha(color), hsv);
        }
        return color;
    }
    // 切换配色风格后，所有卡片颜色立即生效
    public void refreshAllCardColors() {
        int colorStyle = SharedPreferenceManager.getColorStyle(this);
        if (colorStyle == 2) { // 无色模式
            int themeMode = SharedPreferenceManager.getThemeMode(this);
            int color;
            if (themeMode == android.app.UiModeManager.MODE_NIGHT_YES) {
                color = android.graphics.Color.parseColor("#222222"); // 深灰
            } else {
                color = android.graphics.Color.parseColor("#FFFFFF"); // 白色
            }
            for (Integer key : colorCache.keySet()) {
                colorCache.put(key, color);
            }
        } else {
            for (Integer key : colorCache.keySet()) {
                colorCache.put(key, getRandomMonetColor());
            }
        }
        updateWindowData();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
