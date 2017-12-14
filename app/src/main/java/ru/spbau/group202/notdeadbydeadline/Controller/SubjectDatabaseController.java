package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.Model.SubjectCredit;


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
                COLUMN_NAME_PERCENT_FOR_CREDIT + " REAL, " + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    //TODO creditForm type?
    public void addSubject(@NotNull String subject, creditForm, double percentForCredit) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            values.put(COLUMN_NAME_CREDIT_FORM, crediForm);
            values.put(COLUMN_NAME_PERCENT_FOR_CREDIT, percentForCredit);
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<String> getAllSubjects() {
        ArrayList<String> subjects = new ArrayList<>();
        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery("SELECT * FROM " + DATABASE_NAME, null)) {
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
    private SubjectCredit getSubjectCreditByCursor(@NotNull Cursor cursor) {
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        creditForm = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_CREDIT_FORM));
        double percentForCredit = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PERCENT_FOR_CREDIT));

        //TODO
        if(creditForm){
            return
        }
        else if(){
            return
        }
        return
    }

    @NotNull
    public SubjectCredit getSubjectCredit(@NotNull String subject) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_SUBJECT + "=?";
        String[] selectionArgs = new String[]{subject};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                SubjectCredit subjectCredit = getSubjectCreditByCursor(cursor);
                return subjectCredit;
            }
        }
    }
}
