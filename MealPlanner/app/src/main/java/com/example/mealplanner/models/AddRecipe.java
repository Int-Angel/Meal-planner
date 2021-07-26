package com.example.mealplanner.models;

public class AddRecipe {
    private boolean added;
    private Recipe recipe;

    public AddRecipe(boolean added, Recipe recipe) {
        this.added = added;
        this.recipe = recipe;
    }

    public AddRecipe() {

    }

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
