package com.blueeve.hitokoto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import org.json.JSONObject;

public class FavFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        // 获取状态栏高度并设置给标题栏
        com.google.android.material.appbar.MaterialToolbar toolbar = view.findViewById(R.id.fav_toolbar);
        int statusBarHeight = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resId);
        }
        toolbar.setPadding(toolbar.getPaddingLeft(), statusBarHeight, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
        RecyclerView recyclerView = view.findViewById(R.id.favoriteRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<JSONObject> favorites = FavoriteManager.getAllFavorites(requireContext());
        FavoriteAdapter adapter = new FavoriteAdapter(requireContext(), favorites);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
