package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.adapters.AddRecipeAdapter;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class AddRecipeFragment extends Fragment {

    private final static String TAG = "AddRecipeFragment";

    public interface AddRecipeListener {
        void addRecipeToPlan(Recipe recipe);
    }

    private AddRecipeListener listener;

    private ImageButton ibtnClose;
    private AddRecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private List<Recipe> recipes;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = (WeekFragment) getParentFragment();

        ibtnClose = view.findViewById(R.id.ibtnClose);
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        recipes = new ArrayList<>();
        recipes = SavedRecipesManager.getRecipes();
        rvRecipes = view.findViewById(R.id.rvRecipes);
        adapter = new AddRecipeAdapter(getContext(), recipes, new AddRecipeAdapter.AddRecipeAdapterListener() {
            @Override
            public void addToPlan(IRecipe recipe, int index) {
                listener.addRecipeToPlan((Recipe) recipe);
            }
        });
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void closeFragment() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }
}