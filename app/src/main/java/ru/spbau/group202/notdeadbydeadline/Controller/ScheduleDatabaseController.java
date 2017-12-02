package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Model.Class;
import ru.spbau.group202.notdeadbydeadline.Model.Homework;

public class ScheduleDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Schedule";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_DAY_OF_WEEK = "DAY_OF_WEEK";
    private static final String COLUMN_NAME_HOUR = "HOUR";
    private static final String COLUMN_NAME_MINUTE = "MINUTE";
    private static final String COLUMN_NAME_IS_ON_EVEN_WEEK = "IS_ON_EVEN_WEEK";
    private static final String COLUMN_NAME_AUDITORIUM = "AUDITORIUM";
    private static final String COLUMN_NAME_TEACHER = "TEACHER";

    public ScheduleDatabaseController(@NotNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SUBJECT + " TEXT, " +
                COLUMN_NAME_DAY_OF_WEEK + " INTEGER, " +
                COLUMN_NAME_HOUR + " INTEGER, " +
                COLUMN_NAME_MINUTE + " INTEGER, " +
                COLUMN_NAME_IS_ON_EVEN_WEEK + " INTEGER, " +
                COLUMN_NAME_AUDITORIUM + " TEXT, " +
                COLUMN_NAME_TEACHER + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    private Class getScheduleEntryByCursor(@NotNull Cursor cursor) {
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int dayOfWeek = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DAY_OF_WEEK));
        int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MINUTE));
        boolean isOnEvenWeek = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_ON_EVEN_WEEK)) == 1;
        String auditorium = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AUDITORIUM));
        String teacher = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TEACHER));

        return new Class(subject, dayOfWeek, hour, minute,
                isOnEvenWeek, auditorium, teacher);
    }

    public void addScheduleEntry(@NotNull Class scheduleEntry) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, scheduleEntry.getSubject());
            values.put(COLUMN_NAME_DAY_OF_WEEK, scheduleEntry.getDayOfWeek());
            values.put(COLUMN_NAME_HOUR, scheduleEntry.getHour());
            values.put(COLUMN_NAME_MINUTE, scheduleEntry.getMinute());
            values.put(COLUMN_NAME_IS_ON_EVEN_WEEK, scheduleEntry.isOnEvenWeeks() ? 1 : 0);
            values.put(COLUMN_NAME_AUDITORIUM, scheduleEntry.getAuditorium());
            values.put(COLUMN_NAME_TEACHER, scheduleEntry.getTeacher());
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    public ArrayList<Class> getDaySchedule(int dayOfWeek, boolean isOnEvenWeek) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_DAY_OF_WEEK + "=? " +
                "AND " + COLUMN_NAME_IS_ON_EVEN_WEEK + "=? " +
                "ORDER BY " + COLUMN_NAME_HOUR + ", " + COLUMN_NAME_MINUTE;

        String[] selectionArgs = new String[]{String.valueOf(dayOfWeek),
                String.valueOf(isOnEvenWeek ? 1 : 0)};
        ArrayList<Class> daySchedule = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Class scheduleEntry= getScheduleEntryByCursor(cursor);
                    daySchedule.add(scheduleEntry);
                } while (cursor.moveToNext());
            }
        }

        return daySchedule;
    }

}
