package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.Work;
import ru.spbau.group202.notdeadbydeadline.model.WorkEnum;


//TODO rename
class WorkDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Works";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_YEAR = "YEAR";
    private static final String COLUMN_NAME_MONTH = "MONTH";
    private static final String COLUMN_NAME_DAY = "DAY";
    private static final String COLUMN_NAME_HOUR = "HOUR";
    private static final String COLUMN_NAME_MINUTE = "MINUTE";
    private static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_NAME_KIND = "KIND";
    private static final String COLUMN_NAME_IS_ACCEPTED = "IS_ACCEPTED";

    public WorkDatabaseController(@NotNull Context context) {
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
                COLUMN_NAME_DESCRIPTION + " TEXT, " +
                COLUMN_NAME_KIND + " INTEGER, " +
                COLUMN_NAME_IS_ACCEPTED + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    @NotNull
    private Work getWorkByCursor(@NotNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int year = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DAY));
        int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MINUTE));
        String description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
        int kind = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_KIND));
        boolean isAccepted = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_ACCEPTED)) == 1;

        LocalDateTime date = new LocalDateTime(year, month, day, hour, minute);
        Work work = new Work(subject, description, date, WorkEnum.values()[kind], id);
        work.setAccepted(isAccepted);
        return work;
    }

    public void addWork(@NotNull Work work) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID, work.getId());
            values.put(COLUMN_NAME_SUBJECT, work.getSubject());
            values.put(COLUMN_NAME_YEAR, work.getYear());
            values.put(COLUMN_NAME_MONTH, work.getMonth());
            values.put(COLUMN_NAME_DAY, work.getDay());
            values.put(COLUMN_NAME_HOUR, work.getHour());
            values.put(COLUMN_NAME_MINUTE, work.getMinute());
            values.put(COLUMN_NAME_DESCRIPTION, work.getDescription());
            values.put(COLUMN_NAME_KIND, work.getKind().ordinal());
            values.put(COLUMN_NAME_IS_ACCEPTED, work.isAccepted() ? 1 : 0);
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<Work> getWorksBySubject(@NotNull String subject) {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_SUBJECT + "=" + "?";
        List<Work> works = new ArrayList<>();
        String[] selectionArgs = new String[]{String.valueOf(subject)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    works.add(getWorkByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        Collections.sort(works);
        return works;
    }

    @NotNull
    public List<Work> getWorksByDay(LocalDate date) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_YEAR + "=? " +
                "AND " + COLUMN_NAME_MONTH + "=? " + "AND " + COLUMN_NAME_DAY + "=?" +
                "ORDER BY " + COLUMN_NAME_HOUR + ", " + COLUMN_NAME_MINUTE + " ASC";
        String[] selectionArgs = new String[]{String.valueOf(date.getYear()),
                String.valueOf(date.getMonthOfYear()), String.valueOf(date.getDayOfMonth())};
        List<Work> works = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Work work = getWorkByCursor(cursor);
                    works.add(work);
                } while (cursor.moveToNext());
            }
        }

        return works;
    }

    public void deleteWorkById(int id) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            database.delete(DATABASE_NAME, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public void setAcceptedById(int id, boolean isAccepted) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_IS_ACCEPTED, isAccepted);
            database.update(DATABASE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    @NotNull
    public List<Work> getWorkById(int id) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        List<Work> works = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Work work = getWorkByCursor(cursor);
                    works.add(work);
                } while (cursor.moveToNext());
            }
        }

        return works;
    }
}
