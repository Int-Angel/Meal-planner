package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.mealplanner.MainActivity;
import com.example.mealplanner.R;

import org.jetbrains.annotations.NotNull;

/* TODO: Delete*/
public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";

    private final WeekFragment weekFragment = new WeekFragment();
    private final ShoppingListFragment shoppingListFragment = new ShoppingListFragment();

    private RadioButton rbWeek;
    private RadioButton rbShoppingList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rbWeek = view.findViewById(R.id.rbWeek);
        rbShoppingList = view.findViewById(R.id.rbShoppingList);

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, weekFragment).commit();

        setUpOnClickListeners();
    }

    private void setUpOnClickListeners() {
        rbWeek.setOnClickListener(this::onRadioButtonClickedWeek);
        rbShoppingList.setOnClickListener(this::onRadioButtonClickedWeek);
    }

    private void onRadioButtonClickedWeek(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbWeek:
                if (checked)
                    changeFragment(MainActivity.FragmentSelection.WEEK);
                break;
            case R.id.rbShoppingList:
                if (checked)
                    changeFragment(MainActivity.FragmentSelection.SHOPPING_LIST);
                break;
        }
    }

    private void changeFragment(MainActivity.FragmentSelection fragmentSelection) {
        Fragment fragment;

        if(fragmentSelection == MainActivity.FragmentSelection.WEEK)
            fragment = weekFragment;
        else
            fragment = shoppingListFragment;

        getChildFragmentManager()
                .beginTransaction().replace(R.id.flContainer, fragment).commit();
    }


}