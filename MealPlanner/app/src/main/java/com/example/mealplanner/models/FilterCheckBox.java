package com.example.mealplanner.models;

public class FilterCheckBox {

    private String name;
    private boolean isSelected;

    public FilterCheckBox(String name) {
        this.name = name;
        this.isSelected = false;
    }

    public FilterCheckBox(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
