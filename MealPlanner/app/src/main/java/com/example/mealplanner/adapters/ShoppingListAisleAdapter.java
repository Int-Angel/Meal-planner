package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.SwipeToDeleteCallback;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingListAisleAdapter extends RecyclerView.Adapter<ShoppingListAisleAdapter.ViewHolder> {

    private Context context;
    private HashMap<String, List<ShoppingListItem>> aisles;
    private List<String> aislesName;

    public ShoppingListAisleAdapter(Context context, HashMap<String, List<ShoppingListItem>> aisles, List<String> aislesName) {
        this.context = context;
        this.aisles = aisles;
        this.aislesName = aislesName;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_shopping_list_aisle, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String aisle = aislesName.get(position);
        List<ShoppingListItem> shoppingListItems = new ArrayList<>(aisles.get(aisle));

        holder.bind(aisle, shoppingListItems);
    }

    @Override
    public int getItemCount() {
        return aislesName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAisle;
        private RecyclerView rvItems;
        private ShoppingListAdapter adapter;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvAisle = itemView.findViewById(R.id.tvAisle);
            rvItems = itemView.findViewById(R.id.rvItems);

        }

        public void bind(String aisle, List<ShoppingListItem> shoppingListItems) {
            tvAisle.setText(aisle);

            adapter = new ShoppingListAdapter(context, shoppingListItems);
            rvItems.setAdapter(adapter);
            rvItems.setLayoutManager(new LinearLayoutManager(context));

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
            itemTouchHelper.attachToRecyclerView(rvItems);
        }
    }

}
