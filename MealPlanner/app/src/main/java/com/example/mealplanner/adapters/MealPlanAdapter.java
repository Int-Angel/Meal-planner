package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.Recipe;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter to show all the recipes of a meal plan
 */
public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.ViewHolder>
        implements IAdapterSwipeToDelete {

    /**
     * Interface to open a recipe details
     */
    public interface MealPlanAdapterListener {
        void openDetails(IRecipe recipe, int index);
        void updateShoppingList();
    }

    private Context context;
    private List<MealPlan> mealPlan;
    private MealPlanAdapterListener listener;

    public MealPlanAdapter(Context context, List<MealPlan> mealPlan, MealPlanAdapterListener listener) {
        this.context = context;
        this.mealPlan = mealPlan;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View mealView = LayoutInflater.from(context).inflate(R.layout.item_meal_plan, parent, false);
        return new ViewHolder(mealView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(mealPlan.get(position));
    }

    @Override
    public int getItemCount() {
        return mealPlan.size();
    }

    @Override
    public void deleteItem(int position) {
        MealPlan meal = mealPlan.get(position);
        mealPlan.remove(position);
        meal.deleteInBackground();
        notifyItemRemoved(position);
        Toast.makeText(context, "Meal removed", Toast.LENGTH_SHORT).show(); //Not showing :c
        listener.updateShoppingList();
    }

    /**
     * meal plan view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MealPlan bindedMeal;

        private TextView tvRecipeTitleItem;
        private ImageButton ibtnSubtract;
        private ImageButton ibtnAdd;
        private TextView tvQuantity;
        private ImageView ivRecipeImageItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvRecipeTitleItem = itemView.findViewById(R.id.tvRecipeTitleItem);
            ibtnSubtract = itemView.findViewById(R.id.ibtnSubtract);
            ibtnAdd = itemView.findViewById(R.id.ibtnAdd);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivRecipeImageItem = itemView.findViewById(R.id.ivRecipeImageItem);
        }

        /**
         * Update de view inside of the view holder with this data
         * @param meal
         */
        public void bind(MealPlan meal) {
            bindedMeal = meal;
            tvRecipeTitleItem.setText(meal.getRecipe().getString(Recipe.KEY_TITLE));

            Glide.with(context)
                    .load(meal.getRecipe().getString(Recipe.KEY_IMAGE_URL))
                    .transform(new CenterCrop(), new RoundedCorners(1000))
                    .into(ivRecipeImageItem);

            tvQuantity.setText(meal.getQuantity() + "");

            setUpOnClickListeners();
        }

        /**
         * sets up all the view holder onClickListeners
         */
        private void setUpOnClickListeners() {
            ibtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeQuantity(1);
                }
            });

            ibtnSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeQuantity(-1);
                }
            });

            itemView.setOnClickListener(this);
        }

        /**
         * Updates the quantity of the recipe
         * @param n
         */
        private void changeQuantity(int n) {
            int newQuantity = bindedMeal.getQuantity() + n;
            if (newQuantity >= 1) {
                bindedMeal.setQuantity(newQuantity);
                tvQuantity.setText(newQuantity + "");
                bindedMeal.saveInBackground();
                listener.updateShoppingList();
            }
        }

        /**
         * opens the recipe details
         */
        private void openRecipeDetails() {
            IRecipe recipe = SavedRecipesManager.getRecipeById(bindedMeal.getRecipe().getString(Recipe.KEY_ID));
            listener.openDetails(recipe, getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            openRecipeDetails();
        }
    }
}
