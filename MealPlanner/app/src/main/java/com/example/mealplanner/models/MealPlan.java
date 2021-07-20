package com.example.mealplanner.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Week")
public class MealPlan extends ParseObject {

    public final static String KEY_USER = "user";
    public final static String KEY_RECIPE = "recipe";
    public final static String KEY_QUANTITY = "quantity";
    public final static String KEY_DATE = "date";

    public static MealPlan createMealPlan(Recipe recipe, Date date){
        MealPlan mealPlan = new MealPlan();

        mealPlan.setUser(ParseUser.getCurrentUser());
        mealPlan.setRecipe(recipe);
        mealPlan.setQuantity(1);
        mealPlan.setDate(date);

        return mealPlan;
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseObject getRecipe(){
        return getParseObject(KEY_RECIPE);
    }

    public void setRecipe(Recipe recipe){
        put(KEY_RECIPE, recipe);
    }

    public int getQuantity(){
        return (int) getNumber(KEY_QUANTITY);
    }

    public void setQuantity(int quantity){
        put(KEY_QUANTITY, quantity);
    }

    public Date getDate(){
        return getDate(KEY_DATE);
    }

    public void setDate(Date date){
        put(KEY_DATE, date);
    }
}
