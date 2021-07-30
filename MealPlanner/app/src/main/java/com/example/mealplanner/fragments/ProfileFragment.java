package com.example.mealplanner.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.LoginActivity;
import com.example.mealplanner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * Current user profile fragment, shows all his information
 */
public class ProfileFragment extends Fragment {

    private final static String TAG = "ProfileFragment";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private String photoFileName = "photo.jpg";
    private File photoFile;

    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvName;
    private TextView tvLastname;
    private Switch swIsPublic;
    private Button btnLogout;
    private FloatingActionButton fab;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvLastname = view.findViewById(R.id.tvLastname);
        swIsPublic = view.findViewById(R.id.swIsPublic);
        btnLogout = view.findViewById(R.id.btnLogout);
        fab = view.findViewById(R.id.fab);

        bind();
        setUpListeners();
    }

    /**
     * Binds the user information to the view
     */
    private void bind() {
        swIsPublic.setChecked(ParseUser.getCurrentUser().getBoolean("isPublic"));
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvName.setText(ParseUser.getCurrentUser().getString("name"));
        tvLastname.setText(ParseUser.getCurrentUser().getString("lastname"));

        if (ParseUser.getCurrentUser().getParseFile("image") != null) {
            Glide.with(getContext())
                    .load(ParseUser.getCurrentUser().getParseFile("image").getUrl())
                    .transform(new CenterCrop(), new RoundedCorners(1000))
                    .into(ivProfileImage);
        } else {
            Glide.with(getContext())
                    .load("https://happytravel.viajes/wp-content/uploads/2020/04/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png")
                    .transform(new CenterCrop(), new RoundedCorners(1000))
                    .into(ivProfileImage);
        }
    }

    /**
     * Sets up all the onClickListeners
     */
    private void setUpListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        swIsPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePrivateProfile(isChecked);
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogUpdateInformation();
            }
        });
    }

    /**
     * Updates the user image in the database
     */
    private void changeProfileImage() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.put("image", new ParseFile(photoFile));
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving profile picture", e);
                        return;
                    }
                    Log.i(TAG, "Profile picture saved");
                }
            });
        }
    }

    /**
     * Updates the isPublic property of a user in the database
     *
     * @param isPublic
     */
    private void updatePrivateProfile(boolean isPublic) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.put("isPublic", isPublic);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving user isPublic property", e);
                        return;
                    }
                }
            });
        }
    }

    /**
     * Open a dialog alert and the user can insert a new name and lastname
     */
    private void openDialogUpdateInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change information");

        LinearLayout.LayoutParams lpLinearLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(lpLinearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        final EditText etName = new EditText(getContext());
        etName.setText(tvName.getText().toString());
        etName.setHint("Name");
        etName.setLayoutParams(lp);

        final EditText etLastname = new EditText(getContext());
        etLastname.setText(tvLastname.getText().toString());
        etLastname.setHint("Lastname");
        etLastname.setLayoutParams(lp);

        linearLayout.addView(etName);
        linearLayout.addView(etLastname);

        builder.setView(linearLayout);

        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = etName.getText().toString();
                String newLastName = etLastname.getText().toString();

                if (!newName.equals("") && !newLastName.equals("")) {
                    tvName.setText(newName);
                    tvLastname.setText(newLastName);
                    saveNewName(newName, newLastName);
                }
            }
        });

        builder.setNegativeButton("cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Saves the new information of the user in the database
     *
     * @param newName
     * @param newLastname
     */
    private void saveNewName(String newName, String newLastname) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.put("name", newName);
            currentUser.put("lastname", newLastname);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving user information", e);
                        return;
                    }
                }
            });
        }
    }

    /**
     * Logout the user
     */
    private void logout() {
        ParseUser.logOut();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        getContext().startActivity(intent);
        getActivity().finish();
    }

    /**
     * Launches the camera to allow the user to take a picture for his new image profile
     */
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Log.i(TAG, "NO CAMERA");
        }
    }

    /**
     * Returns the File for a photo stored on disk given the fileName
     *
     * @param fileName
     * @return
     */
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    /**
     * Gets the picture taken by the user
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview

                Glide.with(getContext())
                        .load(takenImage)
                        .transform(new CenterCrop(), new RoundedCorners(1000))
                        .into(ivProfileImage);

                changeProfileImage();
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}