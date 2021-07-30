package com.example.mealplanner;

import android.util.Log;

import com.example.mealplanner.models.Ingredient;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SavedRecipesManager manages the saved recipes from the user, it can read and save new recipes
 */
//TODO: change to ModelView
public class SavedRecipesManager {

    private final static String TAG = "SavedRecipesManager";

    private static List<Recipe> recipes;
    private static Set<String> idSet; // a set that contains all the id recipes

    static {
        recipes = new ArrayList<>();
        idSet = new HashSet<>();
    }

    /**
     * This method gets called in the main activity onCreate, and queries all the saved recipes
     * from the current user and generates the idSet to check witch recipes are already saved
     * in the online recipes fragment
     */
    public static void querySavedRecipes() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);

        query.whereEqualTo(Recipe.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Fail getting recipes", e);
                    return;
                }
                recipes.clear();
                recipes.addAll(objects);
                createIdSet();
            }
        });
    }

    /**
     * This method creates the idSet from the recipe list
     */
    private static void createIdSet() {
        idSet.clear();
        for (int i = 0; i < recipes.size(); i++) {
            idSet.add(recipes.get(i).getId());
        }
    }

    /**
     * Returns the recipe that has that id
     *
     * @param id recipe id
     * @return
     */
    public static Recipe getRecipeById(String id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId().equals(id))
                return recipes.get(i);
        }
        return null;
    }

    /**
     * Returns all the saved recipes
     *
     * @return
     */
    public static List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Returns the id set
     *
     * @return
     */
    public static Set<String> getIdSet() {
        return idSet;
    }

    /**
     * Creates a new saved recipe from an online recipe and saves it in the database
     *
     * @param onlineRecipe online recipe that will be used to create a saved recipe
     */
    public static void saveRecipe(OnlineRecipe onlineRecipe) {
        Recipe recipe = Recipe.createRecipe(onlineRecipe);
        recipes.add(0, recipe);
        idSet.add(recipe.getId());

        createRecipeIngredients(recipe, onlineRecipe);

        recipe.saveInBackground(new com.parse.SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving recipe!", e);
                    return;
                }
                Log.i(TAG, "Recipe saved!");
            }
        });
    }

    /**
     * Add a recipe to the list and id set
     *
     * @param recipe
     */
    public static void addRecipe(Recipe recipe) {
        recipes.add(0, recipe);
        idSet.add(recipe.getId());
    }

    /**
     * Creates all the ingredients of a recipe from an online recipe and saved them in the database
     *
     * @param recipe       it's the recipe that owns the ingredients, will be used as reference
     * @param onlineRecipe it's the online recipe that provides the ingredients list
     */
    private static void createRecipeIngredients(Recipe recipe, OnlineRecipe onlineRecipe) {
        try {
            List<Ingredient> ingredients = Ingredient.fromJSONArray(recipe, onlineRecipe.getExtendedIngredients());

            ParseObject.saveAllInBackground(ingredients, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Couldn't save ingredients in database", e);
                        return;
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Couldn't create ingredients from recipe", e);
        }
    }

    /**
     * removes a recipe from the list of recipes that has that id
     *
     * @param id id recipe from the recipe to be removed
     */
    private static void deleteRecipeFromListById(String id) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId().equals(id)) {
                idSet.remove(recipes.get(i).getId());
                recipes.remove(i);
                return;
            }
        }
    }

    /**
     * Deletes a recipe from the database using it's id
     *
     * @param id
     */
    public static void unSaveRecipeById(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
        query.whereEqualTo(Recipe.KEY_ID, id);
        deleteRecipeFromListById(id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Can't Unsave recipe because it's not saved", e);
                    return;
                }

                deleteMealPlansWithRecipe((Recipe) object);
                deleteIngredientsWithRecipe((Recipe) object);

                object.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while unSaving recipe", e);
                            return;
                        }
                        Log.i(TAG, "Recipe unSaved!");
                    }
                });
            }
        });
    }

    /**
     * Deletes all the meal plans in the database with that recipe
     *
     * @param recipe
     */
    private static void deleteMealPlansWithRecipe(Recipe recipe) {
        ParseQuery<MealPlan> query = ParseQuery.getQuery("Week");
        query.whereEqualTo(MealPlan.KEY_RECIPE, recipe);
        query.findInBackground(new FindCallback<MealPlan>() {
            @Override
            public void done(List<MealPlan> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error removing meal plan from recipe", e);
                    return;
                }
                Log.i(TAG, "Removing meal plan: " + objects.size());
                ParseObject.deleteAllInBackground(objects);
            }
        });
    }

    /**
     * Deletes all the ingredients in the database with reference to that recipe
     *
     * @param recipe
     */
    private static void deleteIngredientsWithRecipe(Recipe recipe) {
        ParseQuery<Ingredient> query = ParseQuery.getQuery("Ingredient");
        query.whereEqualTo(Ingredient.KEY_RECIPE, recipe);
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error removing all ingredients from recipe", e);
                    return;
                }
                Log.i(TAG, "Removing ingredients: " + objects.size());
                ParseObject.deleteAllInBackground(objects);
            }
        });
    }

    /**
     * Checks if a recipe is already saved in the database
     *
     * @param idRecipe id of the recipe to check
     * @return returns true if the recipe it's saved and false otherwise
     */
    public static boolean checkIfRecipeIsSaved(String idRecipe) {
        return idSet.contains(idRecipe);
    }
}
