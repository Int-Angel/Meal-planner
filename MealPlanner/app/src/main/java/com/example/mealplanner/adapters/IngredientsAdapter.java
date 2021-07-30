package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter that shows all the ingredients of a recipe in the CreateRecipeFragment class
 */
public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder>
        implements IAdapterSwipeToDelete {

    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    private Context context;
    private List<String> ingredients;
    private OnLongClickListener onLongClickListener;

    public IngredientsAdapter(Context context, List<String> ingredients, OnLongClickListener onLongClickListener) {
        this.context = context;
        this.ingredients = ingredients;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View ingredientView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(ingredientView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public void deleteItem(int position) {
        ingredients.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * ingredient view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
        }

        /**
         * Update de view inside of the view holder with this data
         * @param item
         */
        public void bind(String item) {
            tvItem.setText("• " + item);
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
