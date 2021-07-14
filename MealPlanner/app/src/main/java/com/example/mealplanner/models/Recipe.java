package com.example.mealplanner.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {

    public final static String KEY_USER = "user";
    public final static String KEY_TITLE = "title";
    public final static String KEY_IMAGE_URL = "imageUrl";
    public final static String KEY_DISH_TYPE = "dishType";
    public final static String KEY_MEAL_TYPE = "mealType";
    public final static String KEY_CUISINE_TYPE = "cuisineType";
    public final static String KEY_TOTAL_TIME = "totalTime";
    public final static String KEY_CALORIES = "calories";
    //public final static String KEY_CALORIES = "calories";


}
