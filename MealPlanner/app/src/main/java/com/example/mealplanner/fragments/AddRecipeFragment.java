package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.adapters.AddRecipeAdapter;
import com.example.mealplanner.models.AddRecipe;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment that shows all the saved recipes to add to a meal plan day
 */
public class AddRecipeFragment extends Fragment {

    private final static String TAG = "AddRecipeFragment";
    private final static String ADDED_RECIPES = "recipes";

    public interface AddRecipeListener {
        void addRecipeToPlan(Recipe recipe);
    }

    private AddRecipeListener listener;
    private List<Recipe> alreadyAddedRecipes;

    private ImageButton ibtnClose;
    private AddRecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private List<Recipe> recipes;
    private List<AddRecipe> addRecipes;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * It needs all the already saved recipes to change it's icon
     *
     * @param alreadyAddedRecipes
     * @return
     */
    public static AddRecipeFragment newInstance(List<Recipe> alreadyAddedRecipes) {
        AddRecipeFragment addRecipeFragment = new AddRecipeFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ADDED_RECIPES, (ArrayList<? extends Parcelable>) alreadyAddedRecipes);

        addRecipeFragment.setArguments(args);
        return addRecipeFragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alreadyAddedRecipes = getArguments().getParcelableArrayList(ADDED_RECIPES);
        }
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
        recipes = SavedRecipesManager.getInstance().getRecipes();

        addRecipes = new ArrayList<>();
        addRecipes = generateAddRecipeList();

        rvRecipes = view.findViewById(R.id.rvRecipes);
        adapter = new AddRecipeAdapter(getContext(), addRecipes, new AddRecipeAdapter.AddRecipeAdapterListener() {
            @Override
            public void addToPlan(IRecipe recipe, int index) {
                listener.addRecipeToPlan((Recipe) recipe);
            }
        });
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Generates all the addRecipe list to change the icon based in the already added recipes of
     * that day
     *
     * @return
     */
    private List<AddRecipe> generateAddRecipeList() {
        List<AddRecipe> addRecipes = new ArrayList<>();
        Set<Integer> alreadyAdded = generateIdAddedRecipesSet();

        for (int i = 0; i < recipes.size(); i++) {
            AddRecipe addRecipe = new AddRecipe();
            addRecipe.setRecipe(recipes.get(i));

            String idRecipe = recipes.get(i).getId();
            addRecipe.setAdded(alreadyAdded.contains(Integer.parseInt(idRecipe)));

            addRecipes.add(addRecipe);
        }

        return addRecipes;
    }

    /**
     * Generates a id set for the already added recipes
     *
     * @return
     */
    private Set<Integer> generateIdAddedRecipesSet() {
        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < alreadyAddedRecipes.size(); i++) {
            String idRecipe = alreadyAddedRecipes.get(i).getId();
            set.add(Integer.parseInt(idRecipe));
        }

        return set;
    }

    /**
     * Close the fragment
     */
    private void closeFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_down,R.anim.bottom_down)
                .remove(this)
                .commit();
    }
}