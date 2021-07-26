package com.example.mealplanner.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.SavedRecipesManager;
import com.example.mealplanner.models.AddRecipe;
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddRecipeAdapter extends RecyclerView.Adapter<AddRecipeAdapter.ViewHolder> {

    private static final String TAG = "AddRecipeAdapter";

    public interface AddRecipeAdapterListener {
        void addToPlan(IRecipe recipe, int index);
    }

    private Context context;
    private List<AddRecipe> recipes;
    private AddRecipeAdapterListener listener;

    public AddRecipeAdapter(Context context, List<AddRecipe> recipes, AddRecipeAdapterListener listener) {
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;

        Log.i(TAG, "Size: " + recipes.size());
    }

    @NonNull
    @Override
    public AddRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recipeView = LayoutInflater.from(context).inflate(R.layout.item_add_recipe, parent, false);
        return new AddRecipeAdapter.ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AddRecipe bindedRecipe;

        private ImageView ivRecipeImageItem;
        private ImageButton ibtnAddRecipeItem;
        private TextView tvRecipeTitleItem;
        private TextView tvCaloriesItem;
        private TextView tvCuisineTypeItem;
        private TextView tvMealTypeItem;
        private CardView cvItemContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipeImageItem = itemView.findViewById(R.id.ivRecipeImageItem);
            ibtnAddRecipeItem = itemView.findViewById(R.id.ibtnAddRecipeItem);
            tvRecipeTitleItem = itemView.findViewById(R.id.tvRecipeTitleItem);
            tvCaloriesItem = itemView.findViewById(R.id.tvCaloriesItem);
            tvCuisineTypeItem = itemView.findViewById(R.id.tvCuisineTypeItem);
            tvMealTypeItem = itemView.findViewById(R.id.tvMealTypeItem);
            cvItemContainer = itemView.findViewById(R.id.cvItemContainer);

            ibtnAddRecipeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToPlan();
                }
            });
        }

        public void bind(AddRecipe addRecipe) {
            bindedRecipe = addRecipe;
            Recipe recipe = addRecipe.getRecipe();

            ibtnAddRecipeItem.setSelected(addRecipe.isAdded());

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

        private void addToPlan(){
            if(!bindedRecipe.isAdded()){
                listener.addToPlan(bindedRecipe.getRecipe(), getAdapterPosition());
                ibtnAddRecipeItem.setSelected(true);
                bindedRecipe.setAdded(true);
            }
        }

    }
}

