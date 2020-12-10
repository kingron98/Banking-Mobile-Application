package com.example.bankingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CheckBalanceActivity extends AppCompatActivity {

    // Attributes for check balance screen
    private String uName;
    private TextView bankAccNo;
    private TextView balance;
    private TextView phoneNum;
    private TextView email;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);

        Intent intent = getIntent();
        uName = intent.getStringExtra("uName");
        String loc = intent.getStringExtra("loc");

        dbManager = new DatabaseManager(this);
        String symbol = dbManager.currencySymbol(loc);

        // Retrieving values and displaying to user
        String dbManagerBankAccNo = dbManager.getBankAccNo(uName);
        double dbManagerBalance = dbManager.getBalance(uName);
        String dbManagerPhoneNum = dbManager.getPhoneNum(uName);
        String dbManagerEmail = dbManager.getEmail(uName);

        // Convert currency based on GPS location
        double dbManagerCurrencyConverter = dbManager.convertCurrency(dbManagerBalance, loc);
        double dbManagerFinalBalance = dbManagerBalance * dbManagerCurrencyConverter;

        bankAccNo = (TextView) findViewById(R.id.bankAccNo);
        bankAccNo.setText(dbManagerBankAccNo);

        balance = (TextView) findViewById(R.id.balanceRecord);
        balance.setText(symbol + dbManagerFinalBalance);

        phoneNum = (TextView) findViewById(R.id.phoneRecord);
        phoneNum.setText(dbManagerPhoneNum);

        email = (TextView) findViewById(R.id.emailRecord);
        email.setText(dbManagerEmail);
    }
}