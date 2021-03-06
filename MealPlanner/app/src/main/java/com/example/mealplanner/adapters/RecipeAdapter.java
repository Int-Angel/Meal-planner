package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.OnlineRecipe;

import java.util.List;

/**
 * Adapter to show IRecipe in a recycler view and open it's details
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private static final String TAG = "RecipeAdapter";

    /**
     * Interface to open recipe details
     */
    public interface RecipeAdapterListener {
        void openDetails(IRecipe recipe, int index);
    }

    private final Context context;
    private final List<IRecipe> recipes;
    private final RecipeAdapterListener listener;

    public RecipeAdapter(Context context, List<IRecipe> recipes, RecipeAdapterListener listener) {
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recipeView = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Recipe view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private IRecipe bindedRecipe;

        private ImageView ivRecipeImageItem;
        private ImageButton ibtnSaveRecipeItem;
        private TextView tvRecipeTitleItem;
        private TextView tvCaloriesItem;
        private TextView tvCuisineTypeItem;
        private TextView tvMealTypeItem;
        private CardView cvItemContainer;
        private LottieAnimationView animationViewLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipeImageItem = itemView.findViewById(R.id.ivRecipeImageItem);
            ibtnSaveRecipeItem = itemView.findViewById(R.id.ibtnSaveRecipeItem);
            tvRecipeTitleItem = itemView.findViewById(R.id.tvRecipeTitleItem);
            tvCaloriesItem = itemView.findViewById(R.id.tvCaloriesItem);
            tvCuisineTypeItem = itemView.findViewById(R.id.tvCuisineTypeItem);
            tvMealTypeItem = itemView.findViewById(R.id.tvMealTypeItem);
            cvItemContainer = itemView.findViewById(R.id.cvItemContainer);
            animationViewLike = itemView.findViewById(R.id.animationViewLike);

            ivRecipeImageItem.setOnClickListener(this);
            cvItemContainer.setOnClickListener(this);
            ibtnSaveRecipeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyRecipe();
                }
            });
        }

        /**
         * Update de view inside of the view holder with this data
         *
         * @param recipe
         */
        public void bind(IRecipe recipe) {
            bindedRecipe = recipe;

            if (recipe instanceof OnlineRecipe) {
                ibtnSaveRecipeItem.setVisibility(View.VISIBLE);
                ibtnSaveRecipeItem.setSelected(((OnlineRecipe) recipe).isSaved());
            } else {
                ibtnSaveRecipeItem.setVisibility(View.GONE);
                animationViewLike.setVisibility(View.GONE);
            }

            tvMealTypeItem.setText(recipe.getDishType());

            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(1000))
                    .into(ivRecipeImageItem);

            tvRecipeTitleItem.setText(recipe.getTitle());
            tvCaloriesItem.setText(recipe.getCalories());

            if (recipe.getCuisineType().equals(""))
                tvCuisineTypeItem.setVisibility(View.GONE);
            else
                tvCuisineTypeItem.setText(recipe.getCuisineType());

        }

        /**
         * saves or unsaves a online recipe
         */
        private void copyRecipe() {
            if (!((OnlineRecipe) bindedRecipe).isSaved()) {
                SavedRecipesManager.getInstance().saveRecipe((OnlineRecipe) bindedRecipe);
                ibtnSaveRecipeItem.setSelected(true);
                ((OnlineRecipe) bindedRecipe).setSaved(true);

                animationViewLike.setVisibility(View.VISIBLE);
                animationViewLike.playAnimation();
            } else {
                SavedRecipesManager.getInstance().unSaveRecipeById(bindedRecipe.getId());
                ibtnSaveRecipeItem.setSelected(false);
                ((OnlineRecipe) bindedRecipe).setSaved(false);
                animationViewLike.setVisibility(View.GONE);
            }
        }

        /**
         * Opens the recipe details
         */
        private void openDetails() {
            listener.openDetails(bindedRecipe, getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            openDetails();
        }
    }
}
