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

/**
 * Adapter to show all the shopping list aisle, this adapter uses ShoppingListAdapter to show all
 * the shopping list items inside an aisle
 */
public class ShoppingListAisleAdapter extends RecyclerView.Adapter<ShoppingListAisleAdapter.ViewHolder> {

    /**
     * Interface to communicate that a item needs to be edited
     */
    public interface ShoppingListAisleAdapterListener {
        void editItem(int position, String oldAisle, ShoppingListItem item);
    }

    private final ShoppingListAisleAdapterListener listener;
    private final Context context;
    private final HashMap<String, List<ShoppingListItem>> aisles;
    private final List<String> aislesName;

    public ShoppingListAisleAdapter(Context context, HashMap<String, List<ShoppingListItem>> aisles, List<String> aislesName, ShoppingListAisleAdapterListener listener) {
        this.context = context;
        this.aisles = aisles;
        this.aislesName = aislesName;
        this.listener = listener;
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

    /**
     * Shopping list aisle view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private List<ShoppingListItem> bindedShoppingListItems;

        private TextView tvAisle;
        private RecyclerView rvItems;
        private ShoppingListAdapter adapter;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvAisle = itemView.findViewById(R.id.tvAisle);
            rvItems = itemView.findViewById(R.id.rvItems);

        }

        /**
         * Update de view inside of the view holder with this data
         * @param aisle
         * @param shoppingListItems
         */
        public void bind(String aisle, List<ShoppingListItem> shoppingListItems) {
            bindedShoppingListItems = shoppingListItems;

            tvAisle.setText(aisle);

            adapter = new ShoppingListAdapter(context, shoppingListItems, new ShoppingListAdapter.ShoppingListAdapterListener() {
                @Override
                public void deletedItem(int position) {
                    if (bindedShoppingListItems.isEmpty()) {
                        aislesName.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                }

                @Override
                public void editItem(int position, String oldAisle, ShoppingListItem item) {
                    listener.editItem(position, oldAisle, item);
                }

            });

            rvItems.setAdapter(adapter);
            rvItems.setLayoutManager(new LinearLayoutManager(context));

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
            itemTouchHelper.attachToRecyclerView(rvItems);
        }
    }

}
