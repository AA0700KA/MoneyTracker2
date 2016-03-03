package com.andrewbondarenko.moneytracker.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.io.IOException;

@EBean(scope = EBean.Scope.Singleton)
public class SessionManager {

    public static final String AUTH_ACCOUNT_TYPE = "com.loftschool.moneytracker";
    private static final String AUTH_ACCOUNT_TYPE_FULL_ACCESS = AUTH_ACCOUNT_TYPE + ".authtoken";
    public static final String OPEN_SESSION_BROADCAST = "open-session";

    @SystemService
    AccountManager accountManager;

    @RootContext
    Context context;

    public void createAccount(String login, String authToken) {
        Account account = new Account(login, AUTH_ACCOUNT_TYPE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            accountManager.setAuthToken(account, AUTH_ACCOUNT_TYPE_FULL_ACCESS, authToken);
        }
    }

    @Background
    public void login(Activity activity) {

        if (restoreAccount()) {
            return;
        }

        AccountManagerFuture<Bundle> future = accountManager.addAccount(AUTH_ACCOUNT_TYPE,
                AUTH_ACCOUNT_TYPE_FULL_ACCESS, null, null, activity, null, null);
        try {
            future.getResult();
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }

    }

    private boolean restoreAccount() {
        Account[] accounts = accountManager.getAccountsByType(AUTH_ACCOUNT_TYPE);

        if (accounts.length == 0) {
            return false;
        }

        AccountManagerFuture<Bundle> future = accountManager.getAuthToken(accounts[0],AUTH_ACCOUNT_TYPE_FULL_ACCESS,
                null, false, null, null);

        try {
            future.getResult();
            Log.e("AAAA", "login Auth: " + future.getResult().get(AccountManager.KEY_AUTHTOKEN));
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(OPEN_SESSION_BROADCAST));
            return true;
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }

        return false;
    }

}
