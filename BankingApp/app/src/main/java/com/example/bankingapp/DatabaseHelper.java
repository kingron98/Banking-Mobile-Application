package com.example.bankingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // USER TABLE ATTRIBUTES
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LOCATION = "location";

    // BANK TABLE ATTRIBUTES
    public static final String KEY_BANKACCNO = "bankAccNo";
    public static final String KEY_BALANCE = "balance";

    // TRANSACTIONS TABLE ATTRIBUTES
    public static final String KEY_TRANSACTIONID = "_id";
    public static final String KEY_SENDERFULLNAME = "senderFullName";
    public static final String KEY_RECPFULLNAME = "recpFullName";
    public static final String KEY_AMOUNT = "amount";

    // DATABASE NAME AND TABLES
    public static final String DATABASE_NAME = "Banking";
    public static final String DATABASE_TABLE = "User";
    public static final String DATABASE_TABLE2 = "Bank";
    public static final String DATABASE_TABLE3 = "Transactions";
    public static final int DATABASE_VERSION = 1;

    // These are the strings containing the SQL database create statement
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE  +
                    " (username text primary key, " +
                    "password text not null, " +
                    "fullName text not null, "  +
                    "phoneNumber text not null, "  +
                    "email text not null, "  +
                    "location text not null);";

    private static final String DATABASE_CREATE2 =
            "create table " + DATABASE_TABLE2  +
                    " (username text primary key, " +
                    "fullName text not null, "  +
                    "bankAccNo text not null, "  +
                    "balance double not null, " +
                    "foreign key (username) references " + DATABASE_TABLE + " (username));";

    private static final String DATABASE_CREATE3 =
            "create table " + DATABASE_TABLE3  +
                    " (_id integer primary key autoincrement, " +
                    "username text not null, "  +
                    "senderFullName text not null, "  +
                    "recpFullName text not null, "  +
                    "amount text not null);";

    // Constructor for dB helper class
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        // The “Database_create” strings below needs to contain the SQL statement needed to create the dB
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE2);
        db.execSQL(DATABASE_CREATE3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion)
    {
        // If want to change the structure of database, e.g.
    }
}

