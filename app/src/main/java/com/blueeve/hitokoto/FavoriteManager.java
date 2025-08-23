package com.blueeve.hitokoto;

import android.content.Context;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.*;
import java.util.*;

public class FavoriteManager {
    private static final String FILE_NAME = "favorites.txt";

    // 添加收藏，已存在则不添加
    public static boolean addFavorite(Context context, JSONObject obj) {
        List<JSONObject> all = getAllFavorites(context);
        for (JSONObject o : all) {
            if (o.optString("hitokoto").equals(obj.optString("hitokoto"))) {
                return false; // 已存在
            }
        }
        try (FileWriter fw = new FileWriter(new File(context.getFilesDir(), FILE_NAME), true)) {
            fw.write(obj.toString() + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取所有收藏
    public static List<JSONObject> getAllFavorites(Context context) {
        List<JSONObject> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    list.add(new JSONObject(line));
                } catch (JSONException ignore) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 格式化所有收藏为人类可读文本
    public static String exportAllFavoritesAsText(Context context) {
        List<JSONObject> list = getAllFavorites(context);
        StringBuilder string_bd_worker = new StringBuilder();
        for (JSONObject obj : list) {
            String hitokoto = obj.optString("hitokoto", "");
            String from = obj.optString("from", "").equals("null") ? "未知来源" : obj.optString("from", "");
            String fromWho = obj.optString("from_who", "").equals("null") ? "未知作者" : obj.optString("from_who", "");
            String creator = obj.optString("creator", "").equals("null") ? "未知" : obj.optString("creator", "");
            string_bd_worker.append(hitokoto).append("\n");
            if (!from.isEmpty() || !fromWho.isEmpty()) {
                string_bd_worker.append("—— ");
                if (!from.isEmpty()) string_bd_worker.append(from);
                if (!fromWho.isEmpty()) string_bd_worker.append("  ").append(fromWho);
                string_bd_worker.append("\n");
            }
            if (!creator.isEmpty()) string_bd_worker.append("录入者: ").append(creator).append("\n");
            string_bd_worker.append("\n");
        }
        return string_bd_worker.toString();
    }
}
