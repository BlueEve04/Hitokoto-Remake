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
        RecyclerView recyclerView = view.findViewById(R.id.favoriteRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<JSONObject> favorites = FavoriteManager.getAllFavorites(requireContext());
        FavoriteAdapter adapter = new FavoriteAdapter(requireContext(), favorites);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
