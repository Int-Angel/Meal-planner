package com.example.mealplanner.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a ingredient, it's used to generate the shopping list
 */
@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {

    /**
     * All the database keys
     */
    public final static String KEY_RECIPE = "recipe"; // recipe that owns this ingredients
    public final static String KEY_AISLE = "aisle";
    public final static String KEY_IMAGE = "image";
    public final static String KEY_NAME = "name";
    public final static String KEY_NAME_CLEAN = "nameClean";
    public final static String KEY_ORIGINAL = "original";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_UNIT = "unit";
    public final static String KEY_INGREDIENT_ID = "ingredientId";

    /**
     * Creates an ingredient based on a jsonObject and a Recipe
     *
     * @param recipe     recipe that owns the ingredient
     * @param jsonObject jsonObject that contains the ingredient
     * @return returns the ingredient form the jsonObject
     * @throws JSONException
     */
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

    /**
     * Returns a list of ingredients that are contained inside a jsonArray
     *
     * @param recipe    recipe that owns the ingredients
     * @param jsonArray jsonArray that contains the ingredients
     * @return returns a list of ingredients
     * @throws JSONException
     */
    public static List<Ingredient> fromJSONArray(Recipe recipe, JSONArray jsonArray) throws JSONException {
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ingredients.add(createIngredient(recipe, jsonArray.getJSONObject(i)));
        }

        return ingredients;
    }

    /**
     * This method creates a ingredients from a different jsonObject, it's used when the user
     * creates his own recipe
     *
     * @param jsonObject jsonObject with the ingredient information
     * @return returns the ingredient
     * @throws JSONException
     */
    public static Ingredient createIngredientFromAPI(JSONObject jsonObject) throws JSONException {
        Ingredient ingredient = new Ingredient();

        ingredient.setAisle(jsonObject.getString("aisle"));
        ingredient.setImage(jsonObject.getString("image"));
        ingredient.setName(jsonObject.getString("name"));
        ingredient.setNameClean(jsonObject.getString("originalName"));
        ingredient.setOriginal(jsonObject.getString("original"));

        ingredient.setAmount(jsonObject.getLong("amount"));
        ingredient.setUnit(jsonObject.getString("unitShort"));
        ingredient.setIngredientId(jsonObject.getString("id"));

        return ingredient;
    }

    /**
     * Generates a list of ingredients from a different jsonArray,, it's used when the user
     * creates his own recipe
     *
     * @param jsonArray jsonArray with the ingredients information
     * @return returns a list of ingredients
     * @throws JSONException
     */
    public static List<Ingredient> fromJSONArrayFromAPI(JSONArray jsonArray) throws JSONException {
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ingredients.add(createIngredientFromAPI(jsonArray.getJSONObject(i)));
        }

        return ingredients;
    }

    /**
     * Getters and setters
     */

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
