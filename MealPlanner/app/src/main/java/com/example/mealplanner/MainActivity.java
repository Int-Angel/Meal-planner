package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.example.mealplanner.fragments.RecipeDetailsFragment;
import com.example.mealplanner.fragments.OnlineRecipesFragment;
import com.example.mealplanner.fragments.ProfileFragment;
import com.example.mealplanner.fragments.SavedRecipesFragment;
import com.example.mealplanner.fragments.ShoppingListFragment;
import com.example.mealplanner.fragments.SocialFragment;
import com.example.mealplanner.fragments.WeekFragment;
import com.example.mealplanner.models.IRecipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements OnlineRecipesFragment.OnlineRecipesFragmentListener, SavedRecipesFragment.SavedRecipesFragmentListener {

    enum FragmentSelection {
        WEEK, SHOPPING_LIST, SAVED_RECIPES, ONLINE_RECIPES, SOCIAL, PROFILE;
    }

    private final static String TAG = "MainActivity";

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final WeekFragment weekFragment = new WeekFragment();
    private final ShoppingListFragment shoppingListFragment = new ShoppingListFragment();
    private final SavedRecipesFragment savedRecipesFragment = new SavedRecipesFragment();
    private final OnlineRecipesFragment onlineRecipesFragment = new OnlineRecipesFragment();
    private final SocialFragment socialFragment = new SocialFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    private RecipeDetailsFragment recipeDetailsFragment;

    private BottomNavigationView bottomNavigationView;

    private RelativeLayout recipeFragmentHeader;
    private RelativeLayout weekFragmentHeader;

    private FragmentSelection activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weekFragmentHeader = findViewById(R.id.weekFragmentHeader);
        recipeFragmentHeader = findViewById(R.id.recipeFragmentHeader);
        recipeFragmentHeader.setVisibility(View.GONE);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setBottomNavigationListener();
        bottomNavigationView.setSelectedItemId(R.id.action_week);
        activeFragment = FragmentSelection.WEEK;

    }

    private void setBottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragment(getFragmentSelectionFromMenu(item));
                return true;
            }
        });
    }

    public void onRadioButtonClickedRecipes(View view) {
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

    public void onRadioButtonClickedWeek(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbWeek:
                if (checked)
                    changeFragment(FragmentSelection.WEEK);
                break;
            case R.id.rbShoppingList:
                if (checked)
                    changeFragment(FragmentSelection.SHOPPING_LIST);
                break;
        }
    }

    private FragmentSelection getFragmentSelectionFromMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_week:
                return FragmentSelection.WEEK;
            case R.id.action_recipes:
                return FragmentSelection.SAVED_RECIPES;
            //case R.id.action_social:
            //return FragmentSelection.SOCIAL;
            case R.id.action_profile:
            default:
                return FragmentSelection.PROFILE;
        }
    }

    private void changeFragment(FragmentSelection fragmentSelected) {
        if (activeFragment == fragmentSelected) return;

        Fragment fragment;
        activeFragment = fragmentSelected;

        switch (fragmentSelected) {
            case WEEK:
                weekFragmentHeader.setVisibility(View.VISIBLE);
                recipeFragmentHeader.setVisibility(View.GONE);
                fragment = weekFragment;
                break;
            case SHOPPING_LIST:
                weekFragmentHeader.setVisibility(View.VISIBLE);
                recipeFragmentHeader.setVisibility(View.GONE);
                fragment = shoppingListFragment;
                break;
            case SAVED_RECIPES:
                weekFragmentHeader.setVisibility(View.GONE);
                recipeFragmentHeader.setVisibility(View.VISIBLE);
                fragment = savedRecipesFragment;
                break;
            case ONLINE_RECIPES:
                weekFragmentHeader.setVisibility(View.GONE);
                recipeFragmentHeader.setVisibility(View.VISIBLE);
                fragment = onlineRecipesFragment;
                break;
            case SOCIAL:
                weekFragmentHeader.setVisibility(View.GONE);
                recipeFragmentHeader.setVisibility(View.GONE);
                fragment = socialFragment;
                break;
            case PROFILE:
            default:
                weekFragmentHeader.setVisibility(View.GONE);
                recipeFragmentHeader.setVisibility(View.GONE);
                fragment = profileFragment;
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    private void openRecipeDetailsFragment(IRecipe recipe) {
        recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipe);
        weekFragmentHeader.setVisibility(View.GONE);
        recipeFragmentHeader.setVisibility(View.GONE);
        fragmentManager.beginTransaction().replace(R.id.flContainer, recipeDetailsFragment).commit();
    }

    @Override
    public void openOnlineRecipeDetailsListener(IRecipe recipe) {
        openRecipeDetailsFragment(recipe);
    }

    @Override
    public void openSavedRecipeDetailsFragment(IRecipe recipe) {
        openRecipeDetailsFragment(recipe);
    }
  
}