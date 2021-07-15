package com.example.mealplanner;

import android.util.Log;
import android.widget.Toast;

import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavedRecipesManager {

    private final static String TAG = "SavedRecipesManager";

    private static List<Recipe> recipes;
    private static Set<String> uriSet;

    static {
        recipes = new ArrayList<>();
        uriSet = new HashSet<>();
    }

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
                recipes.addAll(objects);
                createUriSet();
            }
        });
    }

    private static void createUriSet() {
        for (int i = 0; i < recipes.size(); i++) {
            uriSet.add(recipes.get(i).getUri());
        }
    }

    public static List<Recipe> getRecipes() {
        return recipes;
    }

    public static Set<String> getUriSet() {
        return uriSet;
    }

    public static void saveRecipe(OnlineRecipe onlineRecipe) {
        Recipe recipe = Recipe.createRecipe(onlineRecipe);
        recipes.add(0, recipe);
        uriSet.add(recipe.getUri());

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

    private static void deleteRecipeFromListByUri(String uri){
        for(int i = 0; i< recipes.size(); i++){
            if(recipes.get(i).getUri().equals(uri)){
                uriSet.remove(recipes.get(i).getUri());
                recipes.remove(i);
                return;
            }
        }
    }

    public static void unSaveRecipeByUri(String uri) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
        query.whereEqualTo(Recipe.KEY_URI, uri);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Can't Unsave recipe because it's not saved", e);
                    return;
                }
                object.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while unSaving recipe", e);
                            return;
                        }
                        Log.i(TAG, "Recipe unSaved!");
                        //querySavedRecipes();
                        deleteRecipeFromListByUri(uri);
                    }
                });
            }
        });
    }

    public static void unSaveRecipe(int index) {
        Recipe recipe = recipes.get(index);
        uriSet.remove(recipes.get(index).getUri());
        recipes.remove(index);
        recipe.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while deleting recipe: " + index, e);
                    return;
                }
            }
        });
    }


}
