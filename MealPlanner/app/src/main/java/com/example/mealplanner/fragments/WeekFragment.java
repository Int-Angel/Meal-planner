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

import com.example.mealplanner.R;
import com.example.mealplanner.SwipeToDeleteCallback;
import com.example.mealplanner.adapters.MealPlanAdapter;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;
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


public class WeekFragment extends Fragment implements AddRecipeFragment.AddRecipeListener,
        RecipeDetailsFragment.RecipeDetailsFragmentListener {

    private final static String TAG = "WeekFragment";
    private final AddRecipeFragment addRecipeFragment = new AddRecipeFragment();
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

        calendarView.setVisibility(GONE);
        savedRecipesContainer.setVisibility(GONE);

        adapter = new MealPlanAdapter(getContext(), mealPlans, new MealPlanAdapter.MealPlanAdapterListener() {
            @Override
            public void openDetails(IRecipe recipe, int index) {
                openRecipeDetailsFragment(recipe, index);
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

    private void queryRecipesDay() {
        progress_circular.setVisibility(View.VISIBLE);
        tvNoPlan.setVisibility(GONE);

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
                if (mealPlans.size() == 0)
                    tvNoPlan.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();
            }
        });
    }

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

    private boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }

    private void changeDate(int n) {
        calendar.add(Calendar.DAY_OF_MONTH, n);
        updateDateOnScreen();
    }

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

    private void manageCalendar() {
        if (calendarExpanded)
            closeCalendarView();
        else
            openCalendarView();
    }

    private void openSavedRecipes() {
        savedRecipesContainer.setVisibility(View.VISIBLE);
        getChildFragmentManager()
                .beginTransaction().replace(R.id.savedRecipesContainer, addRecipeFragment).commit();
    }

    private void openRecipeDetailsFragment(IRecipe recipe, int index) {
        appBarLayout.setVisibility(View.GONE);
        recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipe, index);

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, recipeDetailsFragment).commit();
    }

    private void closeRecipeDetailsFragment(){
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
            });
        } catch (java.text.ParseException e) {
            Log.e(TAG, "Error with the date", e);
        }
    }

    @Override
    public void backButtonPressed() {
        closeRecipeDetailsFragment();
    }

    @Override
    public void updateRecipeList() {

    }
}