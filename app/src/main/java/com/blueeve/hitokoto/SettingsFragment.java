package com.blueeve.hitokoto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private static final int REQ_CODE_EXPORT_SYSTEM_DOWNLOAD = 2001;
    private String pendingExportText = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.settings_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<SettingsItem> items = new ArrayList<>();
        // 普通项
        SettingsItem normalItem = new SettingsItem(R.drawable.ic_about ,"关于", "查看应用信息", SettingsItem.Type.NORMAL);
        normalItem.rightIconRes = R.drawable.ic_arrow_right;
        normalItem.onClickListener = () -> {
            DialogueManager.showDialog(
                    requireContext(),
                    "关于Hitokoto",
                    "Hitokoto是一个简洁的一言本地查看/小部件创建美化的软件\n" +
                            "\n" +
                            "版本: "+ "1.02-pre" +"\n" +
                            "\n\n" +
                            "Hitokoto基于一言-Bundle，感谢一言开源的文本包支持\n" +
                            "\n文字具有震撼人心的力量，愿你我皆被这世间温柔以待\n本软件依照一言开源协议" +",使用AGPL协议开源\n" +
                            "作为一个Android新手，本软件难免存在一些问题，若有任何建议或意见，欢迎前往GitHub或通过邮箱与我联系\n" +
                            "Github：https://github.com/BlueEve04" ,
                    3, // 这里应为3，和下方按钮数量一致
                    new String[]{"确定", "关于作者", "捐赠我"}, // 长度必须为3
                    which -> {
                        if (which == 0) {
                            Toast.makeText(requireContext(), "求捐赠QwQ", Toast.LENGTH_SHORT).show();
                        } else if (which == 2) {
                            // 点击“捐赠我”弹出带图片的弹窗
                            View customView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_donate, null, false);
                            DialogueManager.showCustomDialog(
                                    requireContext(),
                                    "捐赠支持",
                                    customView,
                                    1,
                                    new String[]{"关闭"},
                                    w -> {
                                    },
                                    null
                            );
                        } else if (which == 1) {
                            View costomView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_about_author, null, false);
                            DialogueManager.showCustomDialog(
                                    requireContext(),
                                    "关于作者",
                                    costomView,
                                    1,
                                    new String[]{"关闭"},
                                    w -> {
                                    },
                                    null
                            );
                        }
                    },
                    null
            );
        };
        items.add(normalItem);
//        // 开关项
//        SettingsItem switchItem = new SettingsItem(R.drawable.ic_notification, "通知提醒", "开启后可接收推送", SettingsItem.Type.SWITCH);
//        switchItem.switchChecked = true;
//        switchItem.onSwitchChangedListener = checked -> {/* TODO: 保存开关状态 */};
//        items.add(switchItem);
        // 主题色下拉项
        int themeMode = SharedPreferenceManager.getThemeMode(requireContext());
        int themeSelected = 0;
        if (themeMode == android.app.UiModeManager.MODE_NIGHT_NO) themeSelected = 1;
        else if (themeMode == android.app.UiModeManager.MODE_NIGHT_YES) themeSelected = 2;
        SettingsItem dropdownItem = new SettingsItem(R.drawable.ic_day_night, "主题色", "选择应用主题色", SettingsItem.Type.DROPDOWN);
        dropdownItem.dropdownOptions = new String[]{"系统默认", "浅色", "深色"};
        dropdownItem.dropdownSelected = themeSelected;
        dropdownItem.onDropdownChangedListener = idx -> {
            int mode;
            if (idx == 1) mode = android.app.UiModeManager.MODE_NIGHT_NO;
            else if (idx == 2) mode = android.app.UiModeManager.MODE_NIGHT_YES;
            else mode = -1;
            SharedPreferenceManager.setThemeMode(requireContext(), mode);
            // 动态切换主题
            if (mode == -1) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (mode == android.app.UiModeManager.MODE_NIGHT_NO) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
            } else if (mode == android.app.UiModeManager.MODE_NIGHT_YES) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
            }
            // 通知主界面刷新卡片
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshCards();
            }
        };
        items.add(dropdownItem);
        // 彩色卡片开关项
        SettingsItem colorfulCardSwitch = new SettingsItem(R.drawable.ic_eye_open, "彩色卡片", "开启后卡片将使用多彩背景", SettingsItem.Type.SWITCH);
        colorfulCardSwitch.switchChecked = SharedPreferenceManager.isColorfulCardEnabled(requireContext());
        colorfulCardSwitch.onSwitchChangedListener = checked -> {
            SharedPreferenceManager.setColorfulCardEnabled(requireContext(), checked);
            // 通知��面刷新卡片
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshCards();
            }
        };
        items.add(colorfulCardSwitch);
        // 字体切换下拉项
        int fontType = SharedPreferenceManager.getFontType(requireContext());
        if (fontType < 0 || fontType > 3) fontType = 1; // 默认黄令东齐伋体
        SettingsItem fontDropdown = new SettingsItem(R.drawable.ic_font, "字体", "切换应用字体", SettingsItem.Type.DROPDOWN);
        fontDropdown.dropdownOptions = new String[]{"系统字体", "黄令东齐伋体", "衡山毛笔行书", "手书体"};
        fontDropdown.dropdownSelected = fontType;
        fontDropdown.onDropdownChangedListener = idx -> {
            SharedPreferenceManager.setFontType(requireContext(), idx);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshFont();
            }
        };
        items.add(fontDropdown);
        // 字体大小下拉项
        int fontSize = SharedPreferenceManager.getFontSize(requireContext());
        int fontSizeSelected = 1; // 默认中等(18sp)
        if (fontSize == 16) fontSizeSelected = 0;
        else if (fontSize == 18) fontSizeSelected = 1;
        else if (fontSize == 24) fontSizeSelected = 2;
        SettingsItem fontSizeDropdown = new SettingsItem(R.drawable.ic_format, "卡片字体大小", "调整卡片文字大小", SettingsItem.Type.DROPDOWN);
        fontSizeDropdown.dropdownOptions = new String[]{"小(16sp)", "中(18sp)", "大(24sp)"};
        fontSizeDropdown.dropdownSelected = fontSizeSelected;
        fontSizeDropdown.onDropdownChangedListener = idx -> {
            int size = 18;
            if (idx == 0) size = 16;
            else if (idx == 1) size = 18;
            else if (idx == 2) size = 24;
            SharedPreferenceManager.setFontSize(requireContext(), size);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshFont();
            }
        };
        items.add(fontSizeDropdown);
        // 配色风格下拉项
        int colorStyle = SharedPreferenceManager.getColorStyle(requireContext());
        SettingsItem colorStyleDropdown = new SettingsItem(R.drawable.ic_comp, "配色风格", "切换卡片配色风格", SettingsItem.Type.DROPDOWN);
        colorStyleDropdown.dropdownOptions = new String[]{"莫奈柔和", "鲜艳"};
        colorStyleDropdown.dropdownSelected = colorStyle;
        colorStyleDropdown.onDropdownChangedListener = idx -> {
            SharedPreferenceManager.setColorStyle(requireContext(), idx);
            // 通知主界面刷新卡片颜色（立即生效）
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshAllCardColors();
            }
        };
        items.add(colorStyleDropdown);
        // 全部导出项
        SettingsItem exportItem = new SettingsItem(R.drawable.ic_export, "全部导出", "导出收藏到下载文件夹", SettingsItem.Type.NORMAL);
        exportItem.onClickListener = () -> {
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("导出收藏")
                .setMessage("请选择导出位置：")
                .setPositiveButton("系统Download目录", (dialog, which) -> exportToSystemDownload())
                .setNegativeButton("应用目录", (dialog, which) -> exportToAppDownload())
                .show();
        };
        items.add(exportItem);
        // 小部件设置项
        SettingsItem widgetItem = new SettingsItem(R.drawable.ic_settings, "小部件设置", "自定义桌面小部件样式", SettingsItem.Type.NORMAL);
        widgetItem.rightIconRes = R.drawable.ic_arrow_right;
        widgetItem.onClickListener = () -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), WidgetConfigActivity.class);
            intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        };
        items.add(widgetItem);
        SettingsAdapter adapter = new SettingsAdapter(requireContext(), items);
        recyclerView.setAdapter(adapter);
        return view;
    }

    // 导出到应用私有Download目录
    private void exportToAppDownload() {
        String text = FavoriteManager.exportAllFavoritesAsText(requireContext());
        if (text.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "没有可导出的收藏", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        java.io.File dir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS);
        if (dir == null) {
            android.widget.Toast.makeText(requireContext(), "无法访问下载目录", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        java.io.File file = new java.io.File(dir, "hitokoto_export.txt");
        try (java.io.FileWriter fw = new java.io.FileWriter(file, false)) {
            fw.write(text);
            android.widget.Toast.makeText(requireContext(), "导出成功: " + file.getAbsolutePath(), android.widget.Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "导出失败", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    // 导出到系统Download目录（需权限）
    private void exportToSystemDownload() {
        String text = FavoriteManager.exportAllFavoritesAsText(requireContext());
        if (text.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "没有可导出的收藏", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            // Android 10+ 用 Storage Access Framework
            pendingExportText = text;
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TITLE, "hitokoto_export.txt");
            startActivityForResult(intent, REQ_CODE_EXPORT_SYSTEM_DOWNLOAD);
        } else if (android.os.Build.VERSION.SDK_INT >= 23 && android.content.pm.PackageManager.PERMISSION_GRANTED != androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            doExportToSystemDownloadCompat(text);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_EXPORT_SYSTEM_DOWNLOAD && resultCode == android.app.Activity.RESULT_OK && data != null && data.getData() != null) {
            try (java.io.OutputStream os = requireContext().getContentResolver().openOutputStream(data.getData())) {
                if (pendingExportText != null) {
                    os.write(pendingExportText.getBytes("UTF-8"));
                    android.widget.Toast.makeText(requireContext(), "导出成功", android.widget.Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                android.widget.Toast.makeText(requireContext(), "导出失败", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doExportToSystemDownloadCompat(String text) {
        java.io.File dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
        if (dir == null) {
            android.widget.Toast.makeText(requireContext(), "无法访问系统Download目录", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        java.io.File file = new java.io.File(dir, "hitokoto_export.txt");
        try (java.io.FileWriter fw = new java.io.FileWriter(file, false)) {
            fw.write(text);
            android.widget.Toast.makeText(requireContext(), "导出成功: " + file.getAbsolutePath(), android.widget.Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(), "导出失败", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            exportToSystemDownload();
        } else if (requestCode == 1001) {
            android.widget.Toast.makeText(requireContext(), "未获得存储权限，导出失败", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
