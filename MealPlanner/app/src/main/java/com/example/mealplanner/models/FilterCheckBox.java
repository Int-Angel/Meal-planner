package com.example.mealplanner.models;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of filters, for example all the cuisine types, this class
 * has the name of the filter and whether is selected or not
 */
public class FilterCheckBox {

    private String name; // name of the filter
    private boolean isSelected; // is it selected?

    /**
     * Gets a list of filterCheckBox and returns a list of all the filters that are selected
     *
     * @param filterCheckBoxes list of filters
     * @return List of strings of active filters
     */
    public static List<String> getSelectedItems(List<FilterCheckBox> filterCheckBoxes) {
        List<String> selectedItems = new ArrayList<>();

        for (int i = 0; i < filterCheckBoxes.size(); i++) {
            if (filterCheckBoxes.get(i).isSelected)
                selectedItems.add(filterCheckBoxes.get(i).getName());
        }

        return selectedItems;
    }

    /**
     * Constructors
     */

    public FilterCheckBox(String name) {
        this.name = name;
        this.isSelected = false;
    }

    public FilterCheckBox(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    /**
     * Getters and setters
     */

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
