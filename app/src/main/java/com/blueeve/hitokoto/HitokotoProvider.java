package com.blueeve.hitokoto;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HitokotoProvider {
    private static final String[] FILE_NAMES = {
            "a.json", "b.json", "c.json", "d.json", "e.json", "f.json", "g.json", "h.json", "i.json", "j.json", "k.json", "l.json"
    };
    private static final String JSON_DIR = "json";
    private static List<JSONObject> hitokotoList = null;
    private static final Random random = new Random();

    // 初始化并加载所有句子
    private static void ensureLoaded(Context context) {
        if (hitokotoList != null) return;
        hitokotoList = new ArrayList<>();
        try {
            AssetManager am = context.getAssets();
            for (String fileName : FILE_NAMES) {
                InputStream is = am.open(JSON_DIR + "/" + fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                is.close();
                JSONArray arr = new JSONArray(sb.toString());
                for (int i = 0; i < arr.length(); i++) {
                    hitokotoList.add(arr.getJSONObject(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 随机获取一句完整的 hitokoto
    public static JSONObject getRandomHitokoto(Context context) {
        ensureLoaded(context);
        if (hitokotoList == null || hitokotoList.isEmpty()) return null;
        int idx = random.nextInt(hitokotoList.size());
        return hitokotoList.get(idx);
    }
}
