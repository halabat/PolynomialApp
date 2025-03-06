package com.example.polynomialapp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PolynomialDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Polynomials.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "polynomials";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DEGREE = "degree";
    public static final String COLUMN_COEFFICIENTS = "coefficients";

    public PolynomialDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DEGREE + " INTEGER, " +
                COLUMN_COEFFICIENTS + " TEXT)");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}