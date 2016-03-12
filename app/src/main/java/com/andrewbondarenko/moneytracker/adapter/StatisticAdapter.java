package com.andrewbondarenko.moneytracker.adapter;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.domain.Category;

import java.util.List;

public class StatisticAdapter extends SelectedAdapter<StatisticAdapter.ColorHolder>{

    private List<Category> categories;
    private List<Integer> colors;
    private List<Integer> price;

    public StatisticAdapter(List<Category> categories, List<Integer> colors, List<Integer> price) {
        this.categories = categories;
        this.colors = colors;
        this.price = price;
    }

    @Override
    public ColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagram_color_item, parent, false);

        return new ColorHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorHolder holder, int position) {
        Category category = categories.get(position);
        int color = colors.get(position);
        if (price == null)
           holder.statisticText.setText(category.getName());
        else holder.statisticText.setText(category.getName() + ": " + price.get(position));
        holder.statisticColor.setBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ColorHolder extends RecyclerView.ViewHolder {

        protected TextView statisticText;
        protected View statisticColor;

        public ColorHolder(View itemView) {
            super(itemView);
            statisticColor = itemView.findViewById(R.id.color_category);
            statisticText = (TextView) itemView.findViewById(R.id.color_category_text);
        }

    }

}
