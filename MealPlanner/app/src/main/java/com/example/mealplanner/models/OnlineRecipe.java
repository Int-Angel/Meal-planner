package com.example.mealplanner.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@Parcel
public class OnlineRecipe implements IRecipe {

    private final static String TAG = "OnlineRecipeClass";

    private String title;
    private String imageUrl;
    private String dishType;
    private String cuisineType;
    private String totalTime;
    private String calories;
    private String caloriesUnit;
    private String recipeUrl;
    private float caloriesNumber;
    private List<String> ingredients;
    private List<String> ingredientsImagesUrl;
    private String id;
    private boolean isSaved;
    private String summary;
    private List<String> instructions;


    public OnlineRecipe() {
    }

    public OnlineRecipe(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("id");
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image");
        recipeUrl = jsonObject.getString("sourceUrl");
        totalTime = jsonObject.getString("readyInMinutes");
        summary = jsonObject.getString("summary");

        JSONObject nutrition = jsonObject.getJSONObject("nutrition");

        calories = nutrition.getJSONArray("nutrients").getJSONObject(0).getString("amount");
        caloriesUnit = nutrition.getJSONArray("nutrients").getJSONObject(0).getString("unit");
        caloriesNumber = Float.parseFloat(calories);
        calories += " " + caloriesUnit;

        try {
            JSONArray cuisineTypeArray = jsonObject.getJSONArray("cuisines");
            if (cuisineTypeArray.length() != 0)
                cuisineType = cuisineTypeArray.getString(0);
            else
                cuisineType = "";
        } catch (JSONException e) {
            cuisineType = "";
        }

        try {
            JSONArray dishTypeArray = jsonObject.getJSONArray("dishTypes");
            if (dishTypeArray.length() != 0)
                dishType = dishTypeArray.getString(0);
            else
                dishType = "";
        } catch (JSONException e) {
            dishType = "";
        }

        getIngredientsFromJson(jsonObject);
        getInstructionsFromJson(jsonObject);
    }

    private void getIngredientsFromJson(JSONObject jsonObject) {
        ingredients = new ArrayList<>();
        ingredientsImagesUrl = new ArrayList<>();
        try {
            JSONArray ingredientsArray = jsonObject.getJSONArray("extendedIngredients");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredients.add(ingredientsArray.getJSONObject(i).getString("original"));

                String image = ingredientsArray.getJSONObject(i).getString("image");
                String imageUrl = "https://spoonacular.com/cdn/ingredients_250x250/";
                ingredientsImagesUrl.add(imageUrl + image);
            }
        } catch (JSONException e) {
            //no ingredients list
        }
    }

    private void getInstructionsFromJson(JSONObject jsonObject) {
        instructions = new ArrayList<>();
        try {
            JSONArray analyzedInstructions = jsonObject.getJSONArray("analyzedInstructions");
            JSONArray steps = analyzedInstructions.getJSONObject(0).getJSONArray("steps");

            for (int i = 0; i < steps.length(); i++) {
                instructions.add(steps.getJSONObject(i).getString("step"));
            }
        } catch (JSONException e) {
            //no instructions
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getDishType() {
        return dishType;
    }

    @Override
    public String getCuisineType() {
        return cuisineType;
    }

    @Override
    public String getTotalTime() {
        return totalTime;
    }

    @Override
    public String getCalories() {
        return calories;
    }

    @Override
    public String getRecipeUrl() {
        return recipeUrl;
    }

    @Override
    public List<String> getIngredients() {
        return ingredients;
    }

    @Override
    public float getCaloriesNumber() {
        return caloriesNumber;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public List<String> getInstructions() {
        return instructions;
    }

    @Override
    public List<String> getIngredientsImagesUrl() {
        return ingredientsImagesUrl;
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
