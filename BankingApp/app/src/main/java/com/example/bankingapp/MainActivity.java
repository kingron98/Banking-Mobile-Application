package com.example.bankingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Attributes for main acitivity screen
    private TextView username;
    private TextView bankAccNo;
    private Button checkBalance;
    private Button updateProfile;
    private Button transferFunds;
    private Button transanctions;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent uName as username that is logging in
        // This username will be passed around every activity as uName
        Intent intent = getIntent();
        String uName = intent.getStringExtra("uName");

        dbManager = new DatabaseManager(this);
        String dbManagerBankAccNo = dbManager.getBankAccNo(uName);

        // Display logged in username
        username = (TextView) findViewById(R.id.username);
        username.setText(uName);

        // Display user bank account number
        bankAccNo = (TextView) findViewById(R.id.bankAccNo);
        bankAccNo.setText(dbManagerBankAccNo);

        checkBalance = (Button)findViewById(R.id.checkBalance);
        updateProfile = (Button)findViewById(R.id.updateProfile);
        transferFunds = (Button)findViewById(R.id.transferFunds);
        transanctions = (Button)findViewById(R.id.transactions);
        checkBalance.setOnClickListener(this);
        updateProfile.setOnClickListener(this);
        transferFunds.setOnClickListener(this);
        transanctions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        final String uName = username.getText().toString();

        switch (v.getId()){
            case R.id.checkBalance:

                Intent checkBalance = new Intent(getApplicationContext(), CheckBalanceActivity.class);
                checkBalance.putExtra("uName", uName);
                checkBalance.putExtra("loc", getIntent().getStringExtra("loc"));
                startActivity(checkBalance);
                break;

            case R.id.updateProfile:

                Intent updateProfile = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                updateProfile.putExtra("uName", uName);
                startActivity(updateProfile);
                break;

            case R.id.transferFunds:

                Intent transferFunds = new Intent(getApplicationContext(), TransferFundsActivity.class);
                transferFunds.putExtra("uName", uName);
                startActivity(transferFunds);
                break;

            case R.id.transactions:

                Intent transactions = new Intent(getApplicationContext(), TransactionActivity.class);
                transactions.putExtra("uName", uName);
                startActivity(transactions);
                break;
        }
    }
}