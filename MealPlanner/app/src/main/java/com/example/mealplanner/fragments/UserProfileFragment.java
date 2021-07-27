package com.example.mealplanner.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.adapters.RecipeAdapter;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class UserProfileFragment extends Fragment implements RecipeDetailsFragment.RecipeDetailsFragmentListener {

    private static final String TAG = "UserProfileFragment";
    private static final String USER = "user";

    private RecipeDetailsFragment recipeDetailsFragment;

    private ParseUser user;
    private List<IRecipe> recipes;
    private RecipeAdapter adapter;

    private ImageButton ibtnBack;
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvName;
    private RecyclerView rvRecipes;
    private ProgressBar progress_circular;
    private TextView tvNoSavedRecipes;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(ParseUser user) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();

        args.putParcelable(USER, user);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getParcelable(USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipes = new ArrayList<>();

        ibtnBack = view.findViewById(R.id.ibtnBack);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        progress_circular = view.findViewById(R.id.progress_circular);
        tvNoSavedRecipes = view.findViewById(R.id.tvNoSavedRecipes);

        adapter = new RecipeAdapter(getContext(), recipes, new RecipeAdapter.RecipeAdapterListener() {
            @Override
            public void openDetails(IRecipe recipe, int index) {
                openRecipeDetails(recipe, index);
            }
        });

        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        bind();
        setUpOnClickListeners();
        queryRecipes();
    }

    private void bind(){
        if (user.getParseFile("image") != null) {
            Glide.with(getContext())
                    .load(user.getParseFile("image").getUrl())
                    .transform(new CenterCrop(), new RoundedCorners(1000))
                    .into(ivProfileImage);
        } else {
            Glide.with(getContext())
                    .load("https://happytravel.viajes/wp-content/uploads/2020/04/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png")
                    .transform(new CenterCrop(), new RoundedCorners(1000))
                    .into(ivProfileImage);
        }

        tvUsername.setText(user.getUsername());
        tvName.setText(user.getString("name") + " " + user.getString("lastname"));
    }

    private void setUpOnClickListeners() {
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
    }

    private void closeFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .remove(UserProfileFragment.this)
                .commit();
    }

    private void openRecipeDetails(IRecipe recipe, int index) {
        recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipe, index);

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, recipeDetailsFragment)
                .commit();
    }

    private void queryRecipes() {
        progress_circular.setVisibility(View.VISIBLE);

        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);

        query.whereEqualTo(Recipe.KEY_USER, user);

        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {

                progress_circular.setVisibility(View.GONE);
                if (e != null) {
                    Log.e(TAG, "Fail getting recipes", e);
                    return;
                }
                recipes.clear();
                recipes.addAll(objects);
                adapter.notifyDataSetChanged();

                if (objects.size() == 0) {
                    tvNoSavedRecipes.setVisibility(View.VISIBLE);
                } else {
                    tvNoSavedRecipes.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void backButtonPressed() {
        getChildFragmentManager()
                .beginTransaction()
                .remove(recipeDetailsFragment)
                .commit();
    }

    @Override
    public void updateRecipeList() {

    }
}