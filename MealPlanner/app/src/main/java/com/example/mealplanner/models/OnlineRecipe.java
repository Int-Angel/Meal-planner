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
    private String id;
    private boolean isSaved;
    private String summary;
    private String instructions;


    public OnlineRecipe() {
    }

    public OnlineRecipe(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("id");
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image");
        recipeUrl = jsonObject.getString("sourceUrl");
        totalTime = jsonObject.getString("readyInMinutes");
        summary = jsonObject.getString("summary");
        //instructions = jsonObject.getString("instructions");

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
    }

    private void getIngredientsFromJson(JSONObject jsonObject){
        // TODO get all ingredients data
        ingredients = new ArrayList<>();
        try {
            JSONArray ingredientsArray = jsonObject.getJSONArray("extendedIngredients");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredients.add(ingredientsArray.getJSONObject(i).getString("original"));
            }
        } catch (JSONException e) {
            //no ingredients list
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

    public String getId() {
        return id;
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
