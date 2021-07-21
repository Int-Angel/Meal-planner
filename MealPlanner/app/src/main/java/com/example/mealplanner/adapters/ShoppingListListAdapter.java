package com.example.mealplanner.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.models.ShoppingList;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class ShoppingListListAdapter extends RecyclerView.Adapter<ShoppingListListAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingList> shoppingLists;

    public ShoppingListListAdapter(Context context, List<ShoppingList> shoppingLists) {
        this.context = context;
        this.shoppingLists = shoppingLists;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View shoppingView = LayoutInflater.from(context).inflate(R.layout.item_shopping_list_list, parent, false);
        return new ViewHolder(shoppingView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(shoppingLists.get(position));
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ShoppingList bindedShoppingList;

        private TextView tvStartDateItem;
        private TextView tvEndDateItem;
        private TextView tvShoppingListNameItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvStartDateItem = itemView.findViewById(R.id.tvStartDateItem);
            tvEndDateItem = itemView.findViewById(R.id.tvEndDateItem);
            tvShoppingListNameItem = itemView.findViewById(R.id.tvShoppingListNameItem);
        }

        public void bind(ShoppingList shoppingList) {
            bindedShoppingList = shoppingList;

            tvStartDateItem.setText(getStringDate(shoppingList.getStartDate()));
            tvEndDateItem.setText(getStringDate(shoppingList.getEndDate()));
            tvShoppingListNameItem.setText(shoppingList.getName());
        }

        private String getStringDate(Date date){
            return DateFormat.format("yyyy.MM.dd", date).toString();
        }
    }
}
