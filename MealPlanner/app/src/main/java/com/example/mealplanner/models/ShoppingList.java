package com.example.mealplanner.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("ShoppingList")
public class ShoppingList extends ParseObject {

    public final static String KEY_OBJECT_ID = "objectId";
    public final static String KEY_NAME = "name";
    public final static String KEY_COMPLETED = "completed";
    public final static String KEY_START_DATE = "startDate";
    public final static String KEY_END_DATE = "endDate";
    public final static String KEY_UPDATE_MESSAGE = "updateMessage";
    public final static String KEY_USER = "user";

    public static ShoppingList createShoppingList(String name, Date startDate, Date endDate) {
        ShoppingList shoppingList = new ShoppingList();

        shoppingList.setName(name);
        shoppingList.setCompleted(false);
        shoppingList.setStartDate(startDate);
        shoppingList.setEndDate(endDate);
        shoppingList.setUpdateMessage(false);
        shoppingList.setUser(ParseUser.getCurrentUser());

        return shoppingList;
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getObjectId() {
        return getString(KEY_OBJECT_ID);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public Boolean isCompleted() {
        return getBoolean(KEY_COMPLETED);
    }

    public void setCompleted(boolean completed) {
        put(KEY_COMPLETED, completed);
    }

    public Date getStartDate() {
        return getDate(KEY_START_DATE);
    }

    public void setStartDate(Date startDate) {
        put(KEY_START_DATE, startDate);
    }

    public Date getEndDate() {
        return getDate(KEY_END_DATE);
    }

    public void setEndDate(Date endDate) {
        put(KEY_END_DATE, endDate);
    }

    public boolean getUpdateMessage() {
        return getBoolean(KEY_UPDATE_MESSAGE);
    }

    public void setUpdateMessage(boolean updateMessage) {
        put(KEY_UPDATE_MESSAGE, updateMessage);
    }

}
