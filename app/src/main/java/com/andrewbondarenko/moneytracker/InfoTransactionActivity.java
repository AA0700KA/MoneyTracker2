package com.andrewbondarenko.moneytracker;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.info_transaction_layout)
public class InfoTransactionActivity extends AppCompatActivity{

    @ViewById
    TextView sumInfo;

    @ViewById
    TextView transactionInfo;

    @ViewById
    TextView categoryInfo;

    @ViewById
    TextView dateInfo;

    @ViewById
    Toolbar toolbar;

    @AfterViews
    void afterView() {
        initToolbar();
        getData();
    }

    @Click
    void ok() {
        finish();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.info_transactions);
    }

    private void getData() {
        String sum = getIntent().getStringExtra("Sum");
        String transaction = getIntent().getStringExtra("Transaction");
        String category = getIntent().getStringExtra("Category");
        String date = getIntent().getStringExtra("Date");
        sumInfo.setText(sum);
        transactionInfo.setText(transaction);
        categoryInfo.setText(category);
        dateInfo.setText(date);
    }

    @OptionsItem(android.R.id.home)
    void back() {
        onBackPressed();
    }

}
