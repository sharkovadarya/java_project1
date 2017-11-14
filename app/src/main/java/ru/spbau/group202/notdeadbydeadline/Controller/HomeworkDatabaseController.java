package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;

public class HomeworkDatabaseController extends SQLiteOpenHelper {
    //TODO add constants for columns' names
    private static final int DATABASE_VERSION = 1;

    public HomeworkDatabaseController(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        //TODO use constants
        db.execSQL("CREATE TABLE " + "Homeworks" + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "SUBJECT TEXT, " +
                "YEAR INTEGER, " +
                "MONTH INTEGER, " +
                "DAY INTEGER, " +
                "HOUR INTEGER, " +
                "MINUTE INTEGER, " +
                "IS_REGULAR INTEGER, " +
                "EXPECTED_SCORE INTEGER, " +
                "ACTUAL_SCORE INTEGER, " +
                "DESCRIPTION TEXT, " +
                "HOW_TO_SEND TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + "Homeworks");
        onCreate(db);
    }

    public void addHomework(Homework hw) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("SUBJECT", hw.getSubject());
            //TODO decide about returning deadline
            /*
            Deadline deadline = hw.getDeadline();
            cv.put("YEAR", );
            cv.put("MONTH", );
            cv.put("DAY", );
            cv.put("HOUR", );
            cv.put("MINUTE", );
            cv.put("IS_REGULAR", );
            cv.put("EXPECTED_SCORE",);
            cv.put("ACTUAL_SCORE", );
            cv.put("DESCRIPTION", );
            cv.put("HOW_TO_SEND",);
            */
            long rowId = database.insert("Homeworks", null, cv);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    public Homework getHomeworkByCursor(@NotNull Cursor cursor) {
        cursor.moveToFirst();
        int ID = cursor.getInt(cursor.getColumnIndex("ID"));
        String subject = cursor.getString(1);
        int year = cursor.getInt(2);
        int month = cursor.getInt(3);
        int day = cursor.getInt(4);
        int hour = cursor.getInt(5);
        int minute = cursor.getInt(6);
        boolean isRegular = cursor.getInt(7) == 1;
        int expectedScore = cursor.getInt(8);
        int actualScore = cursor.getInt(9);
        String description = cursor.getString(10);
        String howToSend = cursor.getString(11);

        /*Homework hw = new Homework(year, month, day, hour, minute, subject,
                isRegular, description, howToSend, expectedScore);
        hw.setActualScore(actualScore);
        return hw;*/

        return null;
    }
}
