package com.example.mealplanner.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.adapters.IngredientsImagesAdapter;
import com.example.mealplanner.adapters.StepsAdapter;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

import java.util.List;


public class RecipeDetailsFragment extends Fragment {

    public interface RecipeDetailsFragmentListener {
        void backButtonPressed();

        void updateRecipeList();
    }

    private static final String TAG = "RecipeDetails";
    private static final String RECIPE = "recipe";
    private static final String INDEX = "index";

    private IRecipe recipe;
    private int index;
    private RecipeDetailsFragmentListener listener;
    private IngredientsImagesAdapter adapter;
    private StepsAdapter stepsAdapter;

    private TextView tvTitle;
    private ImageButton ibtnBackOnlineDetails;
    private ImageButton ibtnSaveRecipeDetails;
    private ImageButton ibtnGoToOriginalUrl;
    private ImageView ivRecipeImageOnlineDetails;
    private TextView tvDishTypeDetails;
    private TextView tvCuisineTypeDetails;
    private TextView tvMealTypeDetails;
    private TextView tvCaloriesDetails;
    private TextView tvTimeDetails;
    private TextView tvIngredients;
    private CardView cvDetails;
    private TextView tvIngredientsTitle;
    private RecyclerView rvIngredientsImages;
    private ViewPager vpSteps;
    private TabLayout tabLayout;


    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailsFragment newInstance(IRecipe recipe, int index) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE, Parcels.wrap(recipe));
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(RECIPE));
            index = getArguments().getInt(INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tvTitle);
        ibtnBackOnlineDetails = view.findViewById(R.id.ibtnBackOnlineDetails);
        ibtnSaveRecipeDetails = view.findViewById(R.id.ibtnSaveRecipeDetails);
        ivRecipeImageOnlineDetails = view.findViewById(R.id.ivRecipeImageOnlineDetails);
        tvDishTypeDetails = view.findViewById(R.id.tvDishTypeDetails);
        tvCuisineTypeDetails = view.findViewById(R.id.tvCuisineTypeDetails);
        tvMealTypeDetails = view.findViewById(R.id.tvMealTypeDetails);
        tvCaloriesDetails = view.findViewById(R.id.tvCaloriesDetails);
        tvTimeDetails = view.findViewById(R.id.tvTimeDetails);
        tvIngredients = view.findViewById(R.id.tvIngredients);
        cvDetails = view.findViewById(R.id.cvDetails);
        tvIngredientsTitle = view.findViewById(R.id.tvIngredientsTitle);
        ibtnGoToOriginalUrl = view.findViewById(R.id.ibtnGoToOriginalUrl);
        rvIngredientsImages = view.findViewById(R.id.rvIngredientsImages);
        vpSteps = view.findViewById(R.id.vpSteps);
        tabLayout = view.findViewById(R.id.tabLayout);

        listener = (RecipeDetailsFragmentListener) getParentFragment();

        stepsAdapter = new StepsAdapter(getContext(),recipe.getInstructions());
        vpSteps.setAdapter(stepsAdapter);
        tabLayout.setupWithViewPager(vpSteps,true);

        int pagerPadding = 20;
        vpSteps.setClipToPadding(false);
        vpSteps.setPadding(pagerPadding, pagerPadding, pagerPadding, pagerPadding);

        adapter = new IngredientsImagesAdapter(getContext(), recipe.getIngredientsImagesUrl());
        rvIngredientsImages.setAdapter(adapter);
        rvIngredientsImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        bind();
        setupOnClickListeners();
    }

    private void bind() {

        if (recipe instanceof Recipe)
            ibtnSaveRecipeDetails.setSelected(true);
        else {
            ibtnSaveRecipeDetails.setSelected(((OnlineRecipe) recipe).isSaved());
        }

        tvTitle.setText(recipe.getTitle());

        if (recipe.getDishType().equals(""))
            tvDishTypeDetails.setVisibility(View.GONE);
        else
            tvDishTypeDetails.setText(recipe.getDishType());

        if (recipe.getCuisineType().equals(""))
            tvCuisineTypeDetails.setVisibility(View.GONE);
        else
            tvCuisineTypeDetails.setText(recipe.getCuisineType());

        tvCaloriesDetails.setText(recipe.getCalories());
        tvTimeDetails.setText(recipe.getTotalTime() + " min");

        Glide.with(getContext())
                .load(recipe.getImageUrl())
                .transform(new CenterCrop(),new RoundedCorners(1000))
                .into(ivRecipeImageOnlineDetails);

        StringBuilder tempIngredients = new StringBuilder();
        List<String> ingredientsList = recipe.getIngredients();
        for (int i = 0; i < ingredientsList.size(); i++) {
            tempIngredients.append("• " + ingredientsList.get(i)).append("\n");
        }

        tvIngredients.setText(tempIngredients.toString());
    }

    private void setupOnClickListeners() {
        ibtnBackOnlineDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backButtonPressed();
            }
        });

        ibtnSaveRecipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyRecipe();
            }
        });

        ibtnGoToOriginalUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl(recipe.getRecipeUrl());
            }
        });
    }

    private void goToUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void copyRecipe() {
        if (this.recipe instanceof OnlineRecipe) {
            saveOnlineRecipe();
        } else {
            saveSavedRecipe();
        }
    }

    private void saveOnlineRecipe() {
        if (!((OnlineRecipe) recipe).isSaved()) {
            SavedRecipesManager.saveRecipe((OnlineRecipe) recipe);
            ibtnSaveRecipeDetails.setSelected(true);
            ((OnlineRecipe) recipe).setSaved(true);
        } else {
            SavedRecipesManager.unSaveRecipeById(recipe.getId());
            ibtnSaveRecipeDetails.setSelected(false);
            ((OnlineRecipe) recipe).setSaved(false);
        }
    }

    private void saveSavedRecipe() {
        ibtnSaveRecipeDetails.setSelected(!ibtnSaveRecipeDetails.isSelected());
    }

    private void removeSavedRecipe() {
        if (recipe instanceof Recipe) {
            if (!ibtnSaveRecipeDetails.isSelected()) {
                SavedRecipesManager.unSaveRecipeById(recipe.getId());
                //SavedRecipesManager.unSaveRecipe(index);
                listener.updateRecipeList();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        removeSavedRecipe();
        listener = null;
    }
}