package com.andrewbondarenko.moneytracker.rest;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;

@Rest(converters = MessageConverter.class, rootUrl = "http://lmt.loftblog.tmweb.ru", interceptors = AuthenticatorInterceptor.class)
public interface RestClient {

    @Get("/auth?login={login}&password={password}")
    AuthResult login(String login, String password);

    @Get("/categories/add?title={title}")
    Result addCategory(String title);

    @Post("/transactions/add?sum={sum}&comment={comment}&category_id=4&tr_date={date}")
    Result addTransaction(int sum, String comment, String date);

    @Get("/balance")
    BalanceResult getBalance();

}
