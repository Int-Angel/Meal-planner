package com.example.mealplanner.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {

    public final static String KEY_RECIPE = "recipe";
    public final static String KEY_AISLE = "aisle";
    public final static String KEY_IMAGE = "image";
    public final static String KEY_NAME = "name";
    public final static String KEY_NAME_CLEAN = "nameClean";
    public final static String KEY_ORIGINAL = "original";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_UNIT = "unit";
    public final static String KEY_INGREDIENT_ID = "ingredientId";

    public static Ingredient createIngredient(Recipe recipe, JSONObject jsonObject) throws JSONException {
        Ingredient ingredient = new Ingredient();

        ingredient.setRecipe(recipe);
        ingredient.setAisle(jsonObject.getString("aisle"));
        ingredient.setImage(jsonObject.getString("image"));
        ingredient.setName(jsonObject.getString("name"));
        ingredient.setNameClean(jsonObject.getString("nameClean"));
        ingredient.setOriginal(jsonObject.getString("original"));

        JSONObject metric = jsonObject.getJSONObject("measures").getJSONObject("metric");

        ingredient.setAmount(metric.getLong("amount"));
        ingredient.setUnit(metric.getString("unitShort"));
        ingredient.setIngredientId(jsonObject.getString("id"));

        return ingredient;
    }

    public static List<Ingredient> fromJSONArray(Recipe recipe, JSONArray jsonArray) throws JSONException {
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ingredients.add(createIngredient(recipe, jsonArray.getJSONObject(i)));
        }

        return ingredients;
    }

    public ParseObject getRecipe() {
        return getParseObject(KEY_RECIPE);
    }

    public String getAisle() {
        return getString(KEY_AISLE);
    }

    public String getImage() {
        return getString(KEY_IMAGE);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public String getNameClean() {
        return getString(KEY_NAME_CLEAN);
    }

    public String getOriginal() {
        return getString(KEY_ORIGINAL);
    }

    public float getAmount() {
        try {
            return (float) getNumber(KEY_AMOUNT);
        } catch (ClassCastException e) {
            return (int) getNumber(KEY_AMOUNT);
        }
    }

    public String getUnit() {
        return getString(KEY_UNIT);
    }

    public String getIngredientId() {
        return getString(KEY_INGREDIENT_ID);
    }

    public void setRecipe(Recipe recipe) {
        put(KEY_RECIPE, recipe);
    }

    public void setAisle(String aisle) {
        put(KEY_AISLE, aisle);
    }

    public void setImage(String image) {
        put(KEY_IMAGE, image);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public void setNameClean(String name) {
        put(KEY_NAME_CLEAN, name);
    }

    public void setOriginal(String original) {
        put(KEY_ORIGINAL, original);
    }

    public void setAmount(float amount) {
        put(KEY_AMOUNT, amount);
    }

    public void setUnit(String unit) {
        put(KEY_UNIT, unit);
    }

    public void setIngredientId(String ingredientId) {
        put(KEY_INGREDIENT_ID, ingredientId);
    }
}
