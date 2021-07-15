package com.example.mealplanner.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class OnlineRecipe implements IRecipe {

    private String title;
    private String imageUrl;
    private String dishType;
    private String mealType;
    private String cuisineType;
    private String totalTime;
    private String calories;
    private String recipeUrl;
    private float caloriesNumber;
    private List<String> ingredients;
    private String uri;
    private boolean isSaved;

    public OnlineRecipe() {
    }

    public OnlineRecipe(JSONObject jsonObject) throws JSONException {
        JSONObject recipe = jsonObject.getJSONObject("recipe");
        title = recipe.getString("label");
        imageUrl = recipe.getString("image");
        recipeUrl = recipe.getString("url");
        totalTime = recipe.getString("totalTime");
        uri = recipe.getString("uri");

        calories = recipe.getString("calories");
        caloriesNumber = Float.parseFloat(calories);

        if (caloriesNumber / 1000f >= 1f) {
            calories = String.format("%.1f", caloriesNumber / 1000f) + " kcal";
        } else {
            calories = String.format("%.1f", caloriesNumber) + " cal";
        }

        ingredients = new ArrayList<>();

        try {
            JSONArray ingredientsArray = recipe.getJSONArray("ingredientLines");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredients.add(ingredientsArray.getString(i));
            }
        } catch (JSONException e) {
            //no ingredients list
        }

        try {
            JSONArray cuisineTypeArray = recipe.getJSONArray("cuisineType");
            if (cuisineTypeArray.length() != 0)
                cuisineType = cuisineTypeArray.getString(0);
            else
                cuisineType = "";
        } catch (JSONException e) {
            cuisineType = "";
        }

        try {
            JSONArray mealTypeArray = recipe.getJSONArray("mealType");
            if (mealTypeArray.length() != 0)
                mealType = mealTypeArray.getString(0);
            else
                mealType = "";
        } catch (JSONException e) {
            mealType = "";
        }

        try {
            JSONArray dishTypeArray = recipe.getJSONArray("dishType");
            if (dishTypeArray.length() != 0)
                dishType = dishTypeArray.getString(0);
            else
                dishType = "";
        } catch (JSONException e) {
            dishType = "";
        }

    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDishType() {
        return dishType;
    }

    public String getMealType() {
        return mealType;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public String getCalories() {
        return calories;
    }

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public float getCaloriesNumber() {
        return caloriesNumber;
    }

    public String getUri() {
        return uri;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public static List<OnlineRecipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<OnlineRecipe> recipes = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new OnlineRecipe(jsonArray.getJSONObject(i)));
        }

        return recipes;
    }
}
