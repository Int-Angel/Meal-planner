package com.example.mealplanner.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealplanner.FilteringViewModel;
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

/**
 * This fragment shows all the online recipes and it's affected by the filtersFragment
 */
public class OnlineRecipesFragment extends Fragment {

    public interface OnlineRecipesFragmentListener {
        void openOnlineRecipeDetailsListener(IRecipe recipe, int index);
    }

    private final static String TAG = "OnlineRecipes";
    private static final String BASE_RECIPES_URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=728721c3da7543769d5413b35ac70cd7&addRecipeInformation=true&addRecipeNutrition=true&instructionsRequired=true&fillIngredients=true";
    private String searchUrl = BASE_RECIPES_URL;

    private FilteringViewModel filteringViewModel;
    private String query;

    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private LottieAnimationView animation_progress;
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
        query = "";

        filteringViewModel = new ViewModelProvider(requireActivity()).get(FilteringViewModel.class);

        onlineRecipes = new ArrayList<>();
        client = new AsyncHttpClient();
        adapter = new RecipeAdapter(getContext(), onlineRecipes, new RecipeAdapter.RecipeAdapterListener() {
            @Override
            public void openDetails(IRecipe recipe, int index) {
                listener.openOnlineRecipeDetailsListener(recipe, index);
            }
        });

        listener = (OnlineRecipesFragmentListener) getParentFragment();

        progressBar = view.findViewById(R.id.progress_circular);
        animation_progress = view.findViewById(R.id.animation_progress);

        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        savedRecipesUri = new HashSet<>();
        savedRecipesUri = SavedRecipesManager.getIdSet();
        getRecipes(buildSearchUrl());
        searchListener();
    }

    /**
     * updates the list if the users search something in the search bar
     */
    private void searchListener() {
        filteringViewModel.getQueryName().observe(getViewLifecycleOwner(), query -> {
            this.query = query;
            getRecipes(buildSearchUrl());
        });
        filteringViewModel.getActiveCuisines().observe(getViewLifecycleOwner(), observer -> {
            getRecipes(buildSearchUrl());
        });
    }

    /**
     * Generates a search url based on the active filters
     *
     * @return
     */
    private String buildSearchUrl() {
        searchUrl = BASE_RECIPES_URL;
        if (!query.equals("") && !query.equals(" ") && !query.equals("null")) {
            searchUrl += "&query=" + query;
        }
        if (filteringViewModel.getActiveCuisines().getValue()) {
            String cuisines = "";
            for (String cuisine : filteringViewModel.getCuisines().getValue()) {
                cuisines += cuisine + ",";
            }
            searchUrl += "&cuisine=" + cuisines;
        }
        if (filteringViewModel.getActiveMealTypes().getValue()) {
            searchUrl += "&type=" + filteringViewModel.getMealTypes().getValue();
        }
        if (filteringViewModel.getActiveMaxTimeReady().getValue()) {
            searchUrl += "&maxReadyTime=" + filteringViewModel.getMaxTimeReady().getValue();
        }
        if (filteringViewModel.getActiveCalories().getValue()) {
            searchUrl += "&minCalories=" + filteringViewModel.getMinCalories().getValue();
            searchUrl += "&maxCalories=" + filteringViewModel.getMaxCalories().getValue();
        }
        return searchUrl;
    }

    /**
     * gets a list of online recipes from the API using a search url
     *
     * @param url
     */
    private void getRecipes(String url) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");

                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    onlineRecipes.clear();
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

    /**
     * Checks if the recipes from the list are already saved and changes it's icon
     */
    private void checkIfRecipesAreAlreadySaved() {
        for (String s : savedRecipesUri) {
            Log.i("Check", s);
        }
        for (int i = 0; i < onlineRecipes.size(); i++) {
            String uri = onlineRecipes.get(i).getId();
            ((OnlineRecipe) onlineRecipes.get(i)).setSaved(savedRecipesUri.contains(uri));
        }
        progressBar.setVisibility(View.GONE);
        animation_progress.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    /**
     * Updates the list of recipes
     */
    public void updateRecipeList(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}