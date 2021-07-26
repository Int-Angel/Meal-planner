package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mealplanner.fragments.ProfileFragment;
import com.example.mealplanner.fragments.RecipeFragment;
import com.example.mealplanner.fragments.ShoppingListManagerFragment;
import com.example.mealplanner.fragments.SocialFragment;
import com.example.mealplanner.fragments.WeekFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public enum FragmentSelection {
        HOME, RECIPES, SOCIAL, PROFILE, WEEK, SHOPPING_LIST, SAVED_RECIPES, ONLINE_RECIPES;
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
    private FragmentSelection lastActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setBottomNavigationListener();
        bottomNavigationView.setSelectedItemId(R.id.action_plan);
        activeFragment = FragmentSelection.HOME;

        filteringViewModel = new ViewModelProvider(this).get(FilteringViewModel.class);
        SavedRecipesManager.querySavedRecipes();
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


    private FragmentSelection getFragmentSelectionFromMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_plan:
                return FragmentSelection.WEEK;
            case R.id.action_shoppingList:
                return FragmentSelection.SHOPPING_LIST;
            case R.id.action_recipes:
                return FragmentSelection.RECIPES;
            case R.id.action_profile:
            default:
                return FragmentSelection.PROFILE;
        }
    }

    private void changeFragment(FragmentSelection fragmentSelected) {
        //if (activeFragment == fragmentSelected) return;

        Fragment fragment;
        lastActiveFragment = activeFragment;
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

        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}