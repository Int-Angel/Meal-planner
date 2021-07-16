package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealplanner.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IngredientsImagesAdapter extends RecyclerView.Adapter<IngredientsImagesAdapter.ViewHolder> {

    private final static String TAG = "IngredientsImagesAdapter";

    private Context context;
    private List<String> ingredientsImageUrl;

    public IngredientsImagesAdapter(Context context, List<String> ingredientsImageUrl) {
        this.context = context;
        this.ingredientsImageUrl = ingredientsImageUrl;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View ingredientItem = LayoutInflater.from(context).inflate(R.layout.item_ingredient_image, parent, false);
        return new ViewHolder(ingredientItem);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(ingredientsImageUrl.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredientsImageUrl.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIngredientImageItem;
        private TextView tvIngredientItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivIngredientImageItem = itemView.findViewById(R.id.ivIngredientImageItem);
            tvIngredientItem = itemView.findViewById(R.id.tvIngredientItem);
        }

        public void bind(String imageUrl){
            tvIngredientItem.setText(getIngredientName(imageUrl));

            Glide.with(context)
                    .load(imageUrl)
                    .transform(new CenterCrop(),new RoundedCorners(1000))
                    .into(ivIngredientImageItem);
        }

        private String getIngredientName(String url){
            String[] parts = url.split("/");
            int lastPos = parts.length - 1;
            String name = parts[lastPos];
            name = name.replaceAll(".jpg","");
            name = name.replaceAll(".png","");
            name = name.replace("-"," ");
            return name;
        }
    }
}
