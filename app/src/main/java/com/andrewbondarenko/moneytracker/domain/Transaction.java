package com.andrewbondarenko.moneytracker.domain;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "Transactions")
public class Transaction extends Model {

    @Column(name = "Name")
    private String name;

    @Column(name = "Sum")
    private int sum;

    @Column(name = "Date")
    private Date date;

    @Column(name = "Category", onDelete = Column.ForeignKeyAction.CASCADE)
    private Category category;

    /*
    *  Default constructor for active ORM
    * */
    public Transaction() {

    }

    public Transaction(String name, int sum, Date date, Category category) {
        super();
        this.name = name;
        this.sum = sum;
        this.date = date;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getSum() {
        return sum;
    }

    public Date getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public static List<Transaction> initData(String filter){
        From from;
        if (TextUtils.equals(filter, "")) {
            from = new Select().from(Transaction.class).orderBy("Date DESC");
        } else {
            from = new Select()
                    .from(Transaction.class)
                    .where("Name LIKE ?", "%" + filter + "%")
                    .orderBy("Date DESC");
        }
        return from.execute();
    }

    public static List<Transaction> findTransactionToCategory(Category category) {
        return new Select()
                .from(Transaction.class)
                .where("Category = ?", category.getId())
                .orderBy("Date DESC")
                .execute();
    }

    public static int count(Category category) {
        return new Select()
                .from(Transaction.class)
                .where("Category = ?", category.getId())
                .count();
    }

    public static List<Transaction> dataToDate(Date dateStart, Date dateFinish) {
        return new Select()
                .from(Transaction.class)
                .where("Date > ?", dateStart.getTime())
                .and("Date < ?", dateFinish.getTime())
                .execute();
    }

}
