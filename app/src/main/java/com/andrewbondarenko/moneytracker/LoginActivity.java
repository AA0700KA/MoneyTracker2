package com.andrewbondarenko.moneytracker;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.andrewbondarenko.moneytracker.auth.SessionManager;
import com.andrewbondarenko.moneytracker.rest.AuthResult;
import com.andrewbondarenko.moneytracker.rest.RestClient;
import com.andrewbondarenko.moneytracker.rest.Result;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

@EActivity(R.layout.login_activity)
public class LoginActivity extends AccountAuthenticatorActivity {

    @ViewById
    EditText login;

    @ViewById
    EditText password;

    @RestService
    RestClient api;

    @Bean
    SessionManager sessionManager;

    @Click
    void loginButton() {
        authentification();
    }

    @Background
    void authentification() {
        final AuthResult login = api.login(this.login.getText().toString(), password.getText().toString());
        handleResultLogIn(login);
    }

    @UiThread
    void handleResultLogIn(AuthResult result) {
        if (result.isSuccessfull()) {
            sessionManager.createAccount(login.getText().toString(), result.authToken);
            setAccountAuthenticatorResult(new Bundle());
            finish();
        } else {
            Toast.makeText(this, "Incorrect login or password", Toast.LENGTH_SHORT).show();
        }
    }

}
