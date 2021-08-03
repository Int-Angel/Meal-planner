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

import com.airbnb.lottie.LottieAnimationView;
import com.example.mealplanner.FilteringViewModel;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.adapters.RecipeAdapter;
import com.example.mealplanner.models.FilterCheckBox;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows all the saved recipes using the SavedRecipesManager
 */
public class SavedRecipesFragment extends Fragment {

    private final static String TAG = "SavedRecipes";

    public interface SavedRecipesFragmentListener {
        void openSavedRecipeDetailsFragment(IRecipe recipe, int index);
        void openCreateNewFragment();
    }

    private FilteringViewModel filteringViewModel;
    private String queryName;

    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private TextView tvNoSavedRecipes;
    private List<IRecipe> recipes;
    private RecipeAdapter adapter;
    private FloatingActionButton fab;
    private LottieAnimationView animation_progress;
    private LottieAnimationView animationView;

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
        fab = view.findViewById(R.id.fab);
        animation_progress = view.findViewById(R.id.animation_progress);
        animationView = view.findViewById(R.id.animationView);

        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        setUpFAB();
        updateRecipeList();
        searchListener();
    }

    /**
     * sets up an onClickListener to the fab
     */
    private void setUpFAB(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateNewRecipe();
            }
        });
    }

    /**
     * opens the fragment to create a new recipe
     */
    private void openCreateNewRecipe(){
        listener.openCreateNewFragment();
    }

    /**
     * Listens if the users search something in the search bar
     */
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

    /**
     * Updates the saved recipes list
     */
    public void updateRecipeList() {
        recipes.clear();
        recipes.addAll(SavedRecipesManager.getRecipes());
        progressBar.setVisibility(View.GONE);
        animation_progress.setVisibility(View.GONE);

        if (recipes.size() == 0) {
            tvNoSavedRecipes.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.VISIBLE);
        } else {
            tvNoSavedRecipes.setVisibility(View.GONE);
            animationView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Gets the saved recipes based in the filters and search bar
     */
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
                animation_progress.setVisibility(View.GONE);

                if (recipes.size() == 0) {
                    tvNoSavedRecipes.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                } else {
                    tvNoSavedRecipes.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);
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