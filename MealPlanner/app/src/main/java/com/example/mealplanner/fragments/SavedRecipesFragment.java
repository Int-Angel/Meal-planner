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
import android.widget.TextView;

import com.example.mealplanner.FilteringViewModel;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.adapters.RecipeAdapter;
import com.example.mealplanner.models.FilterCheckBox;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SavedRecipesFragment extends Fragment {

    private final static String TAG = "SavedRecipes";

    public interface SavedRecipesFragmentListener {
        void openSavedRecipeDetailsFragment(IRecipe recipe, int index);
    }

    private FilteringViewModel filteringViewModel;
    private String queryName;

    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private TextView tvNoSavedRecipes;
    private List<IRecipe> recipes;
    private RecipeAdapter adapter;

    private SavedRecipesFragmentListener listener;

    public SavedRecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryName = "";

        filteringViewModel = new ViewModelProvider(requireActivity()).get(FilteringViewModel.class);

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes, new RecipeAdapter.RecipeAdapterListener() {
            @Override
            public void openDetails(IRecipe recipe, int index) {
                listener.openSavedRecipeDetailsFragment(recipe, index);
            }
        });

        listener = (SavedRecipesFragmentListener) getParentFragment();

        tvNoSavedRecipes = view.findViewById(R.id.tvNoSavedRecipes);
        progressBar = view.findViewById(R.id.progress_circular);

        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        updateRecipeList();
        searchListener();
    }

    private void searchListener() {
        filteringViewModel.getQueryName().observe(getViewLifecycleOwner(), query -> {
            queryName = query;
            getCustomRecipes();
            //getRecipes(query);
        });
        filteringViewModel.getActiveCuisines().observe(getViewLifecycleOwner(), observer -> {
            getCustomRecipes();
        });
    }

    public void updateRecipeList() {
        recipes.clear();
        recipes.addAll(SavedRecipesManager.getRecipes());
        progressBar.setVisibility(View.GONE);

        if (recipes.size() == 0) {
            tvNoSavedRecipes.setVisibility(View.VISIBLE);
        } else {
            tvNoSavedRecipes.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    private void getCustomRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.whereEqualTo(Recipe.KEY_USER, ParseUser.getCurrentUser());

        if (!queryName.equals("") && !queryName.equals(" ") && !queryName.equals("null"))
            query.whereFullText(Recipe.KEY_TITLE, queryName);

        if (filteringViewModel.getActiveCuisines().getValue()) {
            query.whereContainedIn(Recipe.KEY_CUISINE_TYPE, filteringViewModel.getCuisines().getValue());
        }

        if (filteringViewModel.getActiveMealTypes().getValue()) {
            query.whereEqualTo(Recipe.KEY_DISH_TYPE, filteringViewModel.getMealTypes().getValue());
        }

        if (filteringViewModel.getActiveMaxTimeReady().getValue()) {
            query.whereLessThanOrEqualTo(Recipe.KEY_TOTAL_TIME, filteringViewModel.getMaxTimeReady().getValue());
        }

        if (filteringViewModel.getActiveCalories().getValue()) {
            query.whereGreaterThan(Recipe.KEY_CALORIES, filteringViewModel.getMinCalories());
            query.whereLessThanOrEqualTo(Recipe.KEY_CALORIES, filteringViewModel.getMaxCalories());
        }

        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't complete the custom search", e);
                    return;
                }
                recipes.clear();
                recipes.addAll(objects);

                progressBar.setVisibility(View.GONE);

                if (recipes.size() == 0) {
                    tvNoSavedRecipes.setVisibility(View.VISIBLE);
                } else {
                    tvNoSavedRecipes.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        Log.i(TAG, "onDetach");
    }
}