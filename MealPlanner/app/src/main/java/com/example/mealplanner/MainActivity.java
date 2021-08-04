package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mealplanner.fragments.ProfileFragment;
import com.example.mealplanner.fragments.RecipeFragment;
import com.example.mealplanner.fragments.ShoppingListManagerFragment;
import com.example.mealplanner.fragments.SocialFragment;
import com.example.mealplanner.fragments.WeekFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * MainActivity that contains all the fragments and it's in charge of the bottom navigation of the
 * app
 */
public class MainActivity extends AppCompatActivity {

    /**
     * All possible fragments to be open from this activity
     */
    private enum FragmentSelection {
        RECIPES, SOCIAL, PROFILE, WEEK, SHOPPING_LIST
    }

    private final static String TAG = "MainActivity";

    private FilteringViewModel filteringViewModel;

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final WeekFragment weekFragment = new WeekFragment();
    private final ShoppingListManagerFragment shoppingListManagerFragment = new ShoppingListManagerFragment();
    private final RecipeFragment recipeFragment = new RecipeFragment();
    private final SocialFragment socialFragment = new SocialFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    private BottomNavigationView bottomNavigationView;

    private FragmentSelection activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setBottomNavigationListener();
        bottomNavigationView.setSelectedItemId(R.id.action_plan);
        activeFragment = FragmentSelection.WEEK;

        filteringViewModel = new ViewModelProvider(this).get(FilteringViewModel.class);
        filteringViewModel.setApplyChanges(false);
        filteringViewModel.setActiveCuisines(false);
        filteringViewModel.setActiveMealTypes(false);
        filteringViewModel.setActiveMaxTimeReady(false);
        filteringViewModel.setActiveCalories(false);

        SavedRecipesManager.getInstance();
    }

    /**
     * sets up the OnItemSelectedListener of the bottom navigation bar
     */
    private void setBottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragment(getFragmentSelectionFromMenu(item));
                return true;
            }
        });
    }

    /**
     * Returns the fragment that should be open based on the menuItem selected in the bottom
     * navigation bar
     *
     * @param item
     * @return
     */
    private FragmentSelection getFragmentSelectionFromMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_plan:
                return FragmentSelection.WEEK;
            case R.id.action_shoppingList:
                return FragmentSelection.SHOPPING_LIST;
            case R.id.action_recipes:
                return FragmentSelection.RECIPES;
            case R.id.action_social:
                return FragmentSelection.SOCIAL;
            case R.id.action_profile:
            default:
                return FragmentSelection.PROFILE;
        }
    }

    /**
     * Changes the fragment on screen based on the fragmentSelected parameter
     *
     * @param fragmentSelected fragment to be open
     */
    private void changeFragment(FragmentSelection fragmentSelected) {
        Fragment fragment;
        activeFragment = fragmentSelected;

        switch (fragmentSelected) {
            case WEEK:
                fragment = weekFragment;
                break;
            case SHOPPING_LIST:
                fragment = shoppingListManagerFragment;
                break;
            case RECIPES:
                fragment = recipeFragment;
                break;
            case SOCIAL:
                fragment = socialFragment;
                break;
            case PROFILE:
            default:
                fragment = profileFragment;
                break;
        }
        fragmentManager
                .beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();



    }
}