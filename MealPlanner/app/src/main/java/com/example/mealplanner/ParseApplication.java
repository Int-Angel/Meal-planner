package com.example.mealplanner;

import android.app.Application;

import com.example.mealplanner.models.Ingredient;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.Recipe;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Class application, configure the database
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(MealPlan.class);
        ParseObject.registerSubclass(ShoppingList.class);
        ParseObject.registerSubclass(ShoppingListItem.class);
        ParseObject.registerSubclass(Ingredient.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("qy7PFL9qRlAc0O78cKJJkDAjwj6rItJuobbrOuDU")
                .clientKey("TrP1gkIs6KsXZ8hJQq7WiGSn0z9mkqWOprByKFO3")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
