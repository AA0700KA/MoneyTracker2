package com.andrewbondarenko.moneytracker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrewbondarenko.moneytracker.PieChartView;
import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.domain.Category;
import com.andrewbondarenko.moneytracker.domain.Transaction;

import java.util.List;

public class StatisticFragment extends Fragment{

    public static final String TAG = StatisticFragment.class.getSimpleName();

    private PieChartView pieChartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_fragment, null);
        pieChartView = (PieChartView) view.findViewById(R.id.piechart);
        List<Category> categories = Category.initData();
        float[] dataPoints = new float[categories.size()];

        int index = 0;
        for (Category category : categories) {
            List<Transaction> transactions = category.transactions();
            dataPoints[index] = transactions.size();
            index++;
        }

        pieChartView.setDataPoints(dataPoints);
        return view;
    }

}
