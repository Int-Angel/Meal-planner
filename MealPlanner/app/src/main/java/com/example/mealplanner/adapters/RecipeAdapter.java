package com.example.mealplanner.adapters;

import android.content.Context;
import android.service.autofill.SaveCallback;
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
import com.example.mealplanner.models.IRecipe;
import com.example.mealplanner.models.OnlineRecipe;
import com.example.mealplanner.models.Recipe;

import java.text.ParseException;
import java.util.List;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private static final String TAG = "RecipeAdapter";

    public interface RecipeAdapterListener {
        void openDetails(IRecipe recipe);
    }

    private Context context;
    private List<IRecipe> recipes;
    private RecipeAdapterListener listener;

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private IRecipe bindedRecipe;

        private ImageView ivRecipeImageItem;
        private ImageButton ibtnSaveRecipeItem;
        private TextView tvRecipeTitleItem;
        private TextView tvCaloriesItem;
        private TextView tvCuisineTypeItem;
        private TextView tvMealTypeItem;
        private CardView cvItemContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipeImageItem = itemView.findViewById(R.id.ivRecipeImageItem);
            ibtnSaveRecipeItem = itemView.findViewById(R.id.ibtnSaveRecipeItem);
            tvRecipeTitleItem = itemView.findViewById(R.id.tvRecipeTitleItem);
            tvCaloriesItem = itemView.findViewById(R.id.tvCaloriesItem);
            tvCuisineTypeItem = itemView.findViewById(R.id.tvCuisineTypeItem);
            tvMealTypeItem = itemView.findViewById(R.id.tvMealTypeItem);
            cvItemContainer = itemView.findViewById(R.id.cvItemContainer);

            ivRecipeImageItem.setOnClickListener(this);
            cvItemContainer.setOnClickListener(this);
            ibtnSaveRecipeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyRecipe();
                }
            });
        }

        public void bind(IRecipe recipe) {
            bindedRecipe = recipe;

            if(recipe instanceof OnlineRecipe){
                ibtnSaveRecipeItem.setVisibility(View.VISIBLE);
            }else{
                ibtnSaveRecipeItem.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .transform(new RoundedCorners(200))
                    .into(ivRecipeImageItem);

            tvRecipeTitleItem.setText(recipe.getTitle());
            tvCaloriesItem.setText(recipe.getCalories());

            if(recipe.getCuisineType().equals(""))
                tvCuisineTypeItem.setVisibility(View.GONE);
            else
                tvCuisineTypeItem.setText(recipe.getCuisineType());

            if(recipe.getMealType().equals(""))
                tvMealTypeItem.setVisibility(View.GONE);
            else
                tvMealTypeItem.setText(recipe.getMealType());
        }

        private void copyRecipe(){
            Recipe recipe = Recipe.createRecipe((OnlineRecipe) bindedRecipe);

            recipe.saveInBackground(new com.parse.SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if(e!= null){
                        Log.e(TAG,"Error while saving recipe!",e);
                        Toast.makeText(context,"Error while saving recipe!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG,"Recipe saved!");
                    Toast.makeText(context,"Recipe saved!",Toast.LENGTH_SHORT).show();
                }
            });
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
