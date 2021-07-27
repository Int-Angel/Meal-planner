package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.models.FilterCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FilterCheckBoxAdapter extends RecyclerView.Adapter<FilterCheckBoxAdapter.ViewHolder> {

    private Context context;
    private List<FilterCheckBox> filterCheckBoxes;

    public FilterCheckBoxAdapter(Context context, List<FilterCheckBox> filterCheckBoxes){
        this.context = context;
        this.filterCheckBoxes = filterCheckBoxes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View filterView = LayoutInflater.from(context).inflate(R.layout.item_filter_checkbox, parent, false);
        return new ViewHolder(filterView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(filterCheckBoxes.get(position));
    }

    @Override
    public int getItemCount() {
        return filterCheckBoxes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private FilterCheckBox bindedItem;
        private CheckBox cbItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cbItem = itemView.findViewById(R.id.cbItem);
            cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    bindedItem.setSelected(isChecked);
                }
            });
        }

        public void bind(FilterCheckBox item) {
            bindedItem = item;

            cbItem.setText(item.getName());
            cbItem.setChecked(item.isSelected());
        }
    }
}
