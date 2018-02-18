package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.Homework;

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
    private static final String COLUMN_NAME_REGULARITY = "REGULARITY";
    private static final String COLUMN_NAME_EXPECTED_SCORE = "EXPECTED_SCORE";
    private static final String COLUMN_NAME_ACTUAL_SCORE = "ACTUAL_SCORE";
    private static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_NAME_HOW_TO_SEND = "HOW_TO_SEND";
    private static final String COLUMN_NAME_DEFERRAL = "DEFERRAL";
    private static final String COLUMN_NAME_MATERIALS = "MATERIALS";

    public HomeworkDatabaseController(@NotNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_SUBJECT + " TEXT, " +
                COLUMN_NAME_YEAR + " INTEGER, " +
                COLUMN_NAME_MONTH + " INTEGER, " +
                COLUMN_NAME_DAY + " INTEGER, " +
                COLUMN_NAME_HOUR + " INTEGER, " +
                COLUMN_NAME_MINUTE + " INTEGER, " +
                COLUMN_NAME_REGULARITY + " INTEGER, " +
                COLUMN_NAME_EXPECTED_SCORE + " REAL, " +
                COLUMN_NAME_ACTUAL_SCORE + " INTEGER, " +
                COLUMN_NAME_DESCRIPTION + " TEXT, " +
                COLUMN_NAME_HOW_TO_SEND + " TEXT, " +
                COLUMN_NAME_DEFERRAL + " INTEGER, " +
                COLUMN_NAME_MATERIALS + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    @NotNull
    private Homework getHomeworkByCursor(@NotNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int year = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DAY));
        int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MINUTE));
        int regularity = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_REGULARITY));
        double expectedScore = cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_EXPECTED_SCORE));
        int actualScore = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ACTUAL_SCORE));
        String description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
        String howToSend = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOW_TO_SEND));
        int deferral = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DEFERRAL));
        String gsonMaterials = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MATERIALS));
        ArrayList<String> materials = new Gson().fromJson(gsonMaterials,
                new TypeToken<ArrayList<String>>() {
                }.getType());


        LocalDateTime deadline = new LocalDateTime(year, month, day, hour, minute);
        Homework homework = new Homework(deadline, subject, regularity, description, howToSend,
                expectedScore, id, materials);
        homework.setActualScore(actualScore);
        homework.assignDeferral(deferral);
        return homework;
    }

    public void addHomework(@NotNull Homework homework) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID, homework.getId());
            values.put(COLUMN_NAME_SUBJECT, homework.getSubject());
            values.put(COLUMN_NAME_YEAR, homework.getYear());
            values.put(COLUMN_NAME_MONTH, homework.getMonth());
            values.put(COLUMN_NAME_DAY, homework.getDay());
            values.put(COLUMN_NAME_HOUR, homework.getHour());
            values.put(COLUMN_NAME_MINUTE, homework.getMinute());
            values.put(COLUMN_NAME_REGULARITY, homework.getRegularity());
            values.put(COLUMN_NAME_EXPECTED_SCORE, homework.getExpectedScore());
            values.put(COLUMN_NAME_ACTUAL_SCORE, homework.getActualScore());
            values.put(COLUMN_NAME_DESCRIPTION, homework.getDescription());
            values.put(COLUMN_NAME_HOW_TO_SEND, homework.getHowToSend());
            values.put(COLUMN_NAME_DEFERRAL, homework.getDeferral());
            values.put(COLUMN_NAME_MATERIALS, new Gson().toJson(homework.getMaterials()));
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<Homework> getHomeworksBySubject(@NotNull String subject) {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_SUBJECT + "=" + "?";
        List<Homework> homeworks = new ArrayList<>();
        String[] selectionArgs = new String[]{String.valueOf(subject)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    homeworks.add(getHomeworkByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        Collections.sort(homeworks, Homework::compareTo);
        return homeworks;
    }

    @NotNull
    public List<Homework> getActualHomeworks() {
        String query = "SELECT * FROM " + DATABASE_NAME;
        List<Homework> homeworks = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Homework homework = getHomeworkByCursor(cursor);
                    if(!homework.hasPassed()) {
                        homeworks.add(homework);
                    }
                } while (cursor.moveToNext());
            }
        }

        return homeworks;
    }

    @NotNull
    public List<Homework> getPassedHomeworksBySubject(@NotNull String subject) {
        List<Homework> passedHomeworks = new ArrayList<>();
        for (Homework homework : getHomeworksBySubject(subject)) {
            if (homework.hasPassed()) {
                passedHomeworks.add(homework);
            }
        }

        return passedHomeworks;
    }

    @NotNull
    public List<Homework> getHomeworksByDay(LocalDate date) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_YEAR + "=? " +
                "AND " + COLUMN_NAME_MONTH + "=? " + "AND " + COLUMN_NAME_DAY + "=?" +
                "ORDER BY " + COLUMN_NAME_HOUR + ", " + COLUMN_NAME_MINUTE + " ASC";
        String[] selectionArgs = new String[]{String.valueOf(date.getYear()),
                String.valueOf(date.getMonthOfYear()), String.valueOf(date.getDayOfMonth())};
        List<Homework> homeworks = new ArrayList<>();

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

    public void deleteHomeworkById(int id) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            database.delete(DATABASE_NAME, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public void setScoreById(int id, int score) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ACTUAL_SCORE, score);
            database.update(DATABASE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public void setDeferralById(int id, int deferral) {
        Homework homework = getHomeworkById(id);
        deleteHomeworkById(id);
        homework.setDeferral(deferral);
        addHomework(homework);
    }

    @NotNull
    public Homework getHomeworkById(int id) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            cursor.moveToFirst();
            return getHomeworkByCursor(cursor);
        }
    }

    public void editHomeworkById(LocalDateTime deadline, @NotNull String subject, int regularity,
                                 @NotNull String description, @NotNull String howToSend,
                                 double expectedScore, int id, @NotNull ArrayList<String> materials) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            values.put(COLUMN_NAME_YEAR, deadline.getYear());
            values.put(COLUMN_NAME_MONTH, deadline.getMonthOfYear());
            values.put(COLUMN_NAME_DAY, deadline.getDayOfMonth());
            values.put(COLUMN_NAME_HOUR, deadline.getHourOfDay());
            values.put(COLUMN_NAME_MINUTE, deadline.getMinuteOfHour());
            values.put(COLUMN_NAME_REGULARITY, regularity);
            values.put(COLUMN_NAME_EXPECTED_SCORE, expectedScore);
            values.put(COLUMN_NAME_DESCRIPTION, description);
            values.put(COLUMN_NAME_HOW_TO_SEND, howToSend);
            values.put(COLUMN_NAME_MATERIALS, new Gson().toJson(materials));
            database.update(DATABASE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }
}