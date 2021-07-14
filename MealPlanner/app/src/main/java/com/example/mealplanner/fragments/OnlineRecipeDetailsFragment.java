package com.example.mealplanner.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
    private CardView cvDetails;
    private TextView tvIngredientsTitle;


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
        cvDetails = view.findViewById(R.id.cvDetails);
        tvIngredientsTitle = view.findViewById(R.id.tvIngredientsTitle);

        bind();
        changeColors();
    }

    private void bind() {
        tvTitle.setText(recipe.getTitle());

        if(recipe.getDishType().equals(""))
            tvDishTypeDetails.setVisibility(View.GONE);
        else
            tvDishTypeDetails.setText(recipe.getDishType());

        if(recipe.getCuisineType().equals(""))
            tvCuisineTypeDetails.setVisibility(View.GONE);
        else
            tvCuisineTypeDetails.setText(recipe.getCuisineType());

        if(recipe.getMealType().equals(""))
            tvMealTypeDetails.setVisibility(View.GONE);
        else
            tvMealTypeDetails.setText(recipe.getMealType());

        tvCaloriesDetails.setText(recipe.getCalories());
        tvTimeDetails.setText(recipe.getTotalTime() + " min");

        Glide.with(getContext())
                .load(recipe.getImageUrl())
                .transform(new RoundedCorners(600))
                .into(ivRecipeImageOnlineDetails);

        StringBuilder tempIngredients = new StringBuilder();
        List<String> ingredientsList = recipe.getIngredients();
        for (int i = 0; i < ingredientsList.size(); i++) {
            tempIngredients.append(ingredientsList.get(i)).append("\n");
        }

        tvIngredients.setText(tempIngredients.toString());
    }

    private void changeColors(){

        /*CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                // TODO 1. Instruct Glide to load the bitmap into the `holder.ivProfile` profile image view

                Glide.with(getContext())
                        .load(recipe.getImageUrl())
                        .transform(new RoundedCorners(600))
                        .into(ivRecipeImageOnlineDetails);

                // TODO 2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                // Set the result as the background color for `holder.vPalette` view containing the contact's name.
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // can leave empty
            }
        };

        // Instruct Glide to load the bitmap into the asynchronous target defined above
        Glide.with(getContext()).asBitmap().load(recipe.getImageUrl()).centerCrop().into(target);

        Palette.from(bitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                // Get the "vibrant" color swatch based on the bitmap
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    // Set the background color of a layout based on the vibrant color
                    containerView.setBackgroundColor(vibrant.getRgb());
                    // Update the title TextView with the proper text color
                    titleView.setTextColor(vibrant.getTitleTextColor());
                }
            }
        });*/
    }
}