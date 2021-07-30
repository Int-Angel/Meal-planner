package com.example.mealplanner.adapters;

/**
 * Interface that adapters should implement to be able to swipe and delete items
 */
public interface IAdapterSwipeToDelete {
    void deleteItem(int position);
}
