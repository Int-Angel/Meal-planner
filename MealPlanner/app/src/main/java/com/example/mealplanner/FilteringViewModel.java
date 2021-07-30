package com.example.mealplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * This class it's used for communication between the recipe fragments and the filter fragment,
 * all the filters are saved here in this class.
 */
public class FilteringViewModel extends ViewModel {

    private MutableLiveData<String> queryName = new MutableLiveData<>(); // title of the recipe to search

    private MutableLiveData<Boolean> activeCuisines = new MutableLiveData<>(); // is active the cuisine filter?
    private MutableLiveData<List<String>> cuisines = new MutableLiveData<>(); // list of cuisines selected

    private MutableLiveData<Boolean> activeMealTypes = new MutableLiveData<>(); // is active the meal type filter?
    private MutableLiveData<String> mealType = new MutableLiveData<>(); // Just one, API doesn't support more

    private MutableLiveData<Boolean> activeMaxTimeReady = new MutableLiveData<>(); // is active the time filter?
    private MutableLiveData<Integer> maxTimeReady = new MutableLiveData<>(); // max time to get ready a meal

    private MutableLiveData<Boolean> activeCalories = new MutableLiveData<>(); // is active the calories filter?
    private MutableLiveData<Integer> minCalories = new MutableLiveData<>();
    private MutableLiveData<Integer> maxCalories = new MutableLiveData<>();

    /**
     * Setters and getters
     */

    public void setQueryName(String query) {
        queryName.setValue(query);
    }

    public LiveData<String> getQueryName() {
        return queryName;
    }

    public void setActiveCuisines(boolean activeCuisines) {
        this.activeCuisines.setValue(activeCuisines);
    }

    public LiveData<Boolean> getActiveCuisines() {
        return activeCuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines.setValue(cuisines);
    }

    public LiveData<List<String>> getCuisines() {
        return cuisines;
    }

    public void setActiveMealTypes(boolean activeMealTypes) {
        this.activeMealTypes.setValue(activeMealTypes);
    }

    public LiveData<Boolean> getActiveMealTypes() {
        return activeMealTypes;
    }

    public void setMealTypes(String mealType) {
        this.mealType.setValue(mealType);
    }

    public LiveData<String> getMealTypes() {
        return mealType;
    }

    public void setActiveMaxTimeReady(Boolean active) {
        this.activeMaxTimeReady.setValue(active);
    }

    public LiveData<Boolean> getActiveMaxTimeReady() {
        return activeMaxTimeReady;
    }

    public void setMaxTimeReady(int time) {
        maxTimeReady.setValue(time);
    }

    public LiveData<Integer> getMaxTimeReady() {
        return maxTimeReady;
    }

    public void setActiveCalories(Boolean active) {
        activeCalories.setValue(active);
    }

    public LiveData<Boolean> getActiveCalories() {
        return activeCalories;
    }

    public void setMinCalories(int calories) {
        minCalories.setValue(calories);
    }

    public LiveData<Integer> getMinCalories() {
        return minCalories;
    }

    public void setMaxCalories(int calories) {
        maxCalories.setValue(calories);
    }

    public LiveData<Integer> getMaxCalories() {
        return maxCalories;
    }
}
