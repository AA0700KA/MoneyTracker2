package com.andrewbondarenko.moneytracker.rest;

import android.text.TextUtils;

public class Result {

    protected String status;

    public String getStatus() {
        return status;
    }

    public boolean isSuccessfull() {
        return TextUtils.equals(status, "success");
    }

}
