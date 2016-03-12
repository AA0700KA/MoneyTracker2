package com.andrewbondarenko.moneytracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.domain.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StatisticCountFragment extends Fragment {

    private TextView countToday;
    private TextView countMonth;
    private TextView countYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_count_fragment, null);

        countToday = (TextView) view.findViewById(R.id.today);
        countMonth = (TextView) view.findViewById(R.id.month);
        countYear = (TextView) view.findViewById(R.id.year);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date date = new Date();
        String filterToday = new SimpleDateFormat("dd:MM:yyyy").format(date);
        String filterMonth = filterToday.split(":")[1] + ":" + filterToday.split(":")[2];
        String filterYear = filterToday.split(":")[2];

        ActiveAndroid.beginTransaction();
        try {
            sumToday(filterToday);
            sumMonth(filterMonth);
            sumYear(filterYear);
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void sumToday(String todayFilter) {
        String startingCount = todayFilter + " 00:00:00";
        String finishCount = todayFilter + " 23:59:59";

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");

         Date dateStart = null;
         Date dateFinish = null;

        try {
            dateStart = sdf.parse(startingCount);
            dateFinish = sdf.parse(finishCount);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        loadSumma(dateStart, dateFinish, 0);
    }

    private void loadSumma(final Date dateStart, final Date dateFinish, final int index) {
        getLoaderManager().restartLoader(index, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {
            @Override
            public android.support.v4.content.Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Transaction>> categoryLoader = new AsyncTaskLoader<List<Transaction>>(getActivity()) {
                    @Override
                    public List<Transaction> loadInBackground() {
                        return Transaction.dataToDate(dateStart, dateFinish);
                    }
                };
                categoryLoader.forceLoad();
                return categoryLoader;
            }

            @Override
            public void onLoadFinished(android.support.v4.content.Loader<List<Transaction>> loader, List<Transaction> data) {

                int sum = 0;

                for (Transaction transaction : data) {
                    sum += transaction.getSum();
                }
                Log.i("Sum_index", sum + "_" + index);
                switch (index) {
                    case 0: countToday.setText(String.valueOf(sum));
                        break;
                    case 1: countMonth.setText(String.valueOf(sum));
                        break;
                    default: countYear.setText(String.valueOf(sum));
                }


            }

            @Override
            public void onLoaderReset(android.support.v4.content.Loader<List<Transaction>> loader) {

            }
        });
    }

    private void sumMonth(String monthFilter) {
        String startingCount = "01:" + monthFilter + " 00:00:00";
        String finishCount = "31:" + monthFilter + " 23:59:59";

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");

        Date dateStart = null;
        Date dateFinish = null;

        try {
            dateStart = sdf.parse(startingCount);
            dateFinish = sdf.parse(finishCount);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        loadSumma(dateStart, dateFinish, 1);
    }

    private void sumYear(String yearFilter) {
        String startingCount = "01:01:" + yearFilter + " 00:00:00";
        String finishCount = "31:12:" + yearFilter + " 23:59:59";

        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");

        Date dateStart = null;
        Date dateFinish = null;

        try {
            dateStart = sdf.parse(startingCount);
            dateFinish = sdf.parse(finishCount);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        loadSumma(dateStart, dateFinish, 2);
    }

}
