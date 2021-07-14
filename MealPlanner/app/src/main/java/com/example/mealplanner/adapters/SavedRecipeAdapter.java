package com.example.mealplanner.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;
import com.parse.ParseException;
import com.parse.SaveCallback;


import java.util.List;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.ViewHolder> {

    private static final String TAG = "RecipeAdapter";

    public interface SavedRecipeAdapterListener{
        void openDetails(Recipe recipe);
    }

    private Context context;
    private List<Recipe> recipes;
    private SavedRecipeAdapterListener listener;

    public SavedRecipeAdapter(Context context, List<Recipe> recipes, SavedRecipeAdapterListener listener){
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recipeView = LayoutInflater.from(context).inflate(R.layout.item_saved_recipe, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Recipe bindedRecipe;

        private ImageView ivRecipeImageItem;
        private TextView tvRecipeTitleItem;
        private TextView tvCaloriesItem;
        private TextView tvCuisineTypeItem;
        private TextView tvMealTypeItem;
        private CardView cvItemContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipeImageItem = itemView.findViewById(R.id.ivRecipeImageItem);
            tvRecipeTitleItem = itemView.findViewById(R.id.tvRecipeTitleItem);
            tvCaloriesItem = itemView.findViewById(R.id.tvCaloriesItem);
            tvCuisineTypeItem = itemView.findViewById(R.id.tvCuisineTypeItem);
            tvMealTypeItem = itemView.findViewById(R.id.tvMealTypeItem);
            cvItemContainer = itemView.findViewById(R.id.cvItemContainer);

            ivRecipeImageItem.setOnClickListener(this);
            cvItemContainer.setOnClickListener(this);
        }

        public void bind(Recipe recipe) {
            bindedRecipe = recipe;

            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .transform(new RoundedCorners(200))
                    .into(ivRecipeImageItem);

            tvRecipeTitleItem.setText(recipe.getTitle());
            tvCaloriesItem.setText(recipe.getCaloriesText());

            if(recipe.getCuisineType().equals(""))
                tvCuisineTypeItem.setVisibility(View.GONE);
            else
                tvCuisineTypeItem.setText(recipe.getCuisineType());

            if(recipe.getMealType().equals(""))
                tvMealTypeItem.setVisibility(View.GONE);
            else
                tvMealTypeItem.setText(recipe.getMealType());
        }


        private void openDetails(){
            listener.openDetails(bindedRecipe);
        }

        @Override
        public void onClick(View v) {
            openDetails();
        }
    }

}
