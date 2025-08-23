package com.blueeve.hitokoto;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public class DialogueManager {

    public interface OnButtonClickListener {
        void onClick(int which);
    }

    /**
     * 显示自定义弹窗
     * @param context 上下文
     * @param title 标题
     * @param message 内容
     * @param buttonCount 按钮个数（1~3）
     * @param buttonTexts 按钮文本数组（长度需与buttonCount一致）
     * @param listener 按钮点击回调，参数为按钮下标（0/1/2）
     * @param background 可选自定义背景（可为null）
     */
    public static void showDialog(Context context, String title, String message, int buttonCount, String[] buttonTexts, OnButtonClickListener listener, Drawable background) {
        if (buttonCount < 1 || buttonCount > 3 || buttonTexts == null || buttonTexts.length != buttonCount) {
            throw new IllegalArgumentException("按钮数量与文本不匹配，且按钮数量仅支持1~3");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        if (buttonCount >= 1) {
            builder.setPositiveButton(buttonTexts[0], (dialog, which) -> {
                if (listener != null) listener.onClick(0);
            });
        }
        if (buttonCount >= 2) {
            builder.setNegativeButton(buttonTexts[1], (dialog, which) -> {
                if (listener != null) listener.onClick(1);
            });
        }
        if (buttonCount == 3) {
            builder.setNeutralButton(buttonTexts[2], (dialog, which) -> {
                if (listener != null) listener.onClick(2);
            });
        }

        AlertDialog dialog = builder.create();
        if (background != null) {
            dialog.setOnShowListener(d -> {
                dialog.getWindow().setBackgroundDrawable(background);
            });
        }
        dialog.show();
    }

    /**
     * 显示自定义布局弹窗
     * @param context 上下文
     * @param title 标题
     * @param customView 自定义View
     * @param buttonCount 按钮个数（1~3）
     * @param buttonTexts 按钮文本数组（长度需与buttonCount一致）
     * @param listener 按钮点击回调，参数为按钮下标（0/1/2）
     * @param background 可选自定义背景（可为null）
     */
    public static void showCustomDialog(Context context, String title, View customView, int buttonCount, String[] buttonTexts, OnButtonClickListener listener, Drawable background) {
        if (buttonCount < 1 || buttonCount > 3 || buttonTexts == null || buttonTexts.length != buttonCount) {
            throw new IllegalArgumentException("按钮数量与文本不匹配，且按钮数量仅支持1~3");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(customView);

        if (buttonCount >= 1) {
            builder.setPositiveButton(buttonTexts[0], (dialog, which) -> {
                if (listener != null) listener.onClick(0);
            });
        }
        if (buttonCount >= 2) {
            builder.setNegativeButton(buttonTexts[1], (dialog, which) -> {
                if (listener != null) listener.onClick(1);
            });
        }
        if (buttonCount == 3) {
            builder.setNeutralButton(buttonTexts[2], (dialog, which) -> {
                if (listener != null) listener.onClick(2);
            });
        }

        AlertDialog dialog = builder.create();
        if (background != null) {
            dialog.setOnShowListener(d -> {
                dialog.getWindow().setBackgroundDrawable(background);
            });
        }
        dialog.show();
    }

}
