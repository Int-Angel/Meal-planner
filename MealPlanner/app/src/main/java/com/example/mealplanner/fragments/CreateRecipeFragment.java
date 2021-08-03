package com.example.mealplanner.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.SwipeToDeleteCallback;
import com.example.mealplanner.adapters.CreateStepsAdapter;
import com.example.mealplanner.adapters.IngredientsAdapter;
import com.example.mealplanner.adapters.StepsAdapter;
import com.example.mealplanner.models.Ingredient;
import com.example.mealplanner.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Activity.RESULT_OK;

/**
 * This fragment allows the user to create a new recipe and it's added to the database
 */
public class CreateRecipeFragment extends Fragment {

    /**
     * Interface to update the saved recipes list
     */
    public interface CreateRecipeFragmentListener {
        void newRecipeCreated();

        void closeCreateNewRecipe();
    }

    private static final String RECIPE = "recipe";
    private static final String POST_IMAGE_URL = "https://api.imgbb.com/1/upload";
    private static final String POST_INGREDIENTS = "https://api.spoonacular.com/recipes/parseIngredients?apiKey=728721c3da7543769d5413b35ac70cd7";
    private static final String missingImageUrl = "https://cdn2.vectorstock.com/i/thumb-large/48/06/image-preview-icon-picture-placeholder-vector-31284806.jpg";
    private static final String TAG = "CreateRecipe";

    private CreateRecipeFragmentListener listener;
    private ActivityResultLauncher<Intent> galleryLauncher;

    private AsyncHttpClient client;
    private Recipe recipe;
    private CreateStepsAdapter stepsAdapter;
    private IngredientsAdapter ingredientsAdapter;

    private ImageButton ibtnBack;
    private EditText etTitle;
    private ImageView ivRecipeImage;
    private ImageButton ibtnGoToOriginalUrl;
    private ProgressBar progressBarImage;
    private EditText etCalories;
    private EditText etTime;
    private AppCompatSpinner spinnerMealType;
    private AppCompatSpinner spinnerCuisineType;
    private RecyclerView rvIngredients;
    private FloatingActionButton fabIngredients;
    private ViewPager vpSteps;
    private TabLayout tabLayout;
    private FloatingActionButton fabSteps;
    private FloatingActionButton fabSave;
    private ProgressBar progressBar;

    private String imageBase64;

    // Recipe properties
    private String imageUrl;
    private String originalUrl;
    private String mealType;
    private String cuisineType;
    private List<String> steps;
    private List<String> ingredients;

    private List<Ingredient> recipeIngredients;

    public CreateRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * New instance used to edit a recipe
     *
     * @param recipe
     * @return
     */
    public static CreateRecipeFragment newInstance(Recipe recipe) {
        CreateRecipeFragment fragment = new CreateRecipeFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * New instance used to create a new recipe
     *
     * @return
     */
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

        listener = (CreateRecipeFragmentListener) getParentFragment();

        steps = new ArrayList<>();
        ingredients = new ArrayList<>();
        recipeIngredients = new ArrayList<>();

        ibtnBack = view.findViewById(R.id.ibtnBack);
        etTitle = view.findViewById(R.id.etTitle);
        ivRecipeImage = view.findViewById(R.id.ivRecipeImage);
        ibtnGoToOriginalUrl = view.findViewById(R.id.ibtnGoToOriginalUrl);
        progressBarImage = view.findViewById(R.id.progress_circular_image);
        etCalories = view.findViewById(R.id.etCalories);
        etTime = view.findViewById(R.id.etTime);
        spinnerMealType = view.findViewById(R.id.spinnerMealType);
        spinnerCuisineType = view.findViewById(R.id.spinnerCuisineType);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        fabIngredients = view.findViewById(R.id.fabIngredients);
        vpSteps = view.findViewById(R.id.vpSteps);
        tabLayout = view.findViewById(R.id.tabLayout);
        fabSteps = view.findViewById(R.id.fabSteps);
        fabSave = view.findViewById(R.id.fabSave);
        progressBar = view.findViewById(R.id.progress_circular_creating);

        Glide.with(getContext())
                .load(missingImageUrl)
                .transform(new CenterCrop(), new RoundedCorners(1000))
                .into(ivRecipeImage);

        stepsAdapter = new CreateStepsAdapter(getContext(), steps);
        vpSteps.setAdapter(stepsAdapter);

        tabLayout.setupWithViewPager(vpSteps, true);

        int pagerPadding = 20;
        vpSteps.setClipToPadding(false);
        vpSteps.setPadding(pagerPadding, pagerPadding, pagerPadding, pagerPadding);

        ingredientsAdapter = new IngredientsAdapter(getContext(), ingredients, new IngredientsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                editIngredient(position);
            }
        });
        rvIngredients.setAdapter(ingredientsAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(ingredientsAdapter));
        itemTouchHelper.attachToRecyclerView(rvIngredients);

        setUpSpinners();
        setUpGalleryLauncher();
        setUpOnClickListeners();
    }

    /**
     * Prepares the gallery launcher and continues the image process
     */
    private void setUpGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();

                            Uri returnUri = data.getData();
                            Bitmap bitmapImage = null;

                            try {
                                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            imageBase64 = bitmapToBase64(bitmapImage);
                            loadImage();
                        }
                    }
                });
    }

    /**
     * Returns the image as a base64 string
     *
     * @param bitmap
     * @return
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    /**
     * Prepares the spinners
     */
    private void setUpSpinners() {
        ArrayAdapter<CharSequence> mealTypeSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item);

        mealTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMealType.setAdapter(mealTypeSpinnerAdapter);
        spinnerMealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealType = parent.getItemAtPosition(position).toString();
                Log.i(TAG, "Meal type selected: " + mealType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> cuisineTypeSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.cuisine_types_array, android.R.layout.simple_spinner_item);

        cuisineTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCuisineType.setAdapter(cuisineTypeSpinnerAdapter);
        spinnerCuisineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cuisineType = parent.getItemAtPosition(position).toString();
                Log.i(TAG, "Cuisine type selected: " + cuisineType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Sets up the onClickListeners
     */
    private void setUpOnClickListeners() {
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        ivRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGalleryPermission();
            }
        });

        ibtnGoToOriginalUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertOriginalUrl();
            }
        });

        fabSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewStep();
            }
        });

        fabIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewIngredient();
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecipe();
            }
        });
    }

    /**
     * Checks if the application has the permission to open the gallery and ask permission
     */
    private void checkGalleryPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
        } else {
            startGallery();
        }
    }

    /**
     * Starts the gallery to get an image
     */
    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        galleryLauncher.launch(cameraIntent);
    }

    /**
     * Upload the selected image from teh galery to the server using imgbb API and gets the image
     * url
     */
    private void loadImage() {

        progressBarImage.setVisibility(View.VISIBLE);

        client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.put("key", "93840e7eaccdb120c423249c1cddd30f");
        requestParams.put("image", imageBase64);

        client.post(getContext(), POST_IMAGE_URL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                progressBarImage.setVisibility(View.GONE);
                Log.i(TAG, "Success uploading the image");
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    imageUrl = jsonObject.getJSONObject("data").getString("url");

                    Glide.with(getContext())
                            .load(imageUrl)
                            .transform(new CenterCrop(), new RoundedCorners(1000))
                            .into(ivRecipeImage);

                } catch (JSONException e) {
                    Log.e(TAG, "Error with image JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBarImage.setVisibility(View.GONE);
                String res = new String(responseBody);
                Log.e(TAG, "Error while loading image: " + headers.toString() + " " + res, error);
            }
        });
    }

    /**
     * Opens an alert dialog to insert the original url from the recipe
     */
    private void insertOriginalUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Original Url");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                originalUrl = input.getText().toString();
                Log.i(TAG, "Original url: " + originalUrl);
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                originalUrl = "";
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Opens an alert dialog to create a new step for teh recipe
     */
    private void createNewStep() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Step");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                steps.add(input.getText().toString());
                stepsAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Opens a alert dialog to create a new ingredient
     */
    private void createNewIngredient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ingredient");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredients.add(input.getText().toString());
                ingredientsAdapter.notifyItemInserted(ingredients.size() - 1);
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Opens a alert dialog to edit a ingredient from the recipe
     *
     * @param position
     */
    private void editIngredient(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ingredient");

        final EditText input = new EditText(getContext());
        input.setText(ingredients.get(position));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ingredients.set(position, input.getText().toString());
                ingredientsAdapter.notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * closes the fragment
     */
    private void closeFragment() {
        listener.closeCreateNewRecipe();
    }

    /**
     * creates the recipe in the database
     */
    private void createRecipe() {
        progressBar.setVisibility(View.VISIBLE);

        String title = "no title";
        if (!etTitle.getText().toString().equals(""))
            title = etTitle.getText().toString();

        String calories = "";
        if (!etCalories.getText().toString().equals(""))
            calories = etCalories.getText().toString();

        String time = "0";
        if (!etTime.getText().toString().equals(""))
            time = etTime.getText().toString();

        float caloriesNum = 0;
        if (!calories.isEmpty())
            caloriesNum = Float.parseFloat(calories);

        if (imageUrl == null)
            imageUrl = missingImageUrl;

        if (originalUrl == null)
            originalUrl = "www.google.com";

        Recipe recipe = Recipe.createRecipe(title, imageUrl, mealType, cuisineType,
                time, caloriesNum, originalUrl,
                ingredients, steps);

        generateRecipeId(recipe);

        getIngredientsFromAPI(recipe);
    }

    /**
     * Gets all the ingredients information from the API
     *
     * @param recipe recipe that owns the ingredients
     */
    private void getIngredientsFromAPI(Recipe recipe) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("ingredientList", getIngredientsOnePerLine());
        requestParams.put("servings", 1);
        requestParams.put("includeNutrition", false);

        client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.post(getContext(), POST_INGREDIENTS, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    Log.i(TAG, "onSucess getting ingredients: " + jsonArray.toString());

                    recipeIngredients = Ingredient.fromJSONArrayFromAPI(jsonArray);
                    saveRecipeAndIngredients(recipe, recipeIngredients);
                } catch (JSONException e) {
                    Log.e(TAG, "couldn't create jsonObject from response", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "Couldn't get ingredients from API", error);
            }
        });
    }

    /**
     * Saves the ingredients an the recipe in teh database after getting all the information
     *
     * @param recipe
     * @param ingredientList
     */
    private void saveRecipeAndIngredients(Recipe recipe, List<Ingredient> ingredientList) {
        recipe.setIngredientsImagesUrl(getImagesUrlListFromIngredients(ingredientList));

        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't save new recipe", e);
                    return;
                }
                for (int i = 0; i < ingredientList.size(); i++) {
                    ingredientList.get(i).setRecipe(recipe);
                }
                SavedRecipesManager.getInstance().addRecipe(recipe);
                Log.i(TAG, "Saving ingredients");
                ParseObject.saveAllInBackground(ingredientList);
                listener.newRecipeCreated();
                progressBar.setVisibility(View.GONE);
                closeFragment();
            }
        });
    }

    /**
     * Returns a list of images url from a list of ingredients
     *
     * @param ingredientList
     * @return
     */
    private List<String> getImagesUrlListFromIngredients(List<Ingredient> ingredientList) {
        List<String> urls = new ArrayList<>();

        for (Ingredient ingredient : ingredientList) {
            urls.add("https://spoonacular.com/cdn/ingredients_250x250/" + ingredient.getImage());
        }
        return urls;
    }

    /**
     * Generates a recipe id for the new recipe
     *
     * @param recipe
     */
    private void generateRecipeId(Recipe recipe) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RecipeIdCounter");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't get recipe id counter", e);
                    return;
                }
                int id = 0;
                for (ParseObject object : objects) {
                    int currentId = (int) object.getNumber("recipeId");
                    if (currentId < id) {
                        id = currentId;
                    }
                }
                recipe.setId((id - 1) + "");

                ParseObject entity = new ParseObject("RecipeIdCounter");
                entity.put("recipeId", id - 1);
                entity.saveInBackground();
            }
        });

    }

    /**
     * Prepares the ingredients to be sent to the API
     *
     * @return
     */
    private String getIngredientsOnePerLine() {
        String res = "";

        for (String ingredient : ingredients) {
            res += ingredient + "\n";
        }

        return res;
    }
}