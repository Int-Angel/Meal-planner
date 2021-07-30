package com.example.mealplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * The user can login from this activity
 */
public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            // default ACLs for User object
            ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
            parseACL.setPublicReadAccess(true);

            ParseUser.getCurrentUser().setACL(parseACL);
            goMainActivity();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        setupButtonsListeners();
    }

    /**
     * this method creates the OnClickListeners for the buttons in this activity
     */
    private void setupButtonsListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignupActivity();
            }
        });
    }

    /**
     * This method logins the user using the username and the password of the user
     * @param username
     * @param password
     */
    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Fail to login!", Toast.LENGTH_SHORT).show();
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
     * This method opens the sign up activity
     */
    private void goSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     * This methods opens the main activity
     */
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}