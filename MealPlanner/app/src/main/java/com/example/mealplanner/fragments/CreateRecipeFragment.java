package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.models.Recipe;

import org.jetbrains.annotations.NotNull;


public class CreateRecipeFragment extends Fragment {

    private static final String RECIPE = "recipe";
    private static final String missingImageUrl = "https://cdn2.vectorstock.com/i/thumb-large/48/06/image-preview-icon-picture-placeholder-vector-31284806.jpg";
    private static final String TAG = "CreateRecipe";

    private Recipe recipe;

    private ImageButton ibtnBack;
    private ImageView ivRecipeImage;

    public CreateRecipeFragment() {
        // Required empty public constructor
    }

    public static CreateRecipeFragment newInstance(Recipe recipe) {
        CreateRecipeFragment fragment = new CreateRecipeFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateRecipeFragment newInstance() {
        CreateRecipeFragment fragment = new CreateRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ibtnBack = view.findViewById(R.id.ibtnBack);
        ivRecipeImage = view.findViewById(R.id.ivRecipeImage);


        Glide.with(getContext())
                .load(missingImageUrl)
                .transform(new CenterCrop(), new RoundedCorners(1000))
                .into(ivRecipeImage);

        setUpOnClickListeners();
    }



    private void setUpOnClickListeners(){
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
    }

    private void closeFragment(){
        getParentFragmentManager()
                .beginTransaction()
                .remove(CreateRecipeFragment.this)
                .commit();
    }
}