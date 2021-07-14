package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.parceler.Parcels;

import java.util.List;


public class OnlineRecipeDetailsFragment extends Fragment {

    private static final String RECIPE = "recipe";

    private OnlineRecipe recipe;

    private TextView tvTitle;
    private ImageButton ibtnBackOnlineDetails;
    private ImageButton ibtnSaveRecipeDetails;
    private ImageView ivRecipeImageOnlineDetails;
    private TextView tvDishTypeDetails;
    private TextView tvCuisineTypeDetails;
    private TextView tvMealTypeDetails;
    private TextView tvCaloriesDetails;
    private TextView tvTimeDetails;
    private TextView tvIngredients;


    public OnlineRecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static OnlineRecipeDetailsFragment newInstance(OnlineRecipe recipe) {
        OnlineRecipeDetailsFragment fragment = new OnlineRecipeDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_online_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tvTitle);
        ibtnBackOnlineDetails = view.findViewById(R.id.ibtnBackOnlineDetails); //TODO: create OnclickListener
        ibtnSaveRecipeDetails = view.findViewById(R.id.ibtnSaveRecipeDetails); //TODO: create OnclickListener
        ivRecipeImageOnlineDetails = view.findViewById(R.id.ivRecipeImageOnlineDetails);
        tvDishTypeDetails = view.findViewById(R.id.tvDishTypeDetails);
        tvCuisineTypeDetails = view.findViewById(R.id.tvCuisineTypeDetails);
        tvMealTypeDetails = view.findViewById(R.id.tvMealTypeDetails);
        tvCaloriesDetails = view.findViewById(R.id.tvCaloriesDetails);
        tvTimeDetails = view.findViewById(R.id.tvTimeDetails);
        tvIngredients = view.findViewById(R.id.tvIngredients);

        bind();
    }

    private void bind() {
        tvTitle.setText(recipe.getTitle());

        Glide.with(getContext())
                .load(recipe.getImageUrl())
                .transform(new RoundedCorners(600))
                .into(ivRecipeImageOnlineDetails);

        tvDishTypeDetails.setText(recipe.getDishType());
        tvCuisineTypeDetails.setText(recipe.getCuisineType());
        tvMealTypeDetails.setText(recipe.getMealType());
        tvCaloriesDetails.setText(recipe.getCalories());
        tvTimeDetails.setText(recipe.getTotalTime());

        StringBuilder tempIngredients = new StringBuilder();
        List<String> ingredientsList = recipe.getIngredients();
        for (int i = 0; i < ingredientsList.size(); i++) {
            tempIngredients.append(ingredientsList.get(i)).append("\n");
        }

        tvIngredients.setText(tempIngredients.toString());
    }
}