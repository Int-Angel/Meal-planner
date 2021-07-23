package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.models.ShoppingListItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingListItem> items;

    public ShoppingListAdapter(Context context, List<ShoppingListItem> items){
        this.context = context;
        this.items = items;
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
        }

        public void bind(ShoppingListItem item) {
            bindedItem = item;

            cbItemChecked.setChecked(item.isChecked());
            tvItem.setText(item.getName());
            tvItemAmount.setText(item.getAmount() + " " + item.getUnit());
        }
    }
}