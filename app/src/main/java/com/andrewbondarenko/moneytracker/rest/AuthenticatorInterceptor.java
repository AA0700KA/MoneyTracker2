package com.andrewbondarenko.moneytracker.rest;

import android.util.Log;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class AuthenticatorInterceptor implements ClientHttpRequestInterceptor {

    public static String authToken;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Log.e(AuthenticatorInterceptor.class.getSimpleName(),  "AAA intercept " + request.getURI() );
        if (authToken != null)
            request.getHeaders().add("authToken", authToken);
        return execution.execute(request, body);
    }

}
