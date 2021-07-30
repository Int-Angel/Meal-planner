package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.adapters.IAdapterSwipeToDelete;

import org.jetbrains.annotations.NotNull;

/**
 * This class deletes a recyclerview item when the user swipes to one side that item
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private IAdapterSwipeToDelete adapter;

    public SwipeToDeleteCallback(IAdapterSwipeToDelete adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.deleteItem(position);
    }
}
