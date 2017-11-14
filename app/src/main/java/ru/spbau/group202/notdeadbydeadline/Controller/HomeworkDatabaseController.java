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
    private static final String DATABASE_NAME = "Homeworks";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_YEAR = "YEAR";
    private static final String COLUMN_NAME_MONTH = "MONTH";
    private static final String COLUMN_NAME_DAY = "DAY";
    private static final String COLUMN_NAME_HOUR = "HOUR";
    private static final String COLUMN_NAME_MINUTE = "MINUTE";
    private static final String COLUMN_NAME_IS_REGULAR = "REGULAR";
    private static final String COLUMN_NAME_EXPECTED_SCORE = "EXPECTED_SCORE";
    private static final String COLUMN_NAME_ACTUAL_SCORE = "ACTUAL_SCORE";
    private static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_NAME_HOW_TO_SEND = "HOW_TO_SEND";

    public HomeworkDatabaseController(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        //TODO use constants
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
            //TODO decide about returning deadline
            /*
            values.put("YEAR", );
            values.put("MONTH", );
            values.put("DAY", );
            values.put("HOUR", );
            values.put("MINUTE", );
            values.put("IS_REGULAR", );
            values.put("EXPECTED_SCORE",);
            values.put("ACTUAL_SCORE", );
            values.put("DESCRIPTION", );
            values.put("HOW_TO_SEND",);
            */
            long rowId = database.insert("Homeworks", null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    public Homework getHomeworkByCursor(@NotNull Cursor cursor) {
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
}
