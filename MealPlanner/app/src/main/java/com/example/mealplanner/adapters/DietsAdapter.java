package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DietsAdapter extends RecyclerView.Adapter<DietsAdapter.ViewHolder> {

    private final static String TAG = "DietsAdapter";

    private final Context context;
    private final List<String> diets;
    private final List<Integer> colors;

    public DietsAdapter(Context context, List<String> diets, List<Integer> colors) {
        this.context = context;
        this.diets = diets;
        this.colors = colors;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View dietView = LayoutInflater.from(context).inflate(R.layout.item_diet_type, parent, false);
        return new ViewHolder(dietView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(diets.get(position), generateColor());
    }

    @Override
    public int getItemCount() {
        return diets.size();
    }

    /**
     * This method will return a color from the colors list for the ViewHolder
     *
     * @return
     */
    public int generateColor() {
        return R.color.card_1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private String bindedDiet;

        private CardView cardView;
        private TextView tvDiet;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.dietContainer);
            tvDiet = itemView.findViewById(R.id.tvDiet);

        }

        public void bind(String diet, int color) {
            bindedDiet = diet;
            cardView.setCardBackgroundColor(color);
            tvDiet.setText(diet);
        }
    }
}
