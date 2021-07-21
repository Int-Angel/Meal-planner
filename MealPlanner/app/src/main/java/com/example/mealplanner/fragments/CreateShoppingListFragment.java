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

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.example.mealplanner.R;
import com.example.mealplanner.models.ShoppingList;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CreateShoppingListFragment extends Fragment {

    public interface CreateShoppingListFragmentListener{
        void closeCreateShoppingListFragment();
        void shoppingListCreated(ShoppingList shoppingList);
    }

    private final static String TAG = "CreateShoppingList";

    private CreateShoppingListFragmentListener listener;

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

        ibtnClose = view.findViewById(R.id.ibtnClose);
        etShoppingListName = view.findViewById(R.id.etShoppingListName);
        tvDateRange = view.findViewById(R.id.tvDateRange);
        btnDone = view.findViewById(R.id.btnDone);

        setUpOnClickListeners();
        setUpDateRangePicker();
    }

    private void setUpOnClickListeners(){
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
                    Log.e(TAG,"Parse exceptions creating shopping list", e);
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

    private void openDatePicker(){
        dateRangePicker.show(getChildFragmentManager(), TAG);
    }

    private void createShoppingList() throws ParseException {
        ShoppingList shoppingList = ShoppingList.createShoppingList(etShoppingListName.getText().toString(),
                formatter.parse(formatter.format(startDayCalendar.getTime())),
                formatter.parse(formatter.format(endDayCalendar.getTime())));

        shoppingList.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if(e != null){
                    Log.e(TAG,"Couldn't save shopping list", e);
                    return;
                }
                listener.shoppingListCreated(shoppingList);
            }
        });
    }

}