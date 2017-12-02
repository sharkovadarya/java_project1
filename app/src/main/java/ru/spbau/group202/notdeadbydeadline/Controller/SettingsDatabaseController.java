package ru.spbau.group202.notdeadbydeadline.Controller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SettingsDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Settings";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_IS_ODD_WEEK = "IS_ODD_WEEK";
    private static final String COLUMN_NAME_TOTAL_NUMBER_OF_HOMEWORKS = "TOTAL_NUMBER_OF_HOMEWORKS";

    public SettingsDatabaseController(@NotNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_IS_ODD_WEEK + " INTEGER, " +
                COLUMN_NAME_TOTAL_NUMBER_OF_HOMEWORKS + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
/*
    public void addSubject(@NotNull String subject) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    public int getTotalNumberOfHomeworks() {
        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery("SELECT " + COLUMN_NAME_TOTAL_NUMBER_OF_HOMEWORKS
                     + " FROM " + DATABASE_NAME, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
                    subjects.add(subject);
                } while (cursor.moveToNext());
            }
        }

        return subjects;
    }
*/
}

