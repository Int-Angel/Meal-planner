package com.example.mealplanner.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.models.ShoppingList;
import com.example.mealplanner.models.ShoppingListItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class ShoppingListListAdapter extends RecyclerView.Adapter<ShoppingListListAdapter.ViewHolder>
        implements IAdapter {

    public interface ShoppingListListAdapterListener {
        void openShoppingList(ShoppingList shoppingList);
    }

    private final static String TAG = "ShoppingListListAdapter";

    private Context context;
    private List<ShoppingList> shoppingLists;
    private ShoppingListListAdapterListener listener;

    public ShoppingListListAdapter(Context context, List<ShoppingList> shoppingLists, ShoppingListListAdapterListener listener) {
        this.context = context;
        this.shoppingLists = shoppingLists;
        this.listener = listener;
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

    @Override
    public void deleteItem(int position) {
        ShoppingList list = shoppingLists.get(position);
        shoppingLists.remove(position);
        notifyItemRemoved(position);

        deleteShoppingListItems(list);
    }

    private void deleteShoppingListItems(ShoppingList shoppingList) {
        ParseQuery<ShoppingListItem> query = ParseQuery.getQuery("ShoppingListItems");
        query.whereEqualTo(ShoppingListItem.KEY_SHOPPING_LIST, shoppingList);
        query.findInBackground(new FindCallback<ShoppingListItem>() {
            @Override
            public void done(List<ShoppingListItem> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error couldn't delete shopping list items", e);
                    return;
                }
                ParseObject.deleteAllInBackground(objects);
                Log.i(TAG, "Shopping list items deleted");
                shoppingList.deleteInBackground();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ShoppingList bindedShoppingList;

        private TextView tvStartDateItem;
        private TextView tvEndDateItem;
        private TextView tvShoppingListNameItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvStartDateItem = itemView.findViewById(R.id.tvStartDateItem);
            tvEndDateItem = itemView.findViewById(R.id.tvEndDateItem);
            tvShoppingListNameItem = itemView.findViewById(R.id.tvShoppingListNameItem);

            itemView.setOnClickListener(this);
        }

        public void bind(ShoppingList shoppingList) {
            bindedShoppingList = shoppingList;

            tvStartDateItem.setText(getStringDate(shoppingList.getStartDate()));
            tvEndDateItem.setText(getStringDate(shoppingList.getEndDate()));
            tvShoppingListNameItem.setText(shoppingList.getName());
        }

        private String getStringDate(Date date) {
            return DateFormat.format("yyyy.MM.dd", date).toString();
        }

        @Override
        public void onClick(View v) {
            listener.openShoppingList(bindedShoppingList);
        }
    }
}
