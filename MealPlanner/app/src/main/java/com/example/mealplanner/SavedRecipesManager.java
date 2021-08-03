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
public class SavedRecipesManager {

    /**
     * Interface to communicate that the recipe has been saved
     */
    public interface SavedRecipesManagerListener {
        void recipeSaved(Recipe newRecipe);
    }

    private final static String TAG = "SavedRecipesManager";

    private static SavedRecipesManager instance;

    private List<Recipe> recipes;
    private Set<String> idSet; // a set that contains all the id recipes

    /**
     * Private constructor because it's a singleton
     */
    private SavedRecipesManager() {
        recipes = new ArrayList<>();
        idSet = new HashSet<>();

        querySavedRecipes();
    }

    /**
     * Returns the instance of this saved recipes manager
     *
     * @return
     */
    public static SavedRecipesManager getInstance() {
        if (instance == null)
            instance = new SavedRecipesManager();

        return instance;
    }

    /**
     * Clear the instance of the saved recipes manager
     */
    public static void clear() {
        instance = null;
    }

    /**
     * This method gets called in the main activity onCreate, and queries all the saved recipes
     * from the current user and generates the idSet to check witch recipes are already saved
     * in the online recipes fragment
     */
    public void querySavedRecipes() {
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
    private void createIdSet() {
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
    public Recipe getRecipeById(String id) {
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
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Returns the id set
     *
     * @return
     */
    public Set<String> getIdSet() {
        return idSet;
    }

    /**
     * Creates a new saved recipe from an online recipe and saves it in the database
     *
     * @param onlineRecipe online recipe that will be used to create a saved recipe
     */
    public void saveRecipe(OnlineRecipe onlineRecipe) {
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
    public void addRecipe(Recipe recipe) {
        recipes.add(0, recipe);
        idSet.add(recipe.getId());
    }

    /**
     * Creates all the ingredients of a recipe from an online recipe and saved them in the database
     *
     * @param recipe       it's the recipe that owns the ingredients, will be used as reference
     * @param onlineRecipe it's the online recipe that provides the ingredients list
     */
    private void createRecipeIngredients(Recipe recipe, OnlineRecipe onlineRecipe) {
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
    private void deleteRecipeFromListById(String id) {
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
    public void unSaveRecipeById(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
        query.whereEqualTo(Recipe.KEY_ID, id);
        query.whereEqualTo(Recipe.KEY_USER, ParseUser.getCurrentUser());
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
    private void deleteMealPlansWithRecipe(Recipe recipe) {
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
    private void deleteIngredientsWithRecipe(Recipe recipe) {
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
     * Copies a already saved recipe from other user to into the saved recipes of the current user
     *
     * @param newRecipe
     * @param listener
     */
    public void copyRecipeToCurrentUser(Recipe newRecipe, SavedRecipesManagerListener listener) {
        Recipe recipe = Recipe.createRecipe(newRecipe);
        recipes.add(0, recipe);
        idSet.add(recipe.getId());

        createRecipeIngredientsFromAlreadySavedRecipe(recipe, newRecipe);

        recipe.saveInBackground(new com.parse.SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving recipe!", e);
                    return;
                }
                Log.i(TAG, "Recipe saved!");
                listener.recipeSaved(recipe);
            }
        });
    }


    /**
     * Generates a list of ingredients from a already saved recipe
     *
     * @param newRecipe new recipe
     * @param oldRecipe already saved recipe
     */
    private void createRecipeIngredientsFromAlreadySavedRecipe(Recipe newRecipe, Recipe oldRecipe) {
        ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
        query.whereEqualTo(Ingredient.KEY_RECIPE, oldRecipe);
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting recipes from already saved recipe", e);
                    return;
                }
                List<Ingredient> newIngredients = new ArrayList<>(objects);
                for (Ingredient ingredient : newIngredients) {
                    ingredient.setRecipe(newRecipe);
                }
                ParseObject.saveAllInBackground(newIngredients);
            }
        });
    }

    /**
     * Checks if a recipe is already saved in the database
     *
     * @param idRecipe id of the recipe to check
     * @return returns true if the recipe it's saved and false otherwise
     */
    public boolean checkIfRecipeIsSaved(String idRecipe) {
        return idSet.contains(idRecipe);
    }
}
