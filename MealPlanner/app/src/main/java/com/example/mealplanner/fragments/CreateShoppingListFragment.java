package com.example.mealplanner.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mealplanner.R;
import com.example.mealplanner.ShoppingListCreator;
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

/**
 * This fragment allows the user to select a range of dates and generates a shopping list
 * from that range of dates
 */
public class CreateShoppingListFragment extends DialogFragment {

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
        View view = inflater.inflate(R.layout.fragment_create_shopping_list, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
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

    /**
     * Sets up the onClickListeners
     */
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

    /**
     * Configures the date range picker
     */
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

    /**
     * The date range pickers returns the date that the user selected - 1 day, and this method
     * fixes that adding 1 to the selected dates
     *
     * @param startDate
     * @param endDate
     */
    private void updateDateRange(Long startDate, Long endDate) {
        startDayCalendar.setTimeInMillis(startDate);
        startDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
        endDayCalendar.setTimeInMillis(endDate);
        endDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * opens the date picker
     */
    private void openDatePicker() {
        dateRangePicker.show(getChildFragmentManager(), TAG);
    }

    /**
     * Creates the shopping list from the the range of dates selected
     *
     * @throws ParseException
     */
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

                ShoppingListCreator creator = new ShoppingListCreator(new ShoppingListCreator.ShoppingListCreatorListener() {
                    @Override
                    public void shoppingListItemsCreated() {
                        progressBar.setVisibility(View.GONE);
                        listener.shoppingListCreated(createdShoppingList);
                    }
                });

                creator.createShoppingListItems(createdShoppingList);
            }
        });
    }
}