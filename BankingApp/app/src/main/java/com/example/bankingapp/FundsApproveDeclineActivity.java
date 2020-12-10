package com.example.bankingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FundsApproveDeclineActivity extends AppCompatActivity implements View.OnClickListener {

    // Attributes for funds approve decline screen
    private String uName;
    private String fullName;
    private String bankAccNo;
    private String amount;
    private TextView recipientName;
    private TextView recipientBankAccNo;
    private TextView amountToBeSent;
    private Button decline;
    private Button approve;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funds_approve_decline);

        Intent intent = getIntent();
        uName = intent.getStringExtra("uName");
        bankAccNo = intent.getStringExtra("bankAccNo");
        amount = intent.getStringExtra("amount");

        dbManager = new DatabaseManager(this);
        fullName = dbManager.getFullNameByBankAccNo(bankAccNo);

        recipientName = (TextView) findViewById(R.id.recipientName);
        recipientName.setText(fullName);

        recipientBankAccNo = (TextView) findViewById(R.id.recipientBankAccNo);
        recipientBankAccNo.setText(bankAccNo);

        amountToBeSent = (TextView) findViewById(R.id.amountToBeSent);
        amountToBeSent.setText("$" + amount);

        decline = (Button)findViewById(R.id.decline);
        approve = (Button)findViewById(R.id.approve);
        decline.setOnClickListener(this);
        approve.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.approve:
                // Convert amount string to double
                double transactAmount = Double.parseDouble(amount);
                // Execute transaction between bank accounts
                dbManager.transferFunds(uName, bankAccNo, transactAmount);

                String senderBankAcc = dbManager.getBankAccNo(uName);
                String senderFullName = dbManager.getFullNameByBankAccNo(senderBankAcc);

                String recpUsername = dbManager.getUsernameByBankAccNo(bankAccNo);
                String recpFullName = dbManager.getFullNameByBankAccNo(bankAccNo);

                // Record the transaction
                dbManager.recordTransaction(uName, recpUsername, senderFullName, recpFullName, amount);

                // Retrieve user new balance and show to user
                double userNewBal = dbManager.getBalance(uName);
                Toast.makeText(getApplication(), "Transaction Approved. New Balance: " + userNewBal, Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.decline:
                Toast.makeText(getApplication(), "Transaction Declined", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }
}