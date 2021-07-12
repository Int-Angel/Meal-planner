package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mealplanner.fragments.OnlineRecipesFragment;
import com.example.mealplanner.fragments.ProfileFragment;
import com.example.mealplanner.fragments.SavedRecipesFragment;
import com.example.mealplanner.fragments.ShoppingListFragment;
import com.example.mealplanner.fragments.SocialFragment;
import com.example.mealplanner.fragments.WeekFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final WeekFragment weekFragment = new WeekFragment();
    private final ShoppingListFragment shoppingListFragment = new ShoppingListFragment();
    private final SavedRecipesFragment savedRecipesFragment = new SavedRecipesFragment();
    private final OnlineRecipesFragment onlineRecipesFragment = new OnlineRecipesFragment();
    private final SocialFragment socialFragment = new SocialFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setBottomNavigationListener();
        bottomNavigationView.setSelectedItemId(R.id.action_week);
    }

    private void setBottomNavigationListener(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragment(item);
                return true;
            }
        });
    }

    private void changeFragment(MenuItem item){
        Fragment fragment;

        switch (item.getItemId()){
            case R.id.action_week:
                fragment = weekFragment;
                break;
            case R.id.action_recipes:
                fragment = savedRecipesFragment;
                break;
            case R.id.action_social:
                fragment = socialFragment;
                break;
            case R.id.action_profile:
            default:
                fragment = profileFragment;
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
    }

}