package com.example.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mealplanner.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * View pager adapter to show the recipe steps inside the recipe details fragment
 */
public class StepsAdapter extends PagerAdapter {
    private final Context context;
    private final List<String> steps;

    public StepsAdapter(Context context, List<String> steps){
        this.context = context;
        this.steps = steps;
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_step,null);

        TextView tvStepNumberItem = view.findViewById(R.id.tvStepNumberItem);
        TextView tvStepItem = view.findViewById(R.id.tvStepItem);

        tvStepNumberItem.setText("" + (position + 1));
        tvStepItem.setText(steps.get(position));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
