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

import com.airbnb.lottie.LottieAnimationView;
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

/**
 * Adapter that shows a list of shopping lists
 */
public class ShoppingListManagerAdapter extends RecyclerView.Adapter<ShoppingListManagerAdapter.ViewHolder>
        implements IAdapterSwipeToDelete {

    /**
     * Interface to communicate that the user wants to open the details of a shopping list
     */
    public interface ShoppingListListAdapterListener {
        void openShoppingList(ShoppingList shoppingList);

        void shoppingListRemoved();
    }

    private final static String TAG = "ShoppingListListAdapter";

    private final Context context;
    private final List<ShoppingList> shoppingLists;
    private final ShoppingListListAdapterListener listener;

    public ShoppingListManagerAdapter(Context context, List<ShoppingList> shoppingLists, ShoppingListListAdapterListener listener) {
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

        listener.shoppingListRemoved();

        deleteShoppingListItems(list);
    }

    /**
     * Deletes all the shopping list items from the database
     *
     * @param shoppingList
     */
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

    /**
     * Shopping list view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ShoppingList bindedShoppingList;

        private TextView tvStartDateItem;
        private TextView tvEndDateItem;
        private TextView tvShoppingListNameItem;
        private TextView tvOutdated;
        private LottieAnimationView animationView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvStartDateItem = itemView.findViewById(R.id.tvStartDateItem);
            tvEndDateItem = itemView.findViewById(R.id.tvEndDateItem);
            tvShoppingListNameItem = itemView.findViewById(R.id.tvShoppingListNameItem);
            tvOutdated = itemView.findViewById(R.id.tvOutdated);
            animationView = itemView.findViewById(R.id.animationView);

            itemView.setOnClickListener(this);
        }

        /**
         * Update de view inside of the view holder with this data
         *
         * @param shoppingList
         */
        public void bind(ShoppingList shoppingList) {
            bindedShoppingList = shoppingList;

            tvStartDateItem.setText(getStringDate(shoppingList.getStartDate()));
            tvEndDateItem.setText(getStringDate(shoppingList.getEndDate()));
            tvShoppingListNameItem.setText(shoppingList.getName());

            if (shoppingList.getUpdateMessage()) {
                tvOutdated.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.VISIBLE);
            } else {
                tvOutdated.setVisibility(View.GONE);
                animationView.setVisibility(View.GONE);
            }
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
