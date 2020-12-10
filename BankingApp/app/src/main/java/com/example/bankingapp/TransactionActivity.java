package com.example.bankingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class TransactionActivity extends AppCompatActivity {

    // Attributes for transaction screen
    private String uName;
    private DatabaseManager dbManager;
    private ListView listView;
    private SimpleCursorAdapter adapter;

    final String[] from = new String[] { DatabaseHelper.KEY_TRANSACTIONID,
            DatabaseHelper.KEY_SENDERFULLNAME, DatabaseHelper.KEY_RECPFULLNAME, DatabaseHelper.KEY_AMOUNT };

    final int[] to = new int[] { R.id.idRow, R.id.senderRow, R.id.recipientRow, R.id.amountRow };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_listview);

        Intent intent = getIntent();
        uName = intent.getStringExtra("uName");

        dbManager = new DatabaseManager(this);

        Cursor cursor = dbManager.getAllTransactionForUsername(uName);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));

        adapter = new SimpleCursorAdapter(TransactionActivity.this, R.layout.activity_transaction_row, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);
    }
}
