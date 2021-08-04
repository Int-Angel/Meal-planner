package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.mealplanner.FilteringViewModel;
import com.example.mealplanner.MainActivity;
import com.example.mealplanner.R;
import com.example.mealplanner.models.IRecipe;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

/**
 * Parent fragment for SavedRecipesFragment and OnlineRecipesFragment, this fragments switches
 * from saved to online recipes and updates them based on the filters
 */
public class RecipeFragment extends Fragment implements OnlineRecipesFragment.OnlineRecipesFragmentListener,
        SavedRecipesFragment.SavedRecipesFragmentListener, RecipeDetailsFragment.RecipeDetailsFragmentListener,
        CreateRecipeFragment.CreateRecipeFragmentListener {

    /**
     * saved recipes fragment and online recipes fragment
     */
    public enum FragmentSelection {
        SAVED_RECIPES, ONLINE_RECIPES
    }

    private final static String TAG = "RecipeFragment";
    private FilteringViewModel filteringViewModel;

    // Fragments
    private final SavedRecipesFragment savedRecipesFragment = new SavedRecipesFragment();
    private final OnlineRecipesFragment onlineRecipesFragment = new OnlineRecipesFragment();
    private final FiltersFragment filtersFragment = new FiltersFragment();
    private RecipeDetailsFragment recipeDetailsFragment;
    private CreateRecipeFragment createRecipeFragment;

    private FragmentSelection activeFragment;
    private FragmentSelection lastActiveFragment;

    private RadioButton rbSavedRecipes;
    private RadioButton rbOnlineRecipes;
    private LinearLayout fragmentHeaderContainer;
    private ImageButton ibtnFilter;

    private SearchView searchView;


    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filteringViewModel = new ViewModelProvider(requireActivity()).get(FilteringViewModel.class);

        rbSavedRecipes = view.findViewById(R.id.rbSavedRecipes);
        rbOnlineRecipes = view.findViewById(R.id.rbOnlineRecipes);
        fragmentHeaderContainer = view.findViewById(R.id.fragmentHeaderContainer);
        searchView = view.findViewById(R.id.search_recipes);
        ibtnFilter = view.findViewById(R.id.ibtnFilter);

        activeFragment = FragmentSelection.SAVED_RECIPES;

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, savedRecipesFragment).commit();

        setUpOnClickListeners();
    }

    /**
     * Sets up all the onClickListeners
     */
    private void setUpOnClickListeners() {
        rbSavedRecipes.setOnClickListener(this::onRadioButtonClicked);
        rbOnlineRecipes.setOnClickListener(this::onRadioButtonClicked);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateQuery(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateQuery("");
                return false;
            }
        });

        ibtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilters();
            }
        });
    }

    /**
     * Saves the search bar query into the ViewModel
     *
     * @param query
     */
    private void updateQuery(String query) {
        filteringViewModel.setQueryName(query);
    }

    /**
     * Gets called when the user wants to change from saved recipes to online recipes or the other
     * way
     *
     * @param view
     */
    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbSavedRecipes:
                if (checked)
                    changeFragment(FragmentSelection.SAVED_RECIPES);
                break;
            case R.id.rbOnlineRecipes:
                if (checked)
                    changeFragment(FragmentSelection.ONLINE_RECIPES);
                break;
        }
    }

    /**
     * Changes the fragment based on the fragment selection
     *
     * @param fragmentSelection could be saved recipes or online recipes
     */
    private void changeFragment(FragmentSelection fragmentSelection) {
        lastActiveFragment = activeFragment;
        activeFragment = fragmentSelection;
        fragmentHeaderContainer.setVisibility(View.VISIBLE);
        Fragment fragment;

        if (fragmentSelection == FragmentSelection.SAVED_RECIPES)
            fragment = savedRecipesFragment;
        else
            fragment = onlineRecipesFragment;

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    /**
     * Opens the details of a clicked recipe
     *
     * @param recipe
     * @param index
     */
    private void openRecipeDetailsFragment(IRecipe recipe, int index) {
        lastActiveFragment = activeFragment;
        fragmentHeaderContainer.setVisibility(View.GONE);
        recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipe, ParseUser.getCurrentUser());

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, recipeDetailsFragment)
                .commit();
    }

    /**
     * Opens the filters
     */
    private void openFilters() {
        if (filtersFragment.isAdded()) {
            getParentFragmentManager()
                    .beginTransaction()
                    .show(filtersFragment)
                    .commit();
        } else {
            getParentFragmentManager()
                    .beginTransaction()
                    .add(R.id.flContainer, filtersFragment)
                    .commit();
        }
    }

    /**
     * Closes the recipe details
     */
    private void closeDetailsFragment() {
        changeFragment(lastActiveFragment);
        if (activeFragment == FragmentSelection.ONLINE_RECIPES) {
            onlineRecipesFragment.updateRecipeList();
        } else {
            savedRecipesFragment.updateRecipeList();
        }
    }

    @Override
    public void openOnlineRecipeDetailsListener(IRecipe recipe, int index) {
        openRecipeDetailsFragment(recipe, index);
    }

    @Override
    public void backButtonPressed() {
        Log.i(TAG, "BACK");

        closeDetailsFragment();
    }

    @Override
    public void updateRecipeList() {
        if (activeFragment == FragmentSelection.ONLINE_RECIPES) {
            onlineRecipesFragment.updateRecipeList();
        } else {
            savedRecipesFragment.updateRecipeList();
        }
    }

    @Override
    public void openSavedRecipeDetailsFragment(IRecipe recipe, int index) {
        openRecipeDetailsFragment(recipe, index);
    }

    @Override
    public void openCreateNewFragment() {
        createRecipeFragment = CreateRecipeFragment.newInstance();
        fragmentHeaderContainer.setVisibility(View.GONE);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, createRecipeFragment)
                .commit();
    }

    @Override
    public void newRecipeCreated() {
        savedRecipesFragment.updateRecipeList();
    }

    @Override
    public void closeCreateNewRecipe() {
        fragmentHeaderContainer.setVisibility(View.VISIBLE);
        getChildFragmentManager()
                .beginTransaction()
                .remove(createRecipeFragment)
                .commit();
    }
}