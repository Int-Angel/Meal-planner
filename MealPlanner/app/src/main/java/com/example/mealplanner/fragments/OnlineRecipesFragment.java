package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealplanner.R;
import com.example.mealplanner.adapters.OnlineRecipeAdapter;
import com.example.mealplanner.models.OnlineRecipe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class OnlineRecipesFragment extends Fragment {

    private final static String TAG = "OnlineRecipes";
    public static final String CHICKEN_RECIPES_URL = "https://api.edamam.com/api/recipes/v2?type=public&q=chiken&app_id=74ae975a&app_key=7ab0ed179d7a780e86361ffc79d73528";

    private RecyclerView rvRecipes;
    private List<OnlineRecipe> onlineRecipes;
    private OnlineRecipeAdapter adapter;
    private AsyncHttpClient client;

    public OnlineRecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_online_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onlineRecipes = new ArrayList<>();
        client = new AsyncHttpClient();
        adapter = new OnlineRecipeAdapter(getContext(), onlineRecipes);

        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        getRecipes();
    }

    private void getRecipes() {
        client.get(CHICKEN_RECIPES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");

                try {
                    JSONArray results = json.jsonObject.getJSONArray("hits");
                    onlineRecipes.addAll(OnlineRecipe.fromJsonArray(results));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Issue creating recipes", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }
}