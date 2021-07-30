package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.models.ShoppingListItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter to show all the shopping list items inside an aisle
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>
        implements IAdapterSwipeToDelete {

    /**
     * Interface to communicate that a item has been deleted or edited
     */
    public interface ShoppingListAdapterListener {
        void deletedItem(int position);

        void editItem(int position, String oldAisle, ShoppingListItem item);
    }

    private Context context;
    private List<ShoppingListItem> items;
    private ShoppingListAdapterListener listener;

    public ShoppingListAdapter(Context context, List<ShoppingListItem> items, ShoppingListAdapterListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void deleteItem(int position) {
        ShoppingListItem item = items.get(position);
        items.remove(position);
        notifyItemRemoved(position);
        item.deleteInBackground();

        listener.deletedItem(position);
    }

    /**
     * Shopping list item view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private ShoppingListItem bindedItem;

        private CheckBox cbItemChecked;
        private TextView tvItem;
        private TextView tvItemAmount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cbItemChecked = itemView.findViewById(R.id.cbItemChecked);
            tvItem = itemView.findViewById(R.id.tvItem);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.editItem(getAdapterPosition(), bindedItem.getAisle(), bindedItem);
                    return true;
                }
            });
        }

        /**
         * Update de view inside of the view holder with this data
         * @param item
         */
        public void bind(ShoppingListItem item) {
            bindedItem = item;

            cbItemChecked.setChecked(item.isChecked());
            tvItem.setText(item.getName());
            tvItemAmount.setText(item.getAmount() + " " + item.getUnit());

            cbItemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateItemChecked(isChecked);
                }
            });
        }

        /**
         * Updates the isChecked property of an item
         * @param isChecked
         */
        private void updateItemChecked(boolean isChecked) {
            bindedItem.setChecked(isChecked);
            bindedItem.saveInBackground();
        }
    }
}
