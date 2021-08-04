package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mealplanner.R;
import com.example.mealplanner.SwipeToDeleteCallback;
import com.example.mealplanner.adapters.MealPlanAdapter;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;
import com.example.mealplanner.models.ShoppingList;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * This fragments allows the user to add recipes to a selected date
 */
public class WeekFragment extends Fragment implements AddRecipeFragment.AddRecipeListener,
        RecipeDetailsFragment.RecipeDetailsFragmentListener {

    private final static String TAG = "WeekFragment";
    private AddRecipeFragment addRecipeFragment;
    private RecipeDetailsFragment recipeDetailsFragment;

    private Calendar calendar;
    private List<MealPlan> mealPlans;
    private MealPlanAdapter adapter;
    private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private AppBarLayout appBarLayout;
    private TextView tvMonth;
    private TextView tvYear;
    private TextView tvDayNumber;
    private TextView tvDayName;
    private TextView tvNoPlan;
    private ImageButton ibtnPrevDay;
    private ImageButton ibtnNextDay;
    private ImageButton ibtnExpandCalendar;
    private RecyclerView rvRecipes;
    private FloatingActionButton fab;
    private ProgressBar progress_circular;
    private View view_current_day;
    private CalendarView calendarView;
    private FrameLayout savedRecipesContainer;
    private LottieAnimationView animationView;
    private LottieAnimationView animation_progress;

    private boolean calendarExpanded;

    public WeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();
        calendarExpanded = false;

        mealPlans = new ArrayList<>();

        appBarLayout = view.findViewById(R.id.main_appbar);
        tvMonth = view.findViewById(R.id.tvMonth);
        tvYear = view.findViewById(R.id.tvYear);
        tvDayNumber = view.findViewById(R.id.tvDayNumber);
        tvDayName = view.findViewById(R.id.tvDayName);
        ibtnPrevDay = view.findViewById(R.id.ibtnPrevDay);
        ibtnNextDay = view.findViewById(R.id.ibtnNextDay);
        ibtnExpandCalendar = view.findViewById(R.id.ibtnExpandCalendar);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        fab = view.findViewById(R.id.fab);
        progress_circular = view.findViewById(R.id.progress_circular);
        view_current_day = view.findViewById(R.id.view_current_day);
        calendarView = view.findViewById(R.id.calendarView);
        savedRecipesContainer = view.findViewById(R.id.savedRecipesContainer);
        tvNoPlan = view.findViewById(R.id.tvNoPlan);
        animationView = view.findViewById(R.id.animationView);
        animation_progress = view.findViewById(R.id.animation_progress);

        calendarView.setVisibility(GONE);
        savedRecipesContainer.setVisibility(GONE);

        adapter = new MealPlanAdapter(getContext(), mealPlans, new MealPlanAdapter.MealPlanAdapterListener() {
            @Override
            public void openDetails(IRecipe recipe, int index) {
                openRecipeDetailsFragment(recipe, index);
            }

            @Override
            public void updateShoppingList() {
                sendUpdateMessageToShoppingLists();
            }
        });

        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rvRecipes);

        setUpOnClickListeners();
        updateDateOnScreen();
        queryRecipesDay();
    }

    /**
     * Gets all the recipes tha are already on the selected day
     */
    private void queryRecipesDay() {
        progress_circular.setVisibility(GONE);
        animation_progress.setVisibility(View.VISIBLE);
        tvNoPlan.setVisibility(GONE);
        animationView.setVisibility(GONE);

        ParseQuery<MealPlan> query = ParseQuery.getQuery(MealPlan.class);

        query.include("recipe");

        query.whereEqualTo(MealPlan.KEY_USER, ParseUser.getCurrentUser());
        try {
            query.whereEqualTo(MealPlan.KEY_DATE, formatter.parse(formatter.format(calendar.getTime())));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        query.findInBackground(new FindCallback<MealPlan>() {
            @Override
            public void done(List<MealPlan> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting meal plan", e);
                    return;
                }
                Log.i(TAG, "Success getting the meal plan :D");
                mealPlans.clear();
                mealPlans.addAll(objects);
                progress_circular.setVisibility(GONE);
                animation_progress.setVisibility(GONE);
                if (mealPlans.size() == 0){
                    tvNoPlan.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                }else{
                    tvNoPlan.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Updates the recipe after the user changes the recipe from the calendar, this method calls
     * queryRecipeDay() to update the recipes
     */
    private void updateDateOnScreen() {
        tvYear.setText(calendar.get(Calendar.YEAR) + "");
        tvDayNumber.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
        tvDayName.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US) + "");
        tvMonth.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + "");


        if (isToday(calendar))
            view_current_day.setVisibility(View.VISIBLE);
        else
            view_current_day.setVisibility(View.INVISIBLE);

        queryRecipesDay();
    }

    /**
     * Checks if the selected date is today
     *
     * @param calendar selected date
     * @return
     */
    private boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }

    /**
     * changes the current date by n days
     *
     * @param n
     */
    private void changeDate(int n) {
        calendar.add(Calendar.DAY_OF_MONTH, n);
        updateDateOnScreen();
    }

    /**
     * opens the big calendar view
     */
    private void openCalendarView() {
        calendarExpanded = true;

        ibtnNextDay.setVisibility(GONE);
        ibtnPrevDay.setVisibility(GONE);
        tvDayName.setVisibility(GONE);
        tvDayNumber.setVisibility(GONE);
        tvMonth.setVisibility(GONE);
        tvYear.setVisibility(GONE);
        view_current_day.setVisibility(GONE);

        calendarView.setVisibility(View.VISIBLE);
    }

    /**
     * closes the big calendar view
     */
    private void closeCalendarView() {
        calendarExpanded = false;

        ibtnNextDay.setVisibility(View.VISIBLE);
        ibtnPrevDay.setVisibility(View.VISIBLE);
        tvDayName.setVisibility(View.VISIBLE);
        tvDayNumber.setVisibility(View.VISIBLE);
        tvMonth.setVisibility(View.VISIBLE);
        tvYear.setVisibility(View.VISIBLE);
        view_current_day.setVisibility(View.VISIBLE);

        calendarView.setVisibility(GONE);
    }

    /**
     * manages the big calendar, checks if the user wants to close or open the calendar based on
     * it's current state
     */
    private void manageCalendar() {
        if (calendarExpanded)
            closeCalendarView();
        else
            openCalendarView();
    }

    /**
     * open a list of saved recipes to add to the current date selected
     */
    private void openSavedRecipes() {
        savedRecipesContainer.setVisibility(View.VISIBLE);

        addRecipeFragment = AddRecipeFragment.newInstance(getRecipesFromMealPlan(mealPlans));

        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_up, R.anim.bottom_down)
                .replace(R.id.savedRecipesContainer, addRecipeFragment)
                .commit();
    }

    /**
     * get all the recipes from a list of mealPlans
     *
     * @param mealPlans
     * @return
     */
    private List<Recipe> getRecipesFromMealPlan(List<MealPlan> mealPlans) {
        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < mealPlans.size(); i++) {
            recipes.add((Recipe) mealPlans.get(i).getRecipe());
        }

        return recipes;
    }

    /**
     * opens the recipe details
     *
     * @param recipe
     * @param index
     */
    private void openRecipeDetailsFragment(IRecipe recipe, int index) {
        appBarLayout.setVisibility(View.GONE);
        recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipe, ParseUser.getCurrentUser());

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, recipeDetailsFragment).commit();
    }

    /**
     * closes the fragment
     */
    private void closeRecipeDetailsFragment() {
        appBarLayout.setVisibility(View.VISIBLE);

        getChildFragmentManager()
                .beginTransaction().remove(recipeDetailsFragment).commit();
    }

    private void setUpOnClickListeners() {
        ibtnExpandCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageCalendar();
            }
        });
        ibtnPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate(-1);
            }
        });
        ibtnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate(1);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSavedRecipes();
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                updateDateOnScreen();
            }
        });
    }

    @Override
    public void addRecipeToPlan(Recipe recipe) {
        tvNoPlan.setVisibility(GONE);
        try {
            MealPlan newMeal = MealPlan.createMealPlan(recipe, formatter.parse(formatter.format(calendar.getTime())));
            newMeal.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error while saving meal", e);
                    return;
                }
                Log.i(TAG, "Meal saved :D");
                mealPlans.add(0, newMeal);
                adapter.notifyItemInserted(0);

                sendUpdateMessageToShoppingLists();
            });
        } catch (java.text.ParseException e) {
            Log.e(TAG, "Error with the date", e);
        }
    }

    /**
     * This method gets all the shopping lists and updates the update message property
     */
    private void sendUpdateMessageToShoppingLists() {
        ParseQuery<ShoppingList> query = ParseQuery.getQuery(ShoppingList.class);

        query.whereEqualTo(ShoppingList.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ShoppingList>() {
            @Override
            public void done(List<ShoppingList> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while getting shopping lists", e);
                    return;
                }
                try {
                    updateAffectedShoppingLists(objects);
                } catch (java.text.ParseException parseException) {
                    Log.e(TAG, "Error while updating affected shopping lists");
                }
            }
        });
    }

    /**
     * This method checks witch recipes are affected by the change and updates them in the database
     * @param shoppingLists
     * @throws java.text.ParseException
     */
    private void updateAffectedShoppingLists(List<ShoppingList> shoppingLists) throws java.text.ParseException {
        List<ShoppingList> affectedShoppingList = new ArrayList<>();
        for (ShoppingList shoppingList : shoppingLists) {
            Date currentDate = formatter.parse(formatter.format(calendar.getTime()));
            if (shoppingList.getStartDate().compareTo(currentDate) * currentDate.compareTo(shoppingList.getEndDate()) >= 0) {
                shoppingList.setUpdateMessage(true);
                affectedShoppingList.add(shoppingList);
            }
        }
        ParseObject.saveAllInBackground(affectedShoppingList);
    }

    @Override
    public void backButtonPressed() {
        closeRecipeDetailsFragment();
    }

    @Override
    public void updateRecipeList() {

    }
}