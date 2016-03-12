package com.andrewbondarenko.moneytracker.fragment;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.andrewbondarenko.moneytracker.AddTransactionActivity_;
import com.andrewbondarenko.moneytracker.InfoTransactionActivity_;
import com.andrewbondarenko.moneytracker.MainActivity;
import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.adapter.TransactionAdapter;
import com.andrewbondarenko.moneytracker.domain.Category;
import com.andrewbondarenko.moneytracker.domain.Transaction;
import com.andrewbondarenko.moneytracker.utils.RequestCode;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TransactionFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private ActionMode actionMode;
    private ActionModeCallBack actionModeCallBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView sumText;

    private Category searchCategory;

    public static final String TAG = TransactionFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionModeCallBack = new ActionModeCallBack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.list_fragment, null);
        setHasOptionsMenu(true);
        sumText = (TextView) inflate.findViewById(R.id.sum_text);
        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) inflate.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) inflate.findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTransactionActivity_.class);
                getActivity().startActivityForResult(intent, RequestCode.ADD_TRANSACTION);
                getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData("");
            }
        });

        return inflate;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final SearchView searchView = (SearchView) menu.getItem(0).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final String filter = newText;

                final Handler handler = new Handler();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadData(filter);
                            }
                        });
                    }
                }, 500);

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.transactions_fragment_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadData(final String filter) {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {

            @Override
            public Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Transaction>> transactionsLoader = new AsyncTaskLoader<List<Transaction>>(getActivity()) {
                    @Override
                    public List<Transaction> loadInBackground() {
                        List<Transaction> transactions;

                        if (searchCategory == null) {
                            transactions = Transaction.initData(filter);
                        } else {
                            transactions = Transaction.findTransactionToCategory(searchCategory);
                        }

                        return transactions;
                    }
                };
                transactionsLoader.forceLoad();
                return transactionsLoader;
            }

            @Override
            public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
                swipeRefreshLayout.setRefreshing(false);
                adapter = new TransactionAdapter(getActivity(),data, new TransactionAdapter.CardViewHolder.ClickListener() {

                     @Override
                     public void onItemClick(int position) {
                            if (actionMode != null) {
                                  toggleSelection(position);
                            } else {
                                  Transaction transaction = adapter.getTransactions().get(position);
                                  Category category = transaction.getCategory();
                                  Intent intent = new Intent(getActivity(), InfoTransactionActivity_.class);
                                  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                                  intent.putExtra("Transaction", transaction.getName());
                                  intent.putExtra("Sum", String.valueOf(transaction.getSum()));

                                   if (category != null) {
                                       intent.putExtra("Category", category.getName());
                                   }

                                   intent.putExtra("Date", sdf.format(transaction.getDate()));
                                   getActivity().startActivity(intent);
                                   getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                             }
                     }

                     @Override
                     public boolean onItemLongClick(int position) {
                            if (actionMode == null) {
                                AppCompatActivity activity = (AppCompatActivity) getActivity();
                                actionMode = activity.startSupportActionMode(actionModeCallBack);
                            }
                            toggleSelection(position);
                            return true;
                     }
                 });
                 recyclerView.setAdapter(adapter);
                 transactionSum(data);
            }

            @Override
            public void onLoaderReset(Loader<List<Transaction>> loader) {

            }
        });

    }

    private void transactionSum(List<Transaction> data) {
        int sum = 0;

        for (Transaction transaction : data) {
            sum += transaction.getSum();
        }

        sumText.setText(String.valueOf(sum));
    }

    public static TransactionFragment getInstance(Category category) {
        TransactionFragment fragment = new TransactionFragment();
        fragment.setSearchCategory(category);
        return fragment;
    }

    public void setSearchCategory(Category searchCategory) {
        this.searchCategory = searchCategory;
    }

    private void toggleSelection(int position) {
        adapter.toggleSelected(position);
        int count = adapter.getSelectedItemsCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallBack implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toggle_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<Integer> positions = adapter.getSelectedItems();
            int sum = Integer.parseInt(sumText.getText().toString());
            List<Transaction> transactions = adapter.getTransactions();
            for (Integer position : positions) {
                sum -= transactions.get(position).getSum();
            }
            adapter.removeTransactions(positions);
            sumText.setText(String.valueOf(sum));
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clear();
            actionMode = null;
        }

    }

}

