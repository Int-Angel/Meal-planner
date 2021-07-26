package com.example.mealplanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilteringViewModel extends ViewModel {
    private MutableLiveData<String> queryName = new MutableLiveData<>();

    public void setQueryName(String query){
        queryName.setValue(query);
    }

    public LiveData<String> getQueryName(){
        return queryName;
    }
}
