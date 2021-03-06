package com.example.mealplanner.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.security.PublicKey;

/**
 * This class represents independent shopping list items in the database using Parse
 */
@ParseClassName("ShoppingListItems")
public class ShoppingListItem extends ParseObject {

    /**
     * Database keys
     */
    public final static String KEY_SHOPPING_LIST = "ShoppingList";
    public final static String KEY_CHECKED = "checked";
    public final static String KEY_NAME = "name";
    public final static String KEY_AISLE = "aisle";
    public final static String KEY_INGREDIENT_ID = "ingredientId";
    public final static String KEY_AMOUNT = "amount";
    public final static String KEY_UNIT = "unit";
    public final static String KEY_IMAGE = "image";

    /**
     * Generates a shopping list item from a ingredient
     *
     * @param shoppingList shopping list that owns this item
     * @param ingredient   ingredient to generate the shopping list item
     * @return
     */
    public static ShoppingListItem createShoppingListItem(ShoppingList shoppingList, Ingredient ingredient) {
        ShoppingListItem shoppingListItem = new ShoppingListItem();

        shoppingListItem.setShoppingList(shoppingList);
        shoppingListItem.setChecked(false);
        shoppingListItem.setName(ingredient.getNameClean());
        shoppingListItem.setAisle(ingredient.getAisle());
        shoppingListItem.setIngredientId(ingredient.getIngredientId());
        shoppingListItem.setAmount(ingredient.getAmount());
        shoppingListItem.setUnit(ingredient.getUnit());
        shoppingListItem.setImage(ingredient.getImage());

        return shoppingListItem;
    }

    /**
     * Generates a shopping list item from independent parameters
     *
     * @param shoppingList
     * @param name
     * @param aisle
     * @param amount
     * @param unit
     * @return
     */
    public static ShoppingListItem createShoppingListItem(ShoppingList shoppingList, String name, String aisle, float amount, String unit) {
        ShoppingListItem shoppingListItem = new ShoppingListItem();

        shoppingListItem.setShoppingList(shoppingList);
        shoppingListItem.setChecked(false);
        shoppingListItem.setName(name);
        shoppingListItem.setAisle(aisle);
        shoppingListItem.setIngredientId("custom");
        shoppingListItem.setAmount(amount);
        shoppingListItem.setUnit(unit);
        shoppingListItem.setImage("");

        return shoppingListItem;
    }

    /**
     * add n to the amount of the item
     *
     * @param n
     */
    public void addAmount(float n) {
        put(KEY_AMOUNT, getAmount() + n);
    }

    /**
     * Getters and setters
     */
    public ParseObject getShoppingList() {
        return getParseObject(KEY_SHOPPING_LIST);
    }

    public void setShoppingList(ShoppingList shoppingList) {
        put(KEY_SHOPPING_LIST, shoppingList);
    }

    public boolean isChecked() {
        return getBoolean(KEY_CHECKED);
    }

    public void setChecked(boolean checked) {
        put(KEY_CHECKED, checked);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getAisle() {
        return getString(KEY_AISLE);
    }

    public void setAisle(String aisle) {
        put(KEY_AISLE, aisle);
    }

    public String getIngredientId() {
        return getString(KEY_INGREDIENT_ID);
    }

    public void setIngredientId(String ingredientId) {
        put(KEY_INGREDIENT_ID, ingredientId);
    }

    public float getAmount() {
        try {
            return (float) getNumber(KEY_AMOUNT);
        } catch (ClassCastException e) {
            return (int) getNumber(KEY_AMOUNT);
        }
    }

    public void setAmount(float amount) {
        put(KEY_AMOUNT, amount);
    }

    public String getUnit() {
        return getString(KEY_UNIT);
    }

    public void setUnit(String unit) {
        put(KEY_UNIT, unit);
    }

    public String getImage() {
        return getString(KEY_IMAGE);
    }

    public void setImage(String image) {
        put(KEY_IMAGE, image);
    }

}
