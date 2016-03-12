package com.andrewbondarenko.moneytracker.fragment;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewbondarenko.moneytracker.PieChartView;
import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.adapter.StatisticAdapter;
import com.andrewbondarenko.moneytracker.domain.Category;
import com.andrewbondarenko.moneytracker.domain.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CountTransactionStatisticFragment extends Fragment {

    private PieChartView pieChartView;
    private RecyclerView diagramItems;
    private TextView textView;
    private String diagramType;

    public static CountTransactionStatisticFragment getInstance(String diagramType) {
        CountTransactionStatisticFragment fragment = new CountTransactionStatisticFragment();
        fragment.setDiagramType(diagramType);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.count_transactions_fragment, null);

        diagramItems = (RecyclerView) view.findViewById(R.id.designators_category);
        pieChartView = (PieChartView) view.findViewById(R.id.piechart);
        textView = (TextView) view.findViewById(R.id.count_text);
        diagramItems.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        diagramItems.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Category>>() {
            @Override
            public android.support.v4.content.Loader<List<Category>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Category>> categoryLoader = new AsyncTaskLoader<List<Category>>(getActivity()) {
                    @Override
                    public List<Category> loadInBackground() {
                        return Category.initData();
                    }
                };
                categoryLoader.forceLoad();
                return categoryLoader;
            }

            @Override
            public void onLoadFinished(android.support.v4.content.Loader<List<Category>> loader, List<Category> data) {
                switch(diagramType) {
                    case StatisticFragment.COUNT_MODE: loadCountCategories(data);
                        break;
                    case StatisticFragment.COSTS_MODE:loadCoasts(data);
                                                      textView.setText(R.string.all_spent_money);
                }
            }

            @Override
            public void onLoaderReset(android.support.v4.content.Loader<List<Category>> loader) {

            }
        });
    }

    private void loadCountCategories(List<Category> data) {
        float[] dataPoints = new float[data.size()];

        int index = 0;
        for (Category category : data) {
            dataPoints[index] = Transaction.count(category);
            index++;
        }

        pieChartView.setDataPoints(dataPoints);
        List<Integer> colors = pieChartView.getColors();
        StatisticAdapter statisticAdapter = new StatisticAdapter(data, colors, null);
        diagramItems.setAdapter(statisticAdapter);
    }

    public void setDiagramType(String diagramType) {
        this.diagramType = diagramType;
    }

    private void loadCoasts(final List<Category> categories) {

       // pieChartView.setIndexDataPoint(0);
        float[] dataPoints = new float[categories.size()];
        List<Integer> prices = new ArrayList<>();
        int index = 0;
        for (final Category category : categories) {

            List<Transaction> transactions = Transaction.findTransactionToCategory(category);

            int sum = 0;

            for (Transaction transaction : transactions) {
                sum += transaction.getSum();
            }
            prices.add(sum);
            dataPoints[index++] = sum;
        }

        pieChartView.setDataPoints(dataPoints);
        List<Integer> colorsPieChart = pieChartView.getColors();
        StatisticAdapter statisticAdapter = new StatisticAdapter(categories, colorsPieChart, prices);
        diagramItems.setAdapter(statisticAdapter);

    }

}


