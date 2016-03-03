package com.andrewbondarenko.moneytracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectedAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private SparseBooleanArray selected;

    public SelectedAdapter() {
        selected = new SparseBooleanArray();
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public void toggleSelected(int position) {
        if (selected.get(position, false)) {
            selected.delete(position);
        } else {
            selected.put(position, true);
        }

        notifyItemChanged(position);
    }

    public void clear() {
        List<Integer> selectedItems = getSelectedItems();
        selected.clear();

        for (Integer i : selectedItems) {
            notifyItemChanged(i);
        }

    }

    public int getSelectedItemsCount() {
        return selected.size();
    }

    public List<Integer> getSelectedItems() {

        List<Integer> items = new ArrayList<>(getSelectedItemsCount());

        for (int i = 0; i < getSelectedItemsCount(); i++) {
            items.add(selected.keyAt(i));
        }

        return items;
    }

}
