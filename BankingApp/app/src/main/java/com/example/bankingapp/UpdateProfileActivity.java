package com.example.bankingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Attributes for update profile screen
    private String uName;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private Button confirm;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Intent intent = getIntent();
        uName = intent.getStringExtra("uName");

        phoneNumber = (EditText) findViewById(R.id.phoneInput);
        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordChange);

        confirm = (Button) findViewById(R.id.confirm);

        dbManager = new DatabaseManager(this);

        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:

                final String pNum = phoneNumber.getText().toString();
                final String em = email.getText().toString();
                final String pWord = password.getText().toString();

                // Check valid password
                boolean validPassword = dbManager.getPerson(uName, pWord);

                // If valid password and fields not empty then make changes
                if (validPassword && !pNum.isEmpty() && !em.isEmpty()) {
                    dbManager.updatePerson(pNum, em, uName);
                    Toast.makeText(getApplication(), "Changes made successfully.", Toast.LENGTH_LONG).show();
                    phoneNumber.setText(null);
                    email.setText(null);
                    password.setText(null);
                } else {
                    // Display error message if any fields empty
                    if (pNum.isEmpty() || em.isEmpty()) {
                        Toast.makeText(getApplication(), "Fields cannot be empty.", Toast.LENGTH_LONG).show();
                    } else {
                        // Display error message if password not valid
                        password.setText(null);
                        Toast.makeText(getApplication(), "Password incorrect, no changes made.", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }
}