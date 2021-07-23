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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealplanner.R;
import com.example.mealplanner.models.Ingredient;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.Recipe;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;


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

        queryMealsPlanDay(dates);
    }

    private void queryMealsPlanDay(List<Date> dates) {
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
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < mealPlans.size(); i++) {
            Recipe recipe = (Recipe) mealPlans.get(i).getRecipe();
            recipes.add(recipe);
        }

        getAllIngredientsFromRecipes(recipes);
    }

    private void getAllIngredientsFromRecipes(List<Recipe> recipes) {

        List<ParseQuery<Ingredient>> queries = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            ParseQuery<Ingredient> query = ParseQuery.getQuery(Ingredient.class);
            query.whereEqualTo(Ingredient.KEY_RECIPE, recipes.get(i));
            queries.add(query);
        }

        ParseQuery<Ingredient> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> objects, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting ingredients from Parse", e);
                    return;
                }
                Log.i(TAG, "Ingredients :D");
                ingredients.addAll(objects);

                generateShoppingListFromIngredients();
                //computeShoppingList(generateJSONArrayFromIngredients());
            }
        });
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
                listener.shoppingListCreated(createdShoppingList);
            }
        });
    }


    /*
     *
     * API doesn't work
     *
     * */
    private JSONArray generateJSONArrayFromIngredients() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ingredients.size(); i++) {
            String s = "" + ingredients.get(i).getAmount() + " " + ingredients.get(i).getUnit() + " " + ingredients.get(i).getNameClean();
            jsonArray.put(s);
        }
        return jsonArray;
    }


    private void computeShoppingList(JSONArray jsonArray) {
        client = new AsyncHttpClient();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("items", jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, "Cant create parameters", e);
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Cant create entity", e);
        }

        client.post(getContext(), COMPUTE_SHOPPING_LIST_URL, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                } catch (JSONException e) {
                    Log.e(TAG, "Couldn't create JSONobject from shopping list response", e);
                }
                Log.i(TAG, "onSuccess getting shopping list: " + res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String res = new String(responseBody);
                Log.e(TAG, "onFailure getting shopping list statusCode:" + statusCode + " Headers: " + headers.toString() + " Res: " + res, error);
            }
        });
    }


}