package com.andrewbondarenko.moneytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.activeandroid.query.Select;
import com.andrewbondarenko.moneytracker.adapter.CategoryAdapter;
import com.andrewbondarenko.moneytracker.domain.Category;
import com.andrewbondarenko.moneytracker.domain.Transaction;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.add_transaction_layout)
public class AddTransactionActivity extends AppCompatActivity {

    @ViewById(R.id.edit_sum)
    EditText sumEdit;

    @ViewById(R.id.edit_cost)
    EditText transactionEdit;

    @ViewById(R.id.edit_date_day)
    EditText dateEditDay;

    @ViewById(R.id.edit_date_month)
    EditText dateEditMonth;

    @ViewById(R.id.edit_date_year)
    EditText dateEditYear;

    @ViewById(R.id.spinner_category)
    Spinner spinnerCategories;

    @ViewById
    Toolbar toolbar;

    private Category chosenCategory;

    @AfterViews
    public void afterView() {
        initToolbar();
        initData();
        initSpinner();
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.add_transaction_str);
    }


    private void initData() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd:MM:yyyy");
        String[] stringData = sdf.format(new Date()).split(":");
        dateEditDay.setText(stringData[0]);
        dateEditMonth.setText(stringData[1]);
        dateEditYear.setText(stringData[2]);
    }


    private void initSpinner() {
        final List<Category> categories = new Select().from(Category.class).execute();
        categories.add(new Category("No category"));
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);
        spinnerCategories.setSelection(0);
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Click
    public void addTransaction() {

        String transaction = transactionEdit.getText().toString();
        String sum = sumEdit.getText().toString();
        String day = dateEditDay.getText().toString();
        String month = dateEditMonth.getText().toString();
        String year = dateEditYear.getText().toString();

        if (transaction.length() > 0 && sum.length() > 0 && day.length() == 2
                && month.length() == 2 && year.length() == 4 && convert(day) != 0
                && convert(day) <= 31 && convert(month) != 0 && convert(month) <= 12) {

            putData(sum, transaction, day, month, year, chosenCategory);

        } else if (transaction.length() == 0 && sum.length() == 0) {
            Toast.makeText(this, "Write transaction and summ", Toast.LENGTH_SHORT).show();
        } else if (transaction.length() == 0 && sum.length() > 0) {
            Toast.makeText(this, "Write transaction", Toast.LENGTH_SHORT).show();
        } else if (transaction.length() > 0 && sum.length() == 0) {
            Toast.makeText(this, "Write summ", Toast.LENGTH_SHORT).show();
        } else if (convert(day) == 0 || convert(day) > 31 || convert(month) == 0 || convert(month) > 12) {
            Toast.makeText(this, "Day maybe at 01 to 31, month at 01 to 12", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Write data for pattern '01-01-1970'", Toast.LENGTH_SHORT).show();
        }
    }

    private void putData(String sum, String transaction, String day, String month, String year, Category category) {

        String dateString = day + ":" + month + ":" + year;
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");
        Date date = new Date();

        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int summa = Integer.parseInt(sum);
        new Transaction(transaction, summa, date, category).save();
        finish();
    }

    private int convert(String str) {
        if (str.length() == 2 && str.charAt(0) == '0') {
            return Integer.parseInt(str.substring(1));
        }
        return Integer.parseInt(str);
    }

    @OptionsItem(android.R.id.home)
    void back() {
        onBackPressed();
    }

}
