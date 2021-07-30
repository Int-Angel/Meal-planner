package com.example.mealplanner.models;

/**
 * This class is used to check witch recipes are already added to the day in the week fragment and
 * change the add icon to this recipe.
 */
public class AddRecipe {
    private boolean added; // is it added to the meal plan of that day?
    private Recipe recipe; //saved recipe

    //Constructors

    public AddRecipe(boolean added, Recipe recipe) {
        this.added = added;
        this.recipe = recipe;
    }

    public AddRecipe() {

    }

    /**
     * Getters and Setters
     */

    public boolean isAdded() {
        return added;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
