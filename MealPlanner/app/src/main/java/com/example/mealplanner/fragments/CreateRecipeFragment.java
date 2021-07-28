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
import com.example.mealplanner.adapters.CreateStepsAdapter;
import com.example.mealplanner.adapters.StepsAdapter;
import com.example.mealplanner.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
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


public class CreateRecipeFragment extends Fragment {

    private static final String RECIPE = "recipe";
    private static final String POST_IMAGE_URL = "https://api.imgbb.com/1/upload";
    private static final String missingImageUrl = "https://cdn2.vectorstock.com/i/thumb-large/48/06/image-preview-icon-picture-placeholder-vector-31284806.jpg";
    private static final String TAG = "CreateRecipe";

    private ActivityResultLauncher<Intent> galleryLauncher;

    private AsyncHttpClient client;
    private Recipe recipe;
    private List<String> steps;
    private CreateStepsAdapter stepsAdapter;

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

    private String imageBase64;
    private String imageUrl;
    private String originalUrl;
    private String mealType;
    private String cuisineType;

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

        steps = new ArrayList<>();

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

        setUpSpinners();
        setUpGalleryLauncher();
        setUpOnClickListeners();
    }

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

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    private void setUpSpinners() {
        ArrayAdapter<CharSequence> mealTypeSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item);

        mealTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMealType.setAdapter(mealTypeSpinnerAdapter);
        spinnerMealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealType = parent.getItemAtPosition(position).toString();
                Log.i(TAG,"Meal type selected: " + mealType);
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
                Log.i(TAG,"Cuisine type selected: " + cuisineType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

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
    }

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

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        galleryLauncher.launch(cameraIntent);
    }

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

    private void createNewStep(){
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

    private void closeFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .remove(CreateRecipeFragment.this)
                .commit();
    }
}