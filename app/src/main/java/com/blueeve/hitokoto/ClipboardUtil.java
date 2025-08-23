package com.blueeve.hitokoto;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class ClipboardUtil {
    public static void copy(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("hitokoto", text));
            Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
        }
    }
}

