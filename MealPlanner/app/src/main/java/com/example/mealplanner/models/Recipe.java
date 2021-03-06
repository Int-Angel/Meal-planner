package com.example.mealplanner.models;

import android.provider.CallLog;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a saved recipe from the user that it's saved in the database using
 * Parse
 */
@ParseClassName("Recipe")
public class Recipe extends ParseObject implements IRecipe {

    /**
     * All database keys
     */
    public final static String KEY_USER = "user";
    public final static String KEY_TITLE = "title";
    public final static String KEY_IMAGE_URL = "imageUrl";
    public final static String KEY_DISH_TYPE = "dishType";
    public final static String KEY_CUISINE_TYPE = "cuisineType";
    public final static String KEY_TOTAL_TIME = "totalTime";
    public final static String KEY_CALORIES = "calories";
    public final static String KEY_RECIPE_URL = "recipeUrl";
    public final static String KEY_INSTRUCTIONS = "instructions";
    public final static String KEY_INGREDIENTS = "ingredients";
    public final static String KEY_ID = "idRecipe";
    public final static String KEY_SUMMARY = "summary";
    public final static String KEY_INGREDIENTS_IMAGES_URL = "ingredientsImagesUrl";
    public final static String KEY_USER_CREATED = "userCreated";

    /**
     * Constructor from a online recipe
     *
     * @param onlineRecipe
     * @return returns a saved recipe object
     */
    public static Recipe createRecipe(OnlineRecipe onlineRecipe) {
        Recipe recipe = new Recipe();

        recipe.setUser(ParseUser.getCurrentUser());
        recipe.setTile(onlineRecipe.getTitle());
        recipe.setImageUrl(onlineRecipe.getImageUrl());
        recipe.setDishType(onlineRecipe.getDishType());
        recipe.setCuisineType(onlineRecipe.getCuisineType());
        recipe.setTotalTime(Integer.parseInt(onlineRecipe.getTotalTime()));
        recipe.setCalories(onlineRecipe.getCaloriesNumber());
        recipe.setRecipeUrl(onlineRecipe.getRecipeUrl());
        recipe.setIngredients(onlineRecipe.getIngredients());
        recipe.setId(onlineRecipe.getId());
        recipe.setInstructions(onlineRecipe.getInstructions());
        recipe.setIngredientsImagesUrl(onlineRecipe.getIngredientsImagesUrl());
        recipe.setSummary(onlineRecipe.getSummary());
        recipe.setUserCreated(false);

        return recipe;
    }

    /**
     * Creates a recipe from all the properties separated
     *
     * @param title
     * @param imageUrl
     * @param mealType
     * @param cuisineType
     * @param totalTime
     * @param calories
     * @param recipeUrl
     * @param ingredients
     * @param instructions
     * @return
     */
    public static Recipe createRecipe(String title, String imageUrl, String mealType, String cuisineType, String totalTime, float calories,
                                      String recipeUrl, List<String> ingredients, List<String> instructions) {
        Recipe recipe = new Recipe();

        recipe.setUser(ParseUser.getCurrentUser());
        recipe.setTile(title);
        recipe.setImageUrl(imageUrl);
        recipe.setDishType(mealType);
        recipe.setCuisineType(cuisineType);
        recipe.setTotalTime(Integer.parseInt(totalTime));
        recipe.setCalories(calories);
        recipe.setRecipeUrl(recipeUrl);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setUserCreated(true);
        recipe.setSummary("");

        return recipe;
    }

    /**
     * Constructor from a already saved recipe from other user
     *
     * @param newRecipe
     * @return returns a saved recipe object
     */
    public static Recipe createRecipe(Recipe newRecipe) {
        Recipe recipe = new Recipe();

        recipe.setUser(ParseUser.getCurrentUser());
        recipe.setTile(newRecipe.getTitle());
        recipe.setImageUrl(newRecipe.getImageUrl());
        recipe.setDishType(newRecipe.getDishType());
        recipe.setCuisineType(newRecipe.getCuisineType());
        recipe.setTotalTime(Integer.parseInt(newRecipe.getTotalTime()));
        recipe.setCalories(newRecipe.getCaloriesNumber());
        recipe.setRecipeUrl(newRecipe.getRecipeUrl());
        recipe.setIngredients(newRecipe.getIngredients());
        recipe.setId(newRecipe.getId());
        recipe.setInstructions(newRecipe.getInstructions());
        recipe.setIngredientsImagesUrl(newRecipe.getIngredientsImagesUrl());
        recipe.setSummary(newRecipe.getSummary());
        recipe.setUserCreated(false);

        return recipe;
    }

    /**
     * Getters and setters
     */

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    @Override
    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTile(String tile) {
        put(KEY_TITLE, tile);
    }

    @Override
    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

    public void setImageUrl(String imageUrl) {
        put(KEY_IMAGE_URL, imageUrl);
    }

    @Override
    public String getDishType() {
        return getString(KEY_DISH_TYPE);
    }

    public void setDishType(String dishType) {
        put(KEY_DISH_TYPE, dishType);
    }

    @Override
    public String getCuisineType() {
        return getString(KEY_CUISINE_TYPE);
    }

    public void setCuisineType(String cuisineType) {
        put(KEY_CUISINE_TYPE, cuisineType);
    }

    @Override
    public String getTotalTime() {
        return getNumber(KEY_TOTAL_TIME) + "";
    }

    public void setTotalTime(int totalTime) {
        put(KEY_TOTAL_TIME, totalTime);
    }

    @Override
    public float getCaloriesNumber() {
        return getLong(KEY_CALORIES);
    }

    @Override
    public String getCalories() {
        float caloriesNumber = getCaloriesNumber();
        return String.format("%.1f", caloriesNumber) + " kcal";
    }

    public void setCalories(float calories) {
        put(KEY_CALORIES, calories);
    }

    @Override
    public String getRecipeUrl() {
        return getString(KEY_RECIPE_URL);
    }

    public void setRecipeUrl(String recipeUrl) {
        put(KEY_RECIPE_URL, recipeUrl);
    }

    public JSONArray getInstructionsJSONArray() {
        return getJSONArray(KEY_INSTRUCTIONS);
    }

    /**
     * This method transforms the instructions JSON Array to a list of string
     *
     * @return
     */
    @Override
    public List<String> getInstructions() {
        JSONArray instructions = getInstructionsJSONArray();
        List<String> listInstructions = new ArrayList<>();

        for (int i = 0; i < instructions.length(); i++) {
            try {
                listInstructions.add(instructions.getString(i));
            } catch (JSONException e) {
                Log.e("RecipeClass", "Error while getting instructions", e);
            }
        }

        return listInstructions;
    }

    public void setInstructions(JSONArray instructions) {
        put(KEY_INSTRUCTIONS, instructions);
    }

    /**
     * This method transforms a list of instructions to a JSON Array and saves the instructions
     *
     * @param instructions
     */
    public void setInstructions(List<String> instructions) {
        JSONArray instructionsJSONArray = new JSONArray(instructions);
        setInstructions(instructionsJSONArray);
    }

    public JSONArray getIngredientsJSONArray() {
        return getJSONArray(KEY_INGREDIENTS);
    }

    /**
     * Returns the instruction in a List of strings
     *
     * @return
     */
    @Override
    public List<String> getIngredients() {
        JSONArray ingredients = getIngredientsJSONArray();
        List<String> listIngredients = new ArrayList<>();

        for (int i = 0; i < ingredients.length(); i++) {
            try {
                listIngredients.add(ingredients.getString(i));
            } catch (JSONException e) {
                Log.e("RecipeClass", "Error while getting ingredients", e);
            }
        }

        return listIngredients;
    }

    public void setIngredients(JSONArray ingredients) {
        put(KEY_INGREDIENTS, ingredients);
    }

    public void setIngredients(List<String> ingredients) {
        JSONArray ingredientsJSONArray = new JSONArray(ingredients);
        setIngredients(ingredientsJSONArray);
    }

    public JSONArray getIngredientsImagesUrlJSONArray() {
        return getJSONArray(KEY_INGREDIENTS_IMAGES_URL);
    }

    @Override
    public List<String> getIngredientsImagesUrl() {
        JSONArray ingredientsImagesUrl = getIngredientsImagesUrlJSONArray();
        List<String> listIngredientsImagesUrl = new ArrayList<>();

        for (int i = 0; i < ingredientsImagesUrl.length(); i++) {
            try {
                listIngredientsImagesUrl.add(ingredientsImagesUrl.getString(i));
            } catch (JSONException e) {
                Log.e("RecipeClass", "Error while getting ingredients images url", e);
            }
        }

        return listIngredientsImagesUrl;
    }

    public void setIngredientsImagesUrl(JSONArray ingredientsImagesUrlJSONArray) {
        put(KEY_INGREDIENTS_IMAGES_URL, ingredientsImagesUrlJSONArray);
    }

    public void setIngredientsImagesUrl(List<String> ingredientsImagesUrl) {
        JSONArray jsonArray = new JSONArray(ingredientsImagesUrl);
        setIngredientsImagesUrl(jsonArray);
    }

    @Override
    public String getId() {
        return getString(KEY_ID);
    }

    public void setId(String id) {
        put(KEY_ID, id);
    }

    @Override
    public String getSummary() {
        return getString(KEY_SUMMARY);
    }

    public void setSummary(String summary) {
        put(KEY_SUMMARY, summary);
    }

    public boolean getUserCreated() {
        return getBoolean(KEY_USER_CREATED);
    }

    public void setUserCreated(boolean userCreated) {
        put(KEY_USER_CREATED, userCreated);
    }
}
