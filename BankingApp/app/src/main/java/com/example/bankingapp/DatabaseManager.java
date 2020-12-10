package com.example.bankingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// User Table and the attributes
import static com.example.bankingapp.DatabaseHelper.DATABASE_TABLE;
import static com.example.bankingapp.DatabaseHelper.KEY_EMAIL;
import static com.example.bankingapp.DatabaseHelper.KEY_FULLNAME;
import static com.example.bankingapp.DatabaseHelper.KEY_LOCATION;
import static com.example.bankingapp.DatabaseHelper.KEY_PASSWORD;
import static com.example.bankingapp.DatabaseHelper.KEY_PHONENUMBER;
import static com.example.bankingapp.DatabaseHelper.KEY_USERNAME;

// Bank Table and the attributes
// Also has attributes KEY_USERNAME, KEY_FULLNAME - not imported again because already imported above
import static com.example.bankingapp.DatabaseHelper.DATABASE_TABLE2;
import static com.example.bankingapp.DatabaseHelper.KEY_BANKACCNO;
import static com.example.bankingapp.DatabaseHelper.KEY_BALANCE;

// Transactions Table and the attributes
// Has attribute KEY_TRANSACTIONID but not imported because not used
// Also has attributes KEY_USERNAME - not imported again because already imported above
import static com.example.bankingapp.DatabaseHelper.DATABASE_TABLE3;
import static com.example.bankingapp.DatabaseHelper.KEY_SENDERFULLNAME;
import static com.example.bankingapp.DatabaseHelper.KEY_RECPFULLNAME;
import static com.example.bankingapp.DatabaseHelper.KEY_AMOUNT;

public class DatabaseManager
{
    Context context;
    private DatabaseHelper myDatabaseHelper;
    private SQLiteDatabase myDatabase;


    public DatabaseManager(Context context)
    {
        this.context = context;
    }

    public DatabaseManager open() throws SQLException {
        myDatabaseHelper = new DatabaseHelper(context);
        myDatabase = myDatabaseHelper.getWritableDatabase();
        return this;
    }

    // Closes the database
    public void close()
    {
        myDatabaseHelper.close();
    }

    // Insert person into database
    public void insertPerson(String username, String password, String fullName, String phoneNumber, String email, String location, String bankAccNo, double balance)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        initialValues.put(KEY_FULLNAME, fullName);
        initialValues.put(KEY_PHONENUMBER, phoneNumber);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_LOCATION, location);

        ContentValues initialValues2 = new ContentValues();
        initialValues2.put(KEY_USERNAME, username);
        initialValues2.put(KEY_FULLNAME, fullName);
        initialValues2.put(KEY_BANKACCNO, bankAccNo);
        initialValues2.put(KEY_BALANCE, balance);

        myDatabase.insert(DATABASE_TABLE, null, initialValues);
        myDatabase.insert(DATABASE_TABLE2, null, initialValues2);
    }

    // Record transactions
    public void recordTransaction(String username, String recpUsername, String senderFullName, String recpFullName, String amount)
    {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_SENDERFULLNAME, senderFullName);
        initialValues.put(KEY_RECPFULLNAME, recpFullName);
        initialValues.put(KEY_AMOUNT, "-" + amount);

        ContentValues initialValues2 = new ContentValues();
        initialValues2.put(KEY_USERNAME, recpUsername);
        initialValues2.put(KEY_SENDERFULLNAME, senderFullName);
        initialValues2.put(KEY_RECPFULLNAME, recpFullName);
        initialValues2.put(KEY_AMOUNT, "+" + amount);

        myDatabase.insert(DATABASE_TABLE3, null, initialValues);
        myDatabase.insert(DATABASE_TABLE3, null, initialValues2);
        close();
    }

    // Retrieves a person
    public boolean getPerson(String username, String password) throws SQLException
    {
        String[] columns = {"username"};
        String selection = "username=? and password = ?";
        String[] selectionArgs = {username, password};
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null);
        int cursorCount = mCursor.getCount();

        mCursor.close();
        close();

        if (cursorCount > 0) {
            return true;
        }else {
            return false;
        }
    }

    // Retrieve a person by bank account number
    public boolean getPersonByBankAccNo(String bankAccNo) throws SQLException
    {
        String[] columns = {"username"};
        String selection = "bankAccNo=?";
        String[] selectionArgs = {bankAccNo};
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE2, columns, selection, selectionArgs, null, null, null);
        int cursorCount = mCursor.getCount();

        mCursor.close();
        close();

        if (cursorCount > 0) {
            return true;
        }else {
            return false;
        }
    }

    // Retrieve username by bank account number
    public String getUsernameByBankAccNo(String bankAccNo) throws SQLException
    {
        String[] columns = {"username"};
        String selection = "bankAccNo=?";
        String[] selectionArgs = {bankAccNo};
        String username = null;
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE2, columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            username = mCursor.getString(mCursor.getColumnIndex("username"));
        }
        mCursor.close();
        close();

        return username;
    }

    // Retrieve full name by bank account number
    public String getFullNameByBankAccNo(String bankAccNo) throws SQLException
    {
        String[] columns = {"fullName"};
        String selection = "bankAccNo=?";
        String[] selectionArgs = {bankAccNo};
        String fullName = null;
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE2, columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            fullName = mCursor.getString(mCursor.getColumnIndex("fullName"));
        }
        mCursor.close();
        close();

        return fullName;
    }

    // Updates a person's phone number and email
    public void updatePerson(String phoneNumber,
                                String email, String username)
    {
        String whereClause = "username=?";
        String[] whereArgs = {username};
        open();

        ContentValues args = new ContentValues();
        args.put(KEY_PHONENUMBER, phoneNumber);
        args.put(KEY_EMAIL, email);

        myDatabase.update(DATABASE_TABLE, args, whereClause, whereArgs);
    }

    // Transfer funds between bank accounts
    public void transferFunds(String username,
                             String bankAccNo, double amount)
    {
        String whereClause = "username=?";
        String[] whereArgs = {username};

        // Update user balance
        double newUserBalance = getBalance(username) - amount;
        ContentValues args = new ContentValues();
        args.put(KEY_BALANCE, newUserBalance);

        open();
        myDatabase.update(DATABASE_TABLE2, args, whereClause, whereArgs);

        // Update 3rd party balance
        String recipientUsername = getUsernameByBankAccNo(bankAccNo);
        String[] whereArgs2 = {recipientUsername};

        double newRecipientBalance = getBalance(recipientUsername) + amount;
        ContentValues args2 = new ContentValues();
        args2.put(KEY_BALANCE, newRecipientBalance);

        open();
        myDatabase.update(DATABASE_TABLE2, args2, whereClause, whereArgs2);
    }

    // Retrieves balance of the user bank account
    public double getBalance(String username) throws SQLException
    {
        String[] columns = {"balance"};
        String selection = "username=?";
        String[] selectionArgs = {username};
        double balance = 0;
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE2, columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            balance = mCursor.getDouble(mCursor.getColumnIndex("balance"));
        }
        mCursor.close();
        close();

        return balance;
    }

    // Retrieve bank account number by username
    public String getBankAccNo(String username) throws SQLException
    {
        String[] columns = {"bankAccNo"};
        String selection = "username=?";
        String[] selectionArgs = {username};
        String bankAccNo = null;
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE2, columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            bankAccNo = mCursor.getString(mCursor.getColumnIndex("bankAccNo"));
        }
        mCursor.close();
        close();

        return bankAccNo;
    }

    // Retrieve phone number of user
    public String getPhoneNum(String username) throws SQLException
    {
        String[] columns = {"phoneNumber"};
        String selection = "username=?";
        String[] selectionArgs = {username};
        String phoneNum = null;
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            phoneNum = mCursor.getString(mCursor.getColumnIndex("phoneNumber"));
        }
        mCursor.close();
        close();

        return phoneNum;
    }

    // Retrieve email of user
    public String getEmail(String username) throws SQLException
    {
        String[] columns = {"email"};
        String selection = "username=?";
        String[] selectionArgs = {username};
        String email = null;
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null);
        if (mCursor.moveToFirst()) {
            email = mCursor.getString(mCursor.getColumnIndex("email"));
        }
        mCursor.close();
        close();

        return email;
    }

    // Checks the user balance is sufficient for transfer amount
    public boolean checkAmount(String username, double amount) throws SQLException
    {
        double balance = getBalance(username);

        if (balance > amount && amount != 0) {
            return true;
        }else {
            return false;
        }
    }

    // Retrieves all transactions for a user
    public Cursor getAllTransactionForUsername(String username) throws SQLException
    {
        String[] columns = {"_id", "senderFullName", "recpFullName", "amount"};
        String selection = "username=?";
        String[] selectionArgs = {username};
        open();

        Cursor mCursor = myDatabase.query(DATABASE_TABLE3, columns, selection, selectionArgs, null, null, null);

        if (mCursor.moveToFirst()) {
            mCursor.moveToFirst();
        }
        close();

        return mCursor;
    }

    // Display symbol based on location
    public String currencySymbol(String location)
    {
        String symbol = "$";

        if (location.equals("Ireland"))
        {
            symbol = "€";
        }
        else if (location.equals("United Kingdom"))
        {
            symbol = "£";
        }

        return symbol;
    }

    // Converts currency based on location
    public double convertCurrency(double balance, String location)
    {
        double convert = 1;

        if (location.equals("Ireland"))
        {
            convert = 0.82;
        }
        else if (location.equals("United Kingdom"))
        {
            convert = 0.74;
        }

        return convert;
    }
}
