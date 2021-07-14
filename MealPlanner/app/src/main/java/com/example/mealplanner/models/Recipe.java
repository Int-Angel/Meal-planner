package com.example.mealplanner.models;

import android.provider.CallLog;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {

    public final static String KEY_USER = "user";
    public final static String KEY_TITLE = "title";
    public final static String KEY_IMAGE_URL = "imageUrl";
    public final static String KEY_DISH_TYPE = "dishType";
    public final static String KEY_MEAL_TYPE = "mealType";
    public final static String KEY_CUISINE_TYPE = "cuisineType";
    public final static String KEY_TOTAL_TIME = "totalTime";
    public final static String KEY_CALORIES = "calories";
    public final static String KEY_RECIPE_URL = "recipeUrl";
    public final static String KEY_INSTRUCTIONS = "instructions";
    public final static String KEY_INGREDIENTS = "ingredients";

    public static Recipe createRecipe(OnlineRecipe onlineRecipe) {
        Recipe recipe = new Recipe();

        recipe.setUser(ParseUser.getCurrentUser());
        recipe.setTile(onlineRecipe.getTitle());
        recipe.setImageUrl(onlineRecipe.getImageUrl());
        recipe.setDishType(onlineRecipe.getDishType());
        recipe.setMealType(onlineRecipe.getMealType());
        recipe.setCuisineType(onlineRecipe.getCuisineType());
        recipe.setTotalTime(onlineRecipe.getTotalTime());
        recipe.setCalories(onlineRecipe.getCaloriesNumber());
        recipe.setRecipeUrl(onlineRecipe.getRecipeUrl());
        recipe.setInstructions("Copy instructions from link");
        recipe.setIngredients(onlineRecipe.getIngredients());

        return recipe;
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTile(String tile) {
        put(KEY_TITLE, tile);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

    public void setImageUrl(String imageUrl) {
        put(KEY_IMAGE_URL, imageUrl);
    }

    public String getDishType() {
        return getString(KEY_DISH_TYPE);
    }

    public void setDishType(String dishType) {
        put(KEY_DISH_TYPE, dishType);
    }

    public String getMealType() {
        return getString(KEY_MEAL_TYPE);
    }

    public void setMealType(String mealType) {
        put(KEY_MEAL_TYPE, mealType);
    }

    public String getCuisineType() {
        return getString(KEY_CUISINE_TYPE);
    }

    public void setCuisineType(String cuisineType) {
        put(KEY_CUISINE_TYPE, cuisineType);
    }

    public String getTotalTime() {
        return getString(KEY_TOTAL_TIME);
    }

    public void setTotalTime(String totalTime) {
        put(KEY_TOTAL_TIME, totalTime);
    }

    public float getCalories() {
        return getLong(KEY_CALORIES);
    }

    public String getCaloriesText() {
        float caloriesNumber = getCalories();
        String calories;

        if (caloriesNumber / 1000f >= 1f) {
            calories = String.format("%.1f", caloriesNumber / 1000f) + " kcal";
        } else {
            calories = String.format("%.1f", caloriesNumber) + " cal";
        }
        return calories;
    }

    public void setCalories(float calories) {
        put(KEY_CALORIES, calories);
    }

    public String getRecipeUrl() {
        return getString(KEY_RECIPE_URL);
    }

    public void setRecipeUrl(String recipeUrl) {
        put(KEY_RECIPE_URL, recipeUrl);
    }

    public String getInstructions() {
        return getString(KEY_INSTRUCTIONS);
    }

    public void setInstructions(String instructions) {
        put(KEY_INSTRUCTIONS, instructions);
    }

    public JSONArray getIngredientsJSONArray() {
        return getJSONArray(KEY_INGREDIENTS);
    }

    public void setIngredients(JSONArray ingredients) {
        put(KEY_INGREDIENTS, ingredients);
    }

    public List<String> getIngredients() throws JSONException {
        JSONArray ingredients = getIngredientsJSONArray();
        List<String> listIngredients = new ArrayList<>();

        for (int i = 0; i < ingredients.length(); i++) {
            listIngredients.add(ingredients.getString(i));
        }

        return listIngredients;
    }

    public void setIngredients(List<String> ingredients) {
        JSONArray ingredientsJSONArray = new JSONArray(ingredients);
        setIngredients(ingredientsJSONArray);
    }
}