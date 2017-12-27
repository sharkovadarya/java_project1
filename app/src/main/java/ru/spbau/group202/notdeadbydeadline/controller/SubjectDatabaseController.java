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

import ru.spbau.group202.notdeadbydeadline.model.CreditByAcceptedHomeworks;
import ru.spbau.group202.notdeadbydeadline.model.CreditByPercent;
import ru.spbau.group202.notdeadbydeadline.model.CreditEnum;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;


public class SubjectDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Subjects";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_CREDIT_FORM = "CREDIT_FORM";
    private static final String COLUMN_NAME_PERCENT_FOR_CREDIT = "PERCENT_FOR_CREDIT";

    public SubjectDatabaseController(@NotNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_SUBJECT + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_CREDIT_FORM + " INTEGER, " +
                COLUMN_NAME_PERCENT_FOR_CREDIT + " REAL" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public void addSubject(@NotNull String subject, @NotNull CreditEnum creditForm, double percentForCredit) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            values.put(COLUMN_NAME_CREDIT_FORM, creditForm.ordinal());
            values.put(COLUMN_NAME_PERCENT_FOR_CREDIT, percentForCredit);
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<String> getAllSubjects() {
        ArrayList<String> subjects = new ArrayList<>();
        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery("SELECT " + COLUMN_NAME_SUBJECT
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

    @NotNull
    private SubjectCredit getSubjectCreditByCursor(@NotNull Cursor cursor) throws UnrecognizedCreditFormException {
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int creditForm = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_CREDIT_FORM));
        double percentForCredit = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PERCENT_FOR_CREDIT));

        switch (CreditEnum.values()[creditForm]) {
            case ByPercent:
                return new CreditByPercent(subject, percentForCredit);
            case ByAcceptedHomeworks:
                return new CreditByAcceptedHomeworks(subject);
            case NotStated:
                return new SubjectCredit(subject);
            default:
                throw new UnrecognizedCreditFormException();
        }
    }

    @NotNull
    public SubjectCredit getSubjectCredit(@NotNull String subject) throws UnrecognizedCreditFormException {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_SUBJECT + "=?";
        String[] selectionArgs = new String[]{subject};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            return getSubjectCreditByCursor(cursor);
        }
    }

    public void setSubjectCreditForm(@NotNull String subject, @NotNull CreditEnum credit) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_CREDIT_FORM, credit.ordinal());
            database.update(DATABASE_NAME, values, COLUMN_NAME_SUBJECT + " = ?",
                    new String[]{subject});
        }
    }
}
