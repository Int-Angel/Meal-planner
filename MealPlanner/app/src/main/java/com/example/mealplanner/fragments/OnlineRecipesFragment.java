package com.example.mealplanner.fragments;

import android.content.Context;
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
import android.widget.ProgressBar;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.adapters.RecipeAdapter;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.OnlineRecipe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;


public class OnlineRecipesFragment extends Fragment {

    public interface OnlineRecipesFragmentListener {
        void openOnlineRecipeDetailsListener(IRecipe recipe, int index);
    }

    private final static String TAG = "OnlineRecipes";
    public static final String BASE_RECIPES_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=728721c3da7543769d5413b35ac70cd7&addRecipeInformation=true&addRecipeNutrition=true&instructionsRequired=true&fillIngredients=true";

    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private List<IRecipe> onlineRecipes;
    private Set<String> savedRecipesUri;
    private RecipeAdapter adapter;
    private AsyncHttpClient client;

    private OnlineRecipesFragmentListener listener;

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
        adapter = new RecipeAdapter(getContext(), onlineRecipes, new RecipeAdapter.RecipeAdapterListener() {
            @Override
            public void openDetails(IRecipe recipe, int index) {
                listener.openOnlineRecipeDetailsListener(recipe, index);
            }
        });

        progressBar = view.findViewById(R.id.progress_circular);

        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        savedRecipesUri = new HashSet<>();
        savedRecipesUri = SavedRecipesManager.getIdSet();
        getRecipes();
    }

    private void getRecipes() {
        client.get(BASE_RECIPES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");

                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    onlineRecipes.addAll(OnlineRecipe.fromJsonArray(results));
                    checkIfRecipesAreAlreadySaved();
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

    private void checkIfRecipesAreAlreadySaved() {
        for (String s : savedRecipesUri) {
            Log.i("Check", s);
        }
        for (int i = 0; i < onlineRecipes.size(); i++) {
            String uri = onlineRecipes.get(i).getId();
            ((OnlineRecipe) onlineRecipes.get(i)).setSaved(savedRecipesUri.contains(uri));
        }
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnlineRecipesFragmentListener) {
            listener = (OnlineRecipesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Fragment new task Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}