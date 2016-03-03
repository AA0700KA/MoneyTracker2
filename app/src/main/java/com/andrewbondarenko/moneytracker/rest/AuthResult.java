package com.andrewbondarenko.moneytracker.rest;


public class AuthResult extends Result {

    private int id;
    public String authToken;

    public int getId() {
        return id;
    }

    public String getAuthToken() {
        return authToken;
    }

}
