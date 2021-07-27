package com.example.mealplanner.models;

import java.util.ArrayList;
import java.util.List;

public class FilterCheckBox {

    private String name;
    private boolean isSelected;

    public static List<String> getSelectedItems(List<FilterCheckBox> filterCheckBoxes) {
        List<String> selectedItems = new ArrayList<>();

        for (int i = 0; i < filterCheckBoxes.size(); i++) {
            if (filterCheckBoxes.get(i).isSelected)
                selectedItems.add(filterCheckBoxes.get(i).getName());
        }

        return selectedItems;
    }

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
