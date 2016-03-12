package com.andrewbondarenko.moneytracker;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.andrewbondarenko.moneytracker.auth.SessionManager;
import com.andrewbondarenko.moneytracker.domain.Transaction;
import com.andrewbondarenko.moneytracker.fragment.CategoryFragment;
import com.andrewbondarenko.moneytracker.fragment.StatisticFragment;
import com.andrewbondarenko.moneytracker.fragment.TransactionFragment;
import com.andrewbondarenko.moneytracker.rest.AuthResult;
import com.andrewbondarenko.moneytracker.rest.AuthenticatorInterceptor;
import com.andrewbondarenko.moneytracker.rest.BalanceResult;
import com.andrewbondarenko.moneytracker.rest.RestClient;
import com.andrewbondarenko.moneytracker.rest.Result;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.main_layout)
public class MainActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;
    private Drawer drawer;

    @RestService
    RestClient api;

    @Bean
    SessionManager sessionManager;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);

        drawer = initDrawer();

        setTitle(R.string.transactions);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new TransactionFragment(), TransactionFragment.TAG).commit();
    }

    private Drawer initDrawer() {

        AccountHeader accountHeader = getAccountHeader();

        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.transactions),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.categories),
                        new SecondaryDrawerItem().withName(R.string.statistic)
                )
                .withOnDrawerItemClickListener(new DrawerClickListener())
                .build();
    }

    private AccountHeader getAccountHeader() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.androidapp8)
                .addProfiles(
                        new ProfileDrawerItem().withName("Andrew Bondarenko").withEmail("saleen2000@yandex.ru")
                )
                .build();
    }

    @Receiver(actions = {SessionManager.OPEN_SESSION_BROADCAST}, registerAt = Receiver.RegisterAt.OnResumeOnPause,
            local = true)
    void onSessionOpen() {
        //  testNetwork();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.login(this);
    }

    @Background
    public void testNetwork() {
        final AuthResult login = api.login("andrew1101", "pedrosanchez77");
        AuthenticatorInterceptor.authToken = login.authToken;
        Log.e("AuthResult", login.authToken + " " + login.getId() + " " + login.getStatus());
//        final BalanceResult balance = api.getBalance();
//       final Result category = api.addCategory("category1");
//        Log.i("LOG", "Category status:" + category.getStatus());
        // final Result transaction = api.addTransaction(10, "Plane", "2016-02-11");
//        Log.i("LOG", "Transaction status:" + transaction.getStatus());
    }

    private class DrawerClickListener implements Drawer.OnDrawerItemClickListener {

        @Override
        public boolean onItemClick( View view, int position, IDrawerItem drawerItem) {

            // FragmentManager manager = getSupportFragmentManager();
            String name = null;

            drawer.closeDrawer();

            if (drawerItem instanceof SecondaryDrawerItem) {
                name = getResources().getString(((SecondaryDrawerItem) drawerItem).getName().getTextRes());
            } else if (drawerItem instanceof PrimaryDrawerItem) {
                name = getResources().getString(((PrimaryDrawerItem) drawerItem).getName().getTextRes());
            }

            setTitle(name);

            switch (name) {
                case "Transactions":
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new TransactionFragment(), TransactionFragment.TAG).commit();
                    break;
                case "Categories":
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new CategoryFragment(), CategoryFragment.TAG).commit();
                    break;
                case "Statistics":
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new StatisticFragment(), StatisticFragment.TAG).commit();
                    break;
            }

            return true;
        }
    }

}
