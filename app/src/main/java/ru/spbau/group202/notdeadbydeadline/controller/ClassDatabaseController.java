package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.ClassEntry;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;

public class ClassDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Schedule";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_DAY_OF_WEEK = "DAY_OF_WEEK";
    private static final String COLUMN_NAME_HOUR = "HOUR";
    private static final String COLUMN_NAME_MINUTE = "MINUTE";
    private static final String COLUMN_NAME_WEEK_PARITY = "WEEK_PARITY";
    private static final String COLUMN_NAME_AUDITORIUM = "AUDITORIUM";
    private static final String COLUMN_NAME_TEACHER = "TEACHER";

    public ClassDatabaseController(@NotNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_SUBJECT + " TEXT, " +
                COLUMN_NAME_DAY_OF_WEEK + " INTEGER, " +
                COLUMN_NAME_HOUR + " INTEGER, " +
                COLUMN_NAME_MINUTE + " INTEGER, " +
                COLUMN_NAME_WEEK_PARITY + " INTEGER, " +
                COLUMN_NAME_AUDITORIUM + " TEXT, " +
                COLUMN_NAME_TEACHER + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    @NotNull
    private ClassEntry getClassByCursor(@NotNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int dayOfWeek = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DAY_OF_WEEK));
        int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MINUTE));
        int weekParity = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_WEEK_PARITY));
        String auditorium = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AUDITORIUM));
        String teacher = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TEACHER));

        return new ClassEntry(subject, dayOfWeek, hour, minute,
                WeekParityEnum.values()[weekParity], auditorium, teacher, id);
    }

    public void addClass(@NotNull ClassEntry aClass) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID, aClass.getId());
            values.put(COLUMN_NAME_SUBJECT, aClass.getSubject());
            values.put(COLUMN_NAME_DAY_OF_WEEK, aClass.getDayOfWeek());
            values.put(COLUMN_NAME_HOUR, aClass.getHour());
            values.put(COLUMN_NAME_MINUTE, aClass.getMinute());
            values.put(COLUMN_NAME_WEEK_PARITY, aClass.getWeekParity().ordinal());
            values.put(COLUMN_NAME_AUDITORIUM, aClass.getAuditorium());
            values.put(COLUMN_NAME_TEACHER, aClass.getTeacher());
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<ClassEntry> getDaySchedule(int dayOfWeek, WeekParityEnum weekParity) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_DAY_OF_WEEK + "=? " +
                "AND (" + COLUMN_NAME_WEEK_PARITY + "=? " + " OR " + COLUMN_NAME_WEEK_PARITY + "=?) " +
                "ORDER BY " + COLUMN_NAME_HOUR + ", " + COLUMN_NAME_MINUTE;

        String[] selectionArgs = new String[]{String.valueOf(dayOfWeek),
                String.valueOf(weekParity.ordinal()), String.valueOf(WeekParityEnum.ALWAYS.ordinal())};
        List<ClassEntry> daySchedule = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    daySchedule.add(getClassByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return daySchedule;
    }

    @NotNull
    public List<ClassEntry> getAllClasses() {
        String query = "SELECT * FROM " + DATABASE_NAME;
        List<ClassEntry> classes = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    classes.add(getClassByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return classes;
    }

    public void deleteClassById(int id) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            database.delete(DATABASE_NAME, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public ClassEntry getClassById(int id) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            cursor.moveToFirst();
            return getClassByCursor(cursor);
        }
    }

    public void editClassById(@NotNull String subject, int dayOfWeek, int hour, int minute,
                                      @NotNull WeekParityEnum weekParity, @NotNull String auditorium,
                                      @NotNull String teacher, int id) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            values.put(COLUMN_NAME_DAY_OF_WEEK, dayOfWeek);
            values.put(COLUMN_NAME_HOUR, hour);
            values.put(COLUMN_NAME_MINUTE, minute);
            values.put(COLUMN_NAME_WEEK_PARITY, weekParity.ordinal());
            values.put(COLUMN_NAME_AUDITORIUM, auditorium);
            values.put(COLUMN_NAME_TEACHER, teacher);
            database.update(DATABASE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }
}