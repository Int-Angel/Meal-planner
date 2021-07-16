package com.example.mealplanner.models;

import java.util.List;

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
