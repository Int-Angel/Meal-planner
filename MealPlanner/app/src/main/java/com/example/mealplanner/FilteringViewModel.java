package com.example.mealplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class FilteringViewModel extends ViewModel {
    private MutableLiveData<String> queryName = new MutableLiveData<>();

    private MutableLiveData<Boolean> activeCuisines = new MutableLiveData<>();
    private MutableLiveData<List<String>> cuisines = new MutableLiveData<>();

    private MutableLiveData<Boolean> activeMealTypes = new MutableLiveData<>();
    private MutableLiveData<List<String>> mealTypes = new MutableLiveData<>();

    private MutableLiveData<Boolean> activeMaxTimeReady = new MutableLiveData<>();
    private MutableLiveData<Integer> maxTimeReady = new MutableLiveData<>();

    private MutableLiveData<Boolean> activeCalories = new MutableLiveData<>();
    private MutableLiveData<Integer> minCalories = new MutableLiveData<>();
    private MutableLiveData<Integer> maxCalories = new MutableLiveData<>();

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

    public void setMealTypes(List<String> mealTypes) {
        this.mealTypes.setValue(mealTypes);
    }

    public LiveData<List<String>> getMealTypes() {
        return mealTypes;
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
