package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.example.mealplanner.FilteringViewModel;
import com.example.mealplanner.R;
import com.example.mealplanner.adapters.FilterCheckBoxAdapter;
import com.example.mealplanner.models.FilterCheckBox;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FiltersFragment extends Fragment {

    private final static String TAG = "FiltersFragment";
    private FilteringViewModel filteringViewModel;

    private List<FilterCheckBox> cuisines;
    private List<FilterCheckBox> mealType;
    private FilterCheckBoxAdapter cuisinesAdapter;
    private FilterCheckBoxAdapter mealTypesAdapter;

    private ImageButton ibtnClose;
    private CheckBox cbCuisines;
    private CheckBox cbMealTypes;
    private CheckBox cbMaxTime;
    private CheckBox cbCalories;
    private RecyclerView rvCuisines;
    private RecyclerView rvMealTypes;
    private Slider slMaxTime;
    private RangeSlider rSlCalories;
    private Button btnApply;

    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filteringViewModel = new ViewModelProvider(requireActivity()).get(FilteringViewModel.class);

        initializeCuisines();
        initializeMealType();

        ibtnClose = view.findViewById(R.id.ibtnClose);
        cbCuisines = view.findViewById(R.id.cbCuisines);
        cbMealTypes = view.findViewById(R.id.cbMealTypes);
        cbCalories = view.findViewById(R.id.cbCalories);
        cbMaxTime = view.findViewById(R.id.cbMaxTime);
        rvCuisines = view.findViewById(R.id.rvCuisines);
        rvMealTypes = view.findViewById(R.id.rvMealTypes);
        slMaxTime = view.findViewById(R.id.slMaxTime);
        rSlCalories = view.findViewById(R.id.rSlCalories);
        btnApply = view.findViewById(R.id.btnApply);

        cuisinesAdapter = new FilterCheckBoxAdapter(getContext(), cuisines);
        mealTypesAdapter = new FilterCheckBoxAdapter(getContext(), mealType);

        rvCuisines.setAdapter(cuisinesAdapter);
        rvCuisines.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        rvMealTypes.setAdapter(mealTypesAdapter);
        rvMealTypes.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        setUpOnClickListeners();
    }

    private void setUpOnClickListeners() {

        cbCuisines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int visibility = isChecked ? View.VISIBLE : View.GONE;
                rvCuisines.setVisibility(visibility);
            }
        });

        cbMealTypes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int visibility = isChecked ? View.VISIBLE : View.GONE;
                rvMealTypes.setVisibility(visibility);
            }
        });

        cbMaxTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int visibility = isChecked ? View.VISIBLE : View.GONE;
                slMaxTime.setVisibility(visibility);
            }
        });

        cbCalories.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int visibility = isChecked ? View.VISIBLE : View.GONE;
                rSlCalories.setVisibility(visibility);
            }
        });

        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilters();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        filteringViewModel.setCuisines(FilterCheckBox.getSelectedItems(cuisines));
        if (FilterCheckBox.getSelectedItems(mealType).size() > 0)
            filteringViewModel.setMealTypes(FilterCheckBox.getSelectedItems(mealType).get(0));
        else
            filteringViewModel.setMealTypes("");
        filteringViewModel.setMaxTimeReady((int) slMaxTime.getValue());
        filteringViewModel.setMinCalories(Math.round(rSlCalories.getValues().get(0)));
        filteringViewModel.setMaxCalories(Math.round(rSlCalories.getValues().get(1)));

        filteringViewModel.setActiveCalories(cbCalories.isChecked());
        filteringViewModel.setActiveCuisines(cbCuisines.isChecked());
        filteringViewModel.setActiveMaxTimeReady(cbMaxTime.isChecked());
        filteringViewModel.setActiveMealTypes(cbMealTypes.isChecked());

        closeFilters();
    }

    private void closeFilters() {
        /*getParentFragmentManager()
                .beginTransaction()
                .remove(FiltersFragment.this)
                .commit();*/

        getParentFragmentManager()
                .beginTransaction()
                .hide(FiltersFragment.this)
                .commit();
    }

    private void initializeCuisines() {
        cuisines = new ArrayList<>();

        cuisines.add(new FilterCheckBox("African"));
        cuisines.add(new FilterCheckBox("American"));
        cuisines.add(new FilterCheckBox("British"));
        cuisines.add(new FilterCheckBox("Cajun"));
        cuisines.add(new FilterCheckBox("Caribbean"));
        cuisines.add(new FilterCheckBox("Chinese"));
        cuisines.add(new FilterCheckBox("Eastern"));
        cuisines.add(new FilterCheckBox("Eastern European"));
        cuisines.add(new FilterCheckBox("European"));
        cuisines.add(new FilterCheckBox("French"));
        cuisines.add(new FilterCheckBox("German"));
        cuisines.add(new FilterCheckBox("Greek"));
        cuisines.add(new FilterCheckBox("Indian"));
        cuisines.add(new FilterCheckBox("Irish"));
        cuisines.add(new FilterCheckBox("Italian"));
        cuisines.add(new FilterCheckBox("Japanese"));
        cuisines.add(new FilterCheckBox("Jewish"));
        cuisines.add(new FilterCheckBox("Korean"));
        cuisines.add(new FilterCheckBox("Latin American"));
        cuisines.add(new FilterCheckBox("Mediterranean"));
        cuisines.add(new FilterCheckBox("Mexican"));
        cuisines.add(new FilterCheckBox("Middle Eastern"));
        cuisines.add(new FilterCheckBox("Nordic"));
        cuisines.add(new FilterCheckBox("Southern"));
        cuisines.add(new FilterCheckBox("Spanish"));
        cuisines.add(new FilterCheckBox("Thai"));
        cuisines.add(new FilterCheckBox("Vietnamese"));
    }

    private void initializeMealType() {
        mealType = new ArrayList<>();

        mealType.add(new FilterCheckBox("main course"));
        mealType.add(new FilterCheckBox("side dish"));
        mealType.add(new FilterCheckBox("dessert"));
        mealType.add(new FilterCheckBox("appetizer"));
        mealType.add(new FilterCheckBox("salad"));
        mealType.add(new FilterCheckBox("bread"));
        mealType.add(new FilterCheckBox("breakfast"));
        mealType.add(new FilterCheckBox("soup"));
        mealType.add(new FilterCheckBox("beverage"));
        mealType.add(new FilterCheckBox("sauce"));
        mealType.add(new FilterCheckBox("marinade"));
        mealType.add(new FilterCheckBox("fingerfood"));
        mealType.add(new FilterCheckBox("snack"));
        mealType.add(new FilterCheckBox("drink"));
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}