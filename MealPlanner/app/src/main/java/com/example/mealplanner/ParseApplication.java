package com.example.mealplanner;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //ParseObject.registerSubclass();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("qy7PFL9qRlAc0O78cKJJkDAjwj6rItJuobbrOuDU")
                .clientKey("TrP1gkIs6KsXZ8hJQq7WiGSn0z9mkqWOprByKFO3")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
