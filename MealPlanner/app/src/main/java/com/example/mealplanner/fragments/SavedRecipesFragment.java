package com.example.mealplanner.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mealplanner.R;
import com.example.mealplanner.adapters.SavedRecipeAdapter;
import com.example.mealplanner.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SavedRecipesFragment extends Fragment {

    private final static String TAG = "SavedRecipes";

    public interface SavedRecipesFragmentListener{
        void openSavedRecipesFragment(Recipe recipe);
    }

    private RecyclerView rvRecipes;
    private List<Recipe> recipes;
    private SavedRecipeAdapter adapter;

    private SavedRecipesFragmentListener listener;

    public SavedRecipesFragment() {
        // Required empty public constructor
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

        recipes = new ArrayList<>();
        adapter = new SavedRecipeAdapter(getContext(), recipes, new SavedRecipeAdapter.SavedRecipeAdapterListener() {
            @Override
            public void openDetails(Recipe recipe) {
                listener.openSavedRecipesFragment(recipe);
            }
        });
        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        querySavedRecipes();
    }

    private void querySavedRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);

        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Fail getting recipes", e);
                    return;
                }
                recipes.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SavedRecipesFragmentListener) {
            listener = (SavedRecipesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Fragment new task Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}