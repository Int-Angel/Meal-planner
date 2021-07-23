package com.example.mealplanner;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.adapters.IAdapter;
import com.example.mealplanner.adapters.MealPlanAdapter;

import org.jetbrains.annotations.NotNull;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private IAdapter adapter;

    public SwipeToDeleteCallback(IAdapter adapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
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
