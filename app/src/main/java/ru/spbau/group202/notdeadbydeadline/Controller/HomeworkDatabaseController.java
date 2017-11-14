package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;

public class HomeworkDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Homeworks";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_YEAR = "YEAR";
    private static final String COLUMN_NAME_MONTH = "MONTH";
    private static final String COLUMN_NAME_DAY = "DAY";
    private static final String COLUMN_NAME_HOUR = "HOUR";
    private static final String COLUMN_NAME_MINUTE = "MINUTE";
    private static final String COLUMN_NAME_IS_REGULAR = "IS_REGULAR";
    private static final String COLUMN_NAME_EXPECTED_SCORE = "EXPECTED_SCORE";
    private static final String COLUMN_NAME_ACTUAL_SCORE = "ACTUAL_SCORE";
    private static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_NAME_HOW_TO_SEND = "HOW_TO_SEND";

    private Homework getHomeworkByCursor(@NotNull Cursor cursor) {
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int year = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DAY));
        int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MINUTE));
        boolean isRegular = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_REGULAR)) == 1;
        int expectedScore = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_EXPECTED_SCORE));
        int actualScore = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ACTUAL_SCORE));
        String description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
        String howToSend = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOW_TO_SEND));

        Homework homework = new Homework(year, month, day, hour, minute, subject,
                isRegular, description, howToSend, expectedScore);
        homework.setActualScore(actualScore);
        return homework;
    }

    public HomeworkDatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SUBJECT + " TEXT, " +
                COLUMN_NAME_YEAR + " INTEGER, " +
                COLUMN_NAME_MONTH + " INTEGER, " +
                COLUMN_NAME_DAY + " INTEGER, " +
                COLUMN_NAME_HOUR + " INTEGER, " +
                COLUMN_NAME_MINUTE + " INTEGER, " +
                COLUMN_NAME_IS_REGULAR + " INTEGER, " +
                COLUMN_NAME_EXPECTED_SCORE + " INTEGER, " +
                COLUMN_NAME_ACTUAL_SCORE + " INTEGER, " +
                COLUMN_NAME_DESCRIPTION + " TEXT, " +
                COLUMN_NAME_HOW_TO_SEND + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public void addHomework(Homework homework) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, homework.getSubject());
            values.put(COLUMN_NAME_YEAR, homework.getYear());
            values.put(COLUMN_NAME_MONTH, homework.getMonth());
            values.put(COLUMN_NAME_DAY, homework.getDay());
            values.put(COLUMN_NAME_HOUR, homework.getHour());
            values.put(COLUMN_NAME_MINUTE, homework.getMinute());
            values.put(COLUMN_NAME_IS_REGULAR, homework.isRegular() ? 1 : 0);
            values.put(COLUMN_NAME_EXPECTED_SCORE, homework.getExpectedScore());
            values.put(COLUMN_NAME_ACTUAL_SCORE, homework.getActualScore());
            values.put(COLUMN_NAME_DESCRIPTION, homework.getDescription());
            values.put(COLUMN_NAME_HOW_TO_SEND, homework.getHowToSend());
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    public ArrayList<Homework> getActualHomeworks() {
        ArrayList<Homework> homeworks = new ArrayList<>();
        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery("SELECT * FROM " + DATABASE_NAME, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Homework homework = getHomeworkByCursor(cursor);
                    if (homework.hasPassed()) {
                        homeworks.add(homework);
                    }
                } while (cursor.moveToNext());
            }
        }

        return homeworks;
    }

    public ArrayList<Homework> getHomeworksBySubject(String subject) {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_SUBJECT + "=" + subject;
        ArrayList<Homework> homeworks = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    homeworks.add(getHomeworkByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return homeworks;
    }

    public ArrayList<Homework> getHomeworksByDay(int year, int month, int day) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_YEAR + "=? " +
                "AND " + COLUMN_NAME_MONTH + "=? " + "AND " + COLUMN_NAME_DAY + "=?";
        String[] selectionArgs = new String[]{String.valueOf(year),
                String.valueOf(month), String.valueOf(day)};
        ArrayList<Homework> homeworks = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Homework homework = getHomeworkByCursor(cursor);
                    homeworks.add(homework);
                } while (cursor.moveToNext());
            }
        }

        return homeworks;
    }
}
