package com.example.nhom2_app_cham_cong_qr;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recordDB";
    private static final String TABLE_NAME = "attendance";
    private static final String ID = "id";
    private static final String NAME_COL = "name";
    private static final String DATE_COL = "date";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + ID + "INTEGER PRIMARY KEY, "
                + NAME_COL + " TEXT ,"
                + DATE_COL + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tmp = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(tmp);
    }

    public boolean addText(String name, String date) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL, name);
        contentValues.put(DATE_COL, date);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    @SuppressLint("Range")
    public ArrayList<String> getName() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(NAME_COL)));
            cursor.moveToNext();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<String> getDate() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex(DATE_COL)));
            cursor.moveToNext();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllText() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(
                    cursor.getString(cursor.getColumnIndex(NAME_COL)) + " " + cursor.getString(cursor.getColumnIndex(DATE_COL))
            );
            cursor.moveToNext();
        }
        return arrayList;
    }
}