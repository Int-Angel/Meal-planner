package com.example.mealplanner.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mealplanner.R;
import com.example.mealplanner.models.Ingredient;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.Recipe;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class CreateShoppingListFragment extends Fragment {

    public interface CreateShoppingListFragmentListener {
        void closeCreateShoppingListFragment();

        void shoppingListCreated(ShoppingList shoppingList);
    }

    private final static String TAG = "CreateShoppingList";
    private final static String COMPUTE_SHOPPING_LIST_URL = "https://api.spoonacular.com/mealplanner/shopping-list/compute?apiKey=728721c3da7543769d5413b35ac70cd7";

    private CreateShoppingListFragmentListener listener;

    private List<MealPlan> mealPlans;
    private List<Ingredient> ingredients;
    private ShoppingList createdShoppingList;

    private AsyncHttpClient client;
    private Calendar startDayCalendar;
    private Calendar endDayCalendar;
    private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private MaterialDatePicker dateRangePicker;
    private ImageButton ibtnClose;
    private EditText etShoppingListName;
    private TextView tvDateRange;
    private Button btnDone;
    private ProgressBar progressBar;

    public CreateShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = (CreateShoppingListFragmentListener) getParentFragment();

        startDayCalendar = Calendar.getInstance();
        endDayCalendar = Calendar.getInstance();

        mealPlans = new ArrayList<>();
        ingredients = new ArrayList<>();

        ibtnClose = view.findViewById(R.id.ibtnClose);
        etShoppingListName = view.findViewById(R.id.etShoppingListName);
        tvDateRange = view.findViewById(R.id.tvDateRange);
        btnDone = view.findViewById(R.id.btnDone);
        progressBar = view.findViewById(R.id.progress_circular);

        setUpOnClickListeners();
        setUpDateRangePicker();
    }

    private void setUpOnClickListeners() {
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeCreateShoppingListFragment();
            }
        });

        tvDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createShoppingList();
                } catch (ParseException e) {
                    Log.e(TAG, "Parse exceptions creating shopping list", e);
                }
            }
        });
    }

    private void setUpDateRangePicker() {
        dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select dates")
                        .setSelection(
                                new Pair<>(
                                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                        MaterialDatePicker.todayInUtcMilliseconds()
                                )
                        )
                        .build();

        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                tvDateRange.setText(dateRangePicker.getHeaderText());
                updateDateRange(selection.first, selection.second);
            }
        });

        dateRangePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    private void updateDateRange(Long startDate, Long endDate) {
        startDayCalendar.setTimeInMillis(startDate);
        startDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
        endDayCalendar.setTimeInMillis(endDate);
        endDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    private void openDatePicker() {
        dateRangePicker.show(getChildFragmentManager(), TAG);
    }

    private void createShoppingList() throws ParseException {
        progressBar.setVisibility(View.VISIBLE);

        createdShoppingList = ShoppingList.createShoppingList(etShoppingListName.getText().toString(),
                formatter.parse(formatter.format(startDayCalendar.getTime())),
                formatter.parse(formatter.format(endDayCalendar.getTime())));

        createdShoppingList.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't save shopping list", e);
                    return;
                }
                Log.i(TAG, "Shopping list created");
                if (createdShoppingList.getObjectId() == null) {
                    Log.e(TAG, "Created shopping list id NULL");
                    return;
                }

                createShoppingListItems(createdShoppingList);
            }
        });
    }

    private void createShoppingListItems(ShoppingList shoppingList) {

        Date current = shoppingList.getStartDate();
        List<Date> dates = new ArrayList<>();

        while (!current.after(shoppingList.getEndDate())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);

            dates.add(current);

            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();
        }

        queryMealPlanDays(dates);
    }

    private void queryMealPlanDays(List<Date> dates) {
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
                mealPlans.clear();
                mealPlans.addAll(objects);

                getAllRecipesFromMealPlans();
            }
        });
    }


    private void getAllRecipesFromMealPlans() {
        HashMap<Integer, RecipeQuantity> recipeQuantityHashMap = new HashMap<>();

        for (int i = 0; i < mealPlans.size(); i++) {

            Recipe recipe = (Recipe) mealPlans.get(i).getRecipe();
            int id = Integer.parseInt(recipe.getId());

            if (recipeQuantityHashMap.containsKey(id)) {
                recipeQuantityHashMap.get(id).addQuantity(mealPlans.get(i).getQuantity());
            } else {
                RecipeQuantity recipeQuantity = new RecipeQuantity(recipe, mealPlans.get(i).getQuantity());
                recipeQuantityHashMap.put(id, recipeQuantity);
            }
        }

        getAllIngredientsFromRecipes(recipeQuantityHashMap);
    }

    private void getAllIngredientsFromRecipes(HashMap<Integer, RecipeQuantity> recipeQuantityHashMap) {

        List<RecipeQuantity> recipeQuantities = new ArrayList<>(recipeQuantityHashMap.values());
        List<Recipe> recipes = RecipeQuantity.getListOfRecipes(recipeQuantities);
        List<ParseQuery<Ingredient>> queries = new ArrayList<>();

        if(recipes.size() == 0){
            progressBar.setVisibility(View.GONE);
            listener.shoppingListCreated(createdShoppingList);
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
                ingredients.addAll(objects);

                updateIngredientsAmount(recipeQuantityHashMap);

            }
        });
    }

    private void updateIngredientsAmount(HashMap<Integer, RecipeQuantity> recipeQuantityHashMap) {

        for(Ingredient ingredient: ingredients){
            Recipe recipe = (Recipe) ingredient.getRecipe();
            int id = Integer.parseInt(recipe.getId());

            if(recipeQuantityHashMap.containsKey(id)){
                int amount = recipeQuantityHashMap.get(id).getQuantity();
                ingredient.setAmount(ingredient.getAmount() * amount * 1.0f);
            }else{
                Log.e(TAG,"Ingredient without recipe");
            }
        }

        generateShoppingListFromIngredients();
    }

    private void generateShoppingListFromIngredients() {

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

                progressBar.setVisibility(View.GONE);

                listener.shoppingListCreated(createdShoppingList);
            }
        });
    }


    static class RecipeQuantity {
        private Recipe recipe;
        private int quantity;

        public static List<Recipe> getListOfRecipes(List<RecipeQuantity> recipeQuantities) {
            List<Recipe> recipes = new ArrayList<>();

            for (RecipeQuantity recipeQuantity : recipeQuantities) {
                recipes.add(recipeQuantity.getRecipe());
            }

            return recipes;
        }

        public RecipeQuantity(Recipe recipe, int quantity) {
            this.recipe = recipe;
            this.quantity = quantity;
        }

        public void addQuantity(int n) {
            quantity += n;
        }

        public int getQuantity() {
            return quantity;
        }

        public Recipe getRecipe() {
            return recipe;
        }
    }
}