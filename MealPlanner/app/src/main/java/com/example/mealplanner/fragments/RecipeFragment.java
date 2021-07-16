package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.mealplanner.MainActivity;
import com.example.mealplanner.R;
import com.example.mealplanner.models.IRecipe;

import org.jetbrains.annotations.NotNull;


public class RecipeFragment extends Fragment implements OnlineRecipesFragment.OnlineRecipesFragmentListener,
        SavedRecipesFragment.SavedRecipesFragmentListener, RecipeDetailsFragment.RecipeDetailsFragmentListener{

    private final static String TAG = "RecipeFragment";

    private final SavedRecipesFragment savedRecipesFragment = new SavedRecipesFragment();
    private final OnlineRecipesFragment onlineRecipesFragment = new OnlineRecipesFragment();
    private RecipeDetailsFragment recipeDetailsFragment;

    private MainActivity.FragmentSelection activeFragment;
    private MainActivity.FragmentSelection lastActiveFragment;

    private RadioButton rbSavedRecipes;
    private RadioButton rbOnlineRecipes;
    private LinearLayout fragmentHeaderContainer;


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

        rbSavedRecipes = view.findViewById(R.id.rbSavedRecipes);
        rbOnlineRecipes = view.findViewById(R.id.rbOnlineRecipes);
        fragmentHeaderContainer = view.findViewById(R.id.fragmentHeaderContainer);

        activeFragment = MainActivity.FragmentSelection.SAVED_RECIPES;

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, savedRecipesFragment).commit();

        setUpOnClickListeners();
    }

    private void setUpOnClickListeners(){
        rbSavedRecipes.setOnClickListener(this::onRadioButtonClicked);
        rbOnlineRecipes.setOnClickListener(this::onRadioButtonClicked);
    }

    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbSavedRecipes:
                if (checked)
                    changeFragment(MainActivity.FragmentSelection.SAVED_RECIPES);
                break;
            case R.id.rbOnlineRecipes:
                if (checked)
                    changeFragment(MainActivity.FragmentSelection.ONLINE_RECIPES);
                break;
        }
    }

    private void changeFragment(MainActivity.FragmentSelection fragmentSelection) {
        lastActiveFragment = activeFragment;
        activeFragment = fragmentSelection;
        fragmentHeaderContainer.setVisibility(View.VISIBLE);
        Fragment fragment;

        if(fragmentSelection == MainActivity.FragmentSelection.SAVED_RECIPES)
            fragment = savedRecipesFragment;
        else
            fragment = onlineRecipesFragment;

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    private void openRecipeDetailsFragment(IRecipe recipe, int index) {
        lastActiveFragment = activeFragment;
        fragmentHeaderContainer.setVisibility(View.GONE);
        recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipe, index);

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, recipeDetailsFragment).commit();
    }

    private void closeDetailsFragment(){
        changeFragment(lastActiveFragment);
    }

    @Override
    public void openOnlineRecipeDetailsListener(IRecipe recipe, int index) {
        openRecipeDetailsFragment(recipe, index);
    }

    @Override
    public void backButtonPressed() {
        Log.i(TAG, "BACK");
        //onBackPressed();
        closeDetailsFragment();
    }

    @Override
    public void updateRecipeList() {
        savedRecipesFragment.updateRecipeList();
    }

    @Override
    public void openSavedRecipeDetailsFragment(IRecipe recipe, int index) {
        openRecipeDetailsFragment(recipe, index);
    }
}