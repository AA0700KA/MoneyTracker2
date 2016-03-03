package com.andrewbondarenko.moneytracker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.andrewbondarenko.moneytracker.InfoTransactionActivity_;
import com.andrewbondarenko.moneytracker.MainActivity;
import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.domain.Category;
import com.andrewbondarenko.moneytracker.domain.Transaction;
import com.andrewbondarenko.moneytracker.fragment.TransactionFragment;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TransactionAdapter extends SelectedAdapter<TransactionAdapter.CardViewHolder> {

    private List<Transaction> transactions;
    private Context context;

    private CardViewHolder.ClickListener clickListener;

    public TransactionAdapter(Context context, List<Transaction> transactions, CardViewHolder.ClickListener clickListener) {
        this.transactions = transactions;
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        return new CardViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.transactions_anim);

        holder.transactionTxt.setText(transaction.getName());
        holder.dataTxt.setText(sdf.format(transaction.getDate()));
        holder.sumTxt.setText(String.valueOf(transaction.getSum()));
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        holder.cardView.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    private void removeItem(int position) {
        Transaction transaction = transactions.get(position);
        transactions.remove(position);
        notifyItemRemoved(position);
        new Delete().from(Transaction.class).where("Id = ?", transaction.getId()).execute();
    }

    public void removeTransactions(List<Integer> positions) {

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        ActiveAndroid.beginTransaction();
        try {
            while (!positions.isEmpty()) {
                removeItem(positions.get(0));
                positions.remove(0);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        protected TextView transactionTxt;
        protected TextView dataTxt;
        protected TextView sumTxt;
        protected View selectedOverlay;
        protected CardView cardView;

        private ClickListener clickListener;

        public CardViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            transactionTxt = (TextView) itemView.findViewById(R.id.transaction);
            dataTxt = (TextView) itemView.findViewById(R.id.data);
            sumTxt = (TextView) itemView.findViewById(R.id.how_match);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getPosition());
            }
        }


        @Override
        public boolean onLongClick(View v) {

            if (clickListener != null) {
                return clickListener.onItemLongClick(getPosition());
            }
            return false;
        }


        public interface ClickListener {

            void onItemClick(int position);

            boolean onItemLongClick(int position);

        }

    }

}
