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

import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.ExamEnum;


public class ExamDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Exams";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_YEAR = "YEAR";
    private static final String COLUMN_NAME_MONTH = "MONTH";
    private static final String COLUMN_NAME_DAY = "DAY";
    private static final String COLUMN_NAME_HOUR = "HOUR";
    private static final String COLUMN_NAME_MINUTE = "MINUTE";
    private static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_NAME_EXAM_TYPE = "EXAM_TYPE";
    private static final String COLUMN_NAME_IS_ACCEPTED = "IS_ACCEPTED";

    public ExamDatabaseController(@NotNull Context context) {
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
                COLUMN_NAME_EXAM_TYPE + " INTEGER, " +
                COLUMN_NAME_IS_ACCEPTED + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    @NotNull
    private Exam getExamByCursor(@NotNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int year = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_YEAR));
        int month = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MONTH));
        int day = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DAY));
        int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MINUTE));
        String description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
        int examType = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_EXAM_TYPE));
        boolean isAccepted = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_ACCEPTED)) == 1;

        LocalDateTime date = new LocalDateTime(year, month, day, hour, minute);
        Exam exam = new Exam(subject, description, date, ExamEnum.values()[examType], id);
        exam.setAccepted(isAccepted);
        return exam;
    }

    public void addExam(@NotNull Exam exam) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID, exam.getId());
            values.put(COLUMN_NAME_SUBJECT, exam.getSubject());
            values.put(COLUMN_NAME_YEAR, exam.getYear());
            values.put(COLUMN_NAME_MONTH, exam.getMonth());
            values.put(COLUMN_NAME_DAY, exam.getDay());
            values.put(COLUMN_NAME_HOUR, exam.getHour());
            values.put(COLUMN_NAME_MINUTE, exam.getMinute());
            values.put(COLUMN_NAME_DESCRIPTION, exam.getDescription());
            values.put(COLUMN_NAME_EXAM_TYPE, exam.getExamType().ordinal());
            values.put(COLUMN_NAME_IS_ACCEPTED, exam.isAccepted() ? 1 : 0);
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<Exam> getExamsBySubject(@NotNull String subject) {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_SUBJECT + "=" + "?";
        List<Exam> exams = new ArrayList<>();
        String[] selectionArgs = new String[]{String.valueOf(subject)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    exams.add(getExamByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        Collections.sort(exams);
        return exams;
    }

    @NotNull
    public List<Exam> getAllExams() {
        String query = "SELECT * FROM " + DATABASE_NAME;
        List<Exam> exams = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    exams.add(getExamByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return exams;
    }

    @NotNull
    public List<Exam> getExamsByDay(LocalDate date) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_YEAR + "=? " +
                "AND " + COLUMN_NAME_MONTH + "=? " + "AND " + COLUMN_NAME_DAY + "=?" +
                "ORDER BY " + COLUMN_NAME_HOUR + ", " + COLUMN_NAME_MINUTE + " ASC";
        String[] selectionArgs = new String[]{String.valueOf(date.getYear()),
                String.valueOf(date.getMonthOfYear()), String.valueOf(date.getDayOfMonth())};
        List<Exam> exams = new ArrayList<>();

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Exam exam = getExamByCursor(cursor);
                    exams.add(exam);
                } while (cursor.moveToNext());
            }
        }

        return exams;
    }

    public void deleteExamById(int id) {
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
    public Exam getExamById(int id) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            cursor.moveToFirst();
            return getExamByCursor(cursor);
        }
    }

    public void editExamById(@NotNull String subject, @NotNull String description,
                             @NotNull LocalDateTime date, @NotNull ExamEnum examEnum, int id) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            values.put(COLUMN_NAME_YEAR, date.getYear());
            values.put(COLUMN_NAME_MONTH, date.getMonthOfYear());
            values.put(COLUMN_NAME_DAY, date.getDayOfMonth());
            values.put(COLUMN_NAME_HOUR, date.getHourOfDay());
            values.put(COLUMN_NAME_MINUTE, date.getMinuteOfHour());
            values.put(COLUMN_NAME_DESCRIPTION, description);
            values.put(COLUMN_NAME_EXAM_TYPE, examEnum.ordinal());
            database.update(DATABASE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }
}