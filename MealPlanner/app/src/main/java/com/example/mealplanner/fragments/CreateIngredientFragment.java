package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mealplanner.R;
import com.example.mealplanner.models.Ingredient;

import org.jetbrains.annotations.NotNull;

public class CreateIngredientFragment extends Fragment {

    private static final String TAG = "CreateIngredient";
    private static final String INGREDIENT = "ingredient";

    private Ingredient oldIngredient;

    private ImageButton ibtnClose;
    private EditText etItem;
    private EditText etAmount;
    private EditText etUnit;
    private EditText etAisle;
    private Button btnDone;

    public CreateIngredientFragment() {
        // Required empty public constructor
    }

    public static CreateIngredientFragment newInstance(Ingredient ingredient) {
        CreateIngredientFragment fragment = new CreateIngredientFragment();
        Bundle args = new Bundle();

        args.putParcelable(INGREDIENT, ingredient);

        fragment.setArguments(args);
        return fragment;
    }

    public static CreateIngredientFragment newInstance() {
        CreateIngredientFragment fragment = new CreateIngredientFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            oldIngredient = getArguments().getParcelable(INGREDIENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_ingredient, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ibtnClose = view.findViewById(R.id.ibtnClose);
        etItem = view.findViewById(R.id.etItem);
        etAmount = view.findViewById(R.id.etAmount);
        etUnit = view.findViewById(R.id.etUnit);
        etAisle = view.findViewById(R.id.etAisle);
        btnDone = view.findViewById(R.id.btnDone);

        if(oldIngredient != null)
            bind();

        setUpOnClickListeners();
    }

    private void bind(){

    }

    private void setUpOnClickListeners(){
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIngredient();
            }
        });
    }

    private void closeFragment(){
        getParentFragmentManager()
                .beginTransaction()
                .remove(CreateIngredientFragment.this)
                .commit();
    }

    private void createIngredient(){

    }
}