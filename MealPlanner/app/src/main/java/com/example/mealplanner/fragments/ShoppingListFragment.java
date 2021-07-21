package com.example.mealplanner.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.asynchttpclient.AbsCallback;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealplanner.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CancellationException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;


public class ShoppingListFragment extends Fragment {

    private final static String TAG = "ShoppingListFragment";
    private final static String COMPUTE_SHOPPING_LIST_URL = "https://api.spoonacular.com/mealplanner/shopping-list/compute?apiKey=728721c3da7543769d5413b35ac70cd7";

    private AsyncHttpClient client;
    private Calendar startDayCalendar;
    private Calendar endDayCalendar;
    private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private MaterialDatePicker dateRangePicker;
    private TextView tvTest;
    private RecyclerView rvShoppingList;
    private TextView tvDateRange;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startDayCalendar = Calendar.getInstance();
        endDayCalendar = Calendar.getInstance();

        tvTest = view.findViewById(R.id.tvTest);
        tvDateRange = view.findViewById(R.id.tvDateRange);
        rvShoppingList = view.findViewById(R.id.rvShoppingList);

        setUpDateRangePicker();
        setUpOnClickListeners();
    }

    private void setUpOnClickListeners() {
        tvDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRangePicker.show(getChildFragmentManager(), TAG);
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


    private String getCalendarDate(Calendar calendar) {
        String res = "";

        res += calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
        res += " " + calendar.get(Calendar.DAY_OF_MONTH);
        res += " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        res += " " + calendar.get(Calendar.YEAR);

        return res;
    }

    private void TEST() {
        client = new AsyncHttpClient();
        String body = "{items: [4 lbs tomatoes,10 tomatoes,20 Tablespoons Olive Oil,6 tbsp Olive Oil]}";
        client.post(COMPUTE_SHOPPING_LIST_URL, body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.i(TAG, "YEII");
                try {
                    JSONArray jsonArray = json.jsonObject.getJSONArray("aisles");
                    Log.i(TAG, json.jsonObject.toString());
                } catch (JSONException e) {
                    Log.e(TAG, "NO jasonArray", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e(TAG, "NOOO", throwable);
            }
        });

    }
}