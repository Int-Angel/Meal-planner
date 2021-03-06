package com.example.mealplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * This class allows the user to create a new account to use the app
 */
public class SignupActivity extends AppCompatActivity {

    private final static String TAG = "SignupActivity";

    private EditText etUsername;
    private EditText etPassword;
    private EditText etName;
    private EditText etLastname;
    private Button btnSignup;
    private ImageButton ibtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etLastname = findViewById(R.id.etLastname);
        btnSignup = findViewById(R.id.btnSignup);
        ibtnBack = findViewById(R.id.ibtnBack);

        setupButtonsListeners();
    }

    /**
     * this method creates the OnClickListeners for the buttons in this activity
     */
    private void setupButtonsListeners() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singUpUser(
                        etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etName.getText().toString(),
                        etLastname.getText().toString());
            }
        });

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * This method creates the new account for the user
     *
     * @param username
     * @param password
     * @param name
     * @param lastname
     */
    private void singUpUser(String username, String password, String name, String lastname) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("name", name);
        user.put("lastname", lastname);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with signup", e);
                    Toast.makeText(SignupActivity.this, "Fail to signup", Toast.LENGTH_SHORT).show();
                    return;
                }

                // default ACLs for User object
                ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
                parseACL.setPublicReadAccess(true);
                ParseUser.getCurrentUser().setACL(parseACL);

                goMainActivity();
            }
        });
    }

    /**
     * This methods opens the main activity when the user has created an account
     */
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}