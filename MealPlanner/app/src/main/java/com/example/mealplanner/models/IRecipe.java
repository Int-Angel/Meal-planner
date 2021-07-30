package com.example.mealplanner.models;

import java.util.List;

/**
 * Interface implemented by Recipe and OnlineRecipe, it contains based recipe methods
 */
public interface IRecipe {

    public String getTitle();

    public String getImageUrl();

    public String getDishType();

    public String getCuisineType();

    public String getTotalTime();

    public String getCalories();

    public String getRecipeUrl();

    public List<String> getIngredients();

    public float getCaloriesNumber();

    public String getId();

    public String getSummary();

    public List<String> getInstructions();

    public List<String> getIngredientsImagesUrl();
}
