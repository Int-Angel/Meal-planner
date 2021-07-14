package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;


public class SavedRecipeDetailsFragment extends Fragment {

    private static final String TAG = "SavedRecipeDetails";
    private static final String RECIPE = "recipe";

    private Recipe recipe;

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

    public SavedRecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static SavedRecipeDetailsFragment newInstance(Recipe recipe) {
        SavedRecipeDetailsFragment fragment = new SavedRecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE, Parcels.wrap(recipe));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = Parcels.unwrap(getArguments().getParcelable(RECIPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
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

        bind();
        setupOnClickListeners();
    }

    private void bind() {
        tvTitle.setText(recipe.getTitle());

        if (recipe.getDishType().equals(""))
            tvDishTypeDetails.setVisibility(View.GONE);
        else
            tvDishTypeDetails.setText(recipe.getDishType());

        if (recipe.getCuisineType().equals(""))
            tvCuisineTypeDetails.setVisibility(View.GONE);
        else
            tvCuisineTypeDetails.setText(recipe.getCuisineType());

        if (recipe.getMealType().equals(""))
            tvMealTypeDetails.setVisibility(View.GONE);
        else
            tvMealTypeDetails.setText(recipe.getMealType());

        tvCaloriesDetails.setText(recipe.getCaloriesText());
        tvTimeDetails.setText(recipe.getTotalTime() + " min");

        Glide.with(getContext())
                .load(recipe.getImageUrl())
                .transform(new RoundedCorners(600))
                .into(ivRecipeImageOnlineDetails);

        StringBuilder tempIngredients = new StringBuilder();
        List<String> ingredientsList = null;
        try {
            ingredientsList = recipe.getIngredients();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < ingredientsList.size(); i++) {
            tempIngredients.append(ingredientsList.get(i)).append("\n");
        }

        tvIngredients.setText(tempIngredients.toString());
    }

    private void setupOnClickListeners() {
        ibtnBackOnlineDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ibtnSaveRecipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ibtnGoToOriginalUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}