package com.andrewbondarenko.moneytracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.andrewbondarenko.moneytracker.R;


public class StatisticFragment extends Fragment {

    public static final String TAG = StatisticFragment.class.getSimpleName();
    public static final String COSTS_MODE = "Costs_Mode";
    public static final String COUNT_MODE = "Count_Mode";

    private ViewPager viewPager;
    private static final int STRING_COUNT = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_fragment, null);
        viewPager = (ViewPager) view.findViewById(R.id.statistic_view_pager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        PagerAdapter pagerAdapter = new MyPagerAdapter(activity.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CountTransactionStatisticFragment.getInstance(COUNT_MODE);
                case 1:
                    return CountTransactionStatisticFragment.getInstance(COSTS_MODE);
                default: return new StatisticCountFragment();
            }

        }

        @Override
        public int getCount() {
            return STRING_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Transaction count";
                case 1: return "Price statistic";
                case 2: return "Statistic table";
                default: return "Page " + position;
            }

        }

    }

}
