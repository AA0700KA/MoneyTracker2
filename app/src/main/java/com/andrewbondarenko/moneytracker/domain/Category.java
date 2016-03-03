package com.andrewbondarenko.moneytracker.domain;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Categories")
public class Category extends Model{

    @Column(name = "Name", unique = true)
    private String name;

    public Category() {

    }

    public List<Transaction> transactions() {
        return getMany(Transaction.class, "Category");
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Category> initData() {
        return new Select().from(Category.class).execute();
    }

}
