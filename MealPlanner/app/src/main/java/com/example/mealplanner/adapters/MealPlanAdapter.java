package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;
import com.example.mealplanner.models.MealPlan;
import com.example.mealplanner.models.Recipe;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.ViewHolder> {

    private Context context;
    private List<MealPlan> mealPlan;

    public MealPlanAdapter(Context context, List<MealPlan> mealPlan) {
        this.context = context;
        this.mealPlan = mealPlan;
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

    class ViewHolder extends RecyclerView.ViewHolder {

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

        public void bind(MealPlan meal) {
            bindedMeal = meal;

            tvRecipeTitleItem.setText(meal.getRecipe().getString(Recipe.KEY_TITLE));

            Glide.with(context)
                    .load(meal.getRecipe().getString(Recipe.KEY_IMAGE_URL))
                    .transform(new CenterCrop(),new RoundedCorners(1000))
                    .into(ivRecipeImageItem);

            tvQuantity.setText(meal.getQuantity() + "");

            setUpOnClickListeners();
        }

        private void setUpOnClickListeners(){
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
        }

        private void changeQuantity(int n){
            int newQuantity = bindedMeal.getQuantity() + n;
            if(newQuantity >= 1){
                bindedMeal.setQuantity(newQuantity);
                tvQuantity.setText(newQuantity + "");
                bindedMeal.saveInBackground();
            }
        }
    }
}
