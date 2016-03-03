package com.andrewbondarenko.moneytracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.domain.Category;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.categoty_text);
        Category category = getItem(position);
        textView.setText(category.getName());

        return view;
    }

}
