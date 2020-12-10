package com.example.bankingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransferFundsActivity extends AppCompatActivity implements View.OnClickListener {

    // Attributes for transfer funds screen
    private String uName;
    private EditText bankAccNo;
    private EditText amount;
    private Button cont;
    private TextView balanceUSD;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_funds);

        Intent intent = getIntent();
        uName = intent.getStringExtra("uName");

        bankAccNo = (EditText) findViewById(R.id.bankAccNoInput);
        amount = (EditText) findViewById(R.id.amountInput);

        cont = (Button) findViewById(R.id.cont);

        dbManager = new DatabaseManager(this);
        double dbManagerBalance = dbManager.getBalance(uName);
        balanceUSD = (TextView) findViewById(R.id.balanceUSD);
        balanceUSD.setText("$" + dbManagerBalance);

        cont.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cont:
                // Storing bank account number to transfer as string
                final String accNo = bankAccNo.getText().toString();

                // Storing amount to transfer as string
                String amountStr = amount.getText().toString();

                // Checks if amount string given is empty
                // If empty set string to 0
                if (amountStr.isEmpty()) {
                    amountStr = "0";
                }

                // Converts the amount string to a double
                double amountVal = Double.parseDouble(amountStr);

                // Checks if bank account number to transfer is valid
                // Checks if user balance is sufficient for the transfer amount requested
                boolean validAccount = dbManager.getPersonByBankAccNo(accNo);
                boolean validAmount = dbManager.checkAmount(uName, amountVal);

                // Storing the username of the bank account number to transfer
                // To check if bank account number entered is not user own bank account number
                String checkNotSelf = dbManager.getBankAccNo(uName);

                // If valid account and amount and bank account number entered is not user bank account number
                if (validAccount && validAmount && !accNo.equals(checkNotSelf)) {
                    Intent fundsAD = new Intent(getApplicationContext(), FundsApproveDeclineActivity.class);
                    fundsAD.putExtra("uName", uName);
                    fundsAD.putExtra("bankAccNo", accNo);
                    fundsAD.putExtra("amount", amountStr);
                    startActivity(fundsAD);
                    finish(); // Finish activity so user cannot go back to this screen, instead to main page
                } else {
                    // Display toast message depending on situation
                    // Situation bank account number is user bank account number
                    if (accNo.equals(checkNotSelf)) {
                        bankAccNo.setText(null);
                        amount.setText(null);
                        Toast.makeText(getApplication(), "Cannot transfer to own account. " + accNo + " - " + amountVal, Toast.LENGTH_LONG).show();
                    }else{
                        // Situation bank account number or amount is invalid
                        bankAccNo.setText(null);
                        amount.setText(null);
                        Toast.makeText(getApplication(), "Invalid Account or Amount: " + accNo + " - " + amountVal, Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}