package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mealplanner.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;


public class WeekFragment extends Fragment {

    private final static String TAG = "WeekFragment";
    private final AddRecipeFragment addRecipeFragment = new AddRecipeFragment();

    private Calendar calendar;

    private AppBarLayout appBarLayout;
    private TextView tvMonth;
    private TextView tvYear;
    private TextView tvDayNumber;
    private TextView tvDayName;
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

        calendarView.setVisibility(GONE);
        progress_circular.setVisibility(GONE);
        savedRecipesContainer.setVisibility(GONE);

        setUpOnClickListeners();
        updateDateOnScreen();
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

    private void updateDate(long date) {
        calendar.setTime(new Date(date));
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
                //Date date = new Date(year,month,dayOfMonth);
                //updateDate(view.getDate());
            }
        });
    }
}