package com.example.mealplanner;

import android.util.Log;
import android.view.View;

import com.example.mealplanner.fragments.CreateShoppingListFragment;
import com.example.mealplanner.models.Ingredient;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.Recipe;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This class helps to create shopping list and update the items in the database
 */
public class ShoppingListCreator {

    public interface  ShoppingListCreatorListener{
        void shoppingListItemsCreated();
    }
    private final static String TAG = "ShoppingListCreator";
    private  DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private  ShoppingList createdShoppingList;
    private  ShoppingListCreatorListener listener;

    public ShoppingListCreator(ShoppingListCreatorListener listener){
        this.listener = listener;
    }

    /**
     * Gets all the dates to generate all the shopping list items from the date range
     *
     * @param shoppingList
     */
    public void createShoppingListItems(ShoppingList shoppingList) {

        Date current = shoppingList.getStartDate();
        List<Date> dates = new ArrayList<>();

        createdShoppingList = shoppingList;

        while (!current.after(shoppingList.getEndDate())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);

            dates.add(current);

            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();
        }

        queryMealPlanDays(dates);
    }

    /**
     * Gets all the meal plans from a list of dates, from the database
     *
     * @param dates
     */
    private  void queryMealPlanDays(List<Date> dates) {
        List<ParseQuery<MealPlan>> queries = new ArrayList<>();
        try {
            for (int i = 0; i < dates.size(); i++) {
                ParseQuery<MealPlan> query = ParseQuery.getQuery(MealPlan.class);
                query.whereEqualTo(MealPlan.KEY_DATE, formatter.parse(formatter.format(dates.get(i))));
                queries.add(query);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<MealPlan> mainQuery = ParseQuery.or(queries);
        mainQuery.include(MealPlan.KEY_RECIPE);
        mainQuery.whereEqualTo(MealPlan.KEY_USER, ParseUser.getCurrentUser());
        mainQuery.findInBackground(new FindCallback<MealPlan>() {
            @Override
            public void done(List<MealPlan> objects, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting meal plan", e);
                    return;
                }
                Log.i(TAG, "Success getting the meal plan :D");

                List<MealPlan> mealPlans = new ArrayList<>();
                mealPlans.addAll(objects);

                getAllRecipesFromMealPlans(mealPlans);
            }
        });
    }

    /**
     * Gets all the recipes from the meal plans
     */
    private  void getAllRecipesFromMealPlans(List<MealPlan> mealPlans) {
        HashMap<Integer, CreateShoppingListFragment.RecipeQuantity> recipeQuantityHashMap = new HashMap<>();

        for (int i = 0; i < mealPlans.size(); i++) {

            Recipe recipe = (Recipe) mealPlans.get(i).getRecipe();
            int id = Integer.parseInt(recipe.getId());

            if (recipeQuantityHashMap.containsKey(id)) {
                recipeQuantityHashMap.get(id).addQuantity(mealPlans.get(i).getQuantity());
            } else {
                CreateShoppingListFragment.RecipeQuantity recipeQuantity = new CreateShoppingListFragment.RecipeQuantity(recipe, mealPlans.get(i).getQuantity());
                recipeQuantityHashMap.put(id, recipeQuantity);
            }
        }

        getAllIngredientsFromRecipes(recipeQuantityHashMap);
    }

    /**
     * Gets all the ingredients from all the recipes
     *
     * @param recipeQuantityHashMap
     */
    private  void getAllIngredientsFromRecipes(HashMap<Integer, CreateShoppingListFragment.RecipeQuantity> recipeQuantityHashMap) {

        List<CreateShoppingListFragment.RecipeQuantity> recipeQuantities = new ArrayList<>(recipeQuantityHashMap.values());
        List<Recipe> recipes = CreateShoppingListFragment.RecipeQuantity.getListOfRecipes(recipeQuantities);
        List<ParseQuery<Ingredient>> queries = new ArrayList<>();

        if (recipes.size() == 0) {
            return;
        }

        for (int i = 0; i < recipes.size(); i++) {
            ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
            query.whereEqualTo(Ingredient.KEY_RECIPE, recipes.get(i));
            queries.add(query);
        }

        ParseQuery<Ingredient> mainQuery = ParseQuery.or(queries);
        mainQuery.include(Ingredient.KEY_RECIPE);
        mainQuery.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting ingredients from Parse", e);
                    return;
                }
                Log.i(TAG, "Ingredients :D");
                List<Ingredient> ingredients = new ArrayList<>();
                ingredients.addAll(objects);
                updateIngredientsAmount(recipeQuantityHashMap, ingredients);

            }
        });
    }

    /**
     * It multiplies the amount of a ingredient by the quantity of recipes of that day
     * (example 3 chicken tacos, multiplies 3 times all the chicken tacos ingredients)
     *
     * @param recipeQuantityHashMap
     */
    private  void updateIngredientsAmount(HashMap<Integer, CreateShoppingListFragment.RecipeQuantity> recipeQuantityHashMap, List<Ingredient> ingredients) {

        for (Ingredient ingredient : ingredients) {
            Recipe recipe = (Recipe) ingredient.getRecipe();
            int id = Integer.parseInt(recipe.getId());

            if (recipeQuantityHashMap.containsKey(id)) {
                int amount = recipeQuantityHashMap.get(id).getQuantity();
                ingredient.setAmount(ingredient.getAmount() * amount * 1.0f);
            } else {
                Log.e(TAG, "Ingredient without recipe");
            }
        }

        generateShoppingListFromIngredients(ingredients);
    }

    /**
     * Generates all the shopping list items and saves them in the database
     */
    private void generateShoppingListFromIngredients(List<Ingredient> ingredients) {

        HashMap<Integer, ShoppingListItem> items = new HashMap<>();

        if (createdShoppingList.getObjectId() == null) {
            Log.e(TAG, "Created shopping list id NULL 2");
            return;
        }

        for (Ingredient ingredient : ingredients) {
            if (items.containsKey(Integer.parseInt(ingredient.getIngredientId()))) {
                items.get(Integer.parseInt(ingredient.getIngredientId())).addAmount(ingredient.getAmount());
            } else {
                ShoppingListItem shoppingListItem = ShoppingListItem.createShoppingListItem(createdShoppingList, ingredient);
                items.put(Integer.parseInt(ingredient.getIngredientId()), shoppingListItem);
            }
        }

        List<ShoppingListItem> shoppingListItems = new ArrayList<>(items.values());
        ParseObject.saveAllInBackground(shoppingListItems, new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving shopping list items", e);
                    return;
                }
                Log.i(TAG, "Shopping list items saved :D");
                listener.shoppingListItemsCreated();
            }
        });
    }

}
