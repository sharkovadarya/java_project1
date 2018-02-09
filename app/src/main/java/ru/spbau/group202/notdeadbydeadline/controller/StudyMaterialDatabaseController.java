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

import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;

public class StudyMaterialDatabaseController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudyMaterials";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_SUBJECT = "SUBJECT";
    private static final String COLUMN_NAME_TERM = "TERM";
    private static final String COLUMN_NAME_VERSION = "VERSION";
    private static final String COLUMN_NAME_PATH = "PATH";
    private static final String COLUMN_NAME_NAME = "NAME";

    public StudyMaterialDatabaseController(@NotNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate database");
        db.execSQL("CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_SUBJECT + " TEXT, " +
                COLUMN_NAME_TERM + " INTEGER, " +
                COLUMN_NAME_VERSION + " INTEGER, " +
                COLUMN_NAME_PATH + " TEXT, " +
                COLUMN_NAME_NAME + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    @NotNull
    private StudyMaterial getStudyMaterialByCursor(@NotNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
        String subject = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUBJECT));
        int term = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TERM));
        int version = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_VERSION));
        String path = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PATH));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME));
        return new StudyMaterial(name, subject, term, path, version, id);
    }

    public void addStudyMaterial(@NotNull StudyMaterial studyMaterial) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID, studyMaterial.getId());
            values.put(COLUMN_NAME_SUBJECT, studyMaterial.getSubject());
            values.put(COLUMN_NAME_TERM, studyMaterial.getTerm());
            values.put(COLUMN_NAME_VERSION, studyMaterial.getVersion());
            values.put(COLUMN_NAME_PATH, studyMaterial.getPath());
            values.put(COLUMN_NAME_NAME, studyMaterial.getName());
            long rowId = database.insert(DATABASE_NAME, null, values);
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    @NotNull
    public List<StudyMaterial> getStudyMaterialsBySubject(@NotNull String subject) {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_SUBJECT + "=" + "?";
        List<StudyMaterial> studyMaterials = new ArrayList<>();
        String[] selectionArgs = new String[]{String.valueOf(subject)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    studyMaterials.add(getStudyMaterialByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return studyMaterials;
    }

    @NotNull
    public List<StudyMaterial> getStudyMaterialsByTerm(int term) {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_TERM + "=" + "?";
        List<StudyMaterial> studyMaterials = new ArrayList<>();
        String[] selectionArgs = new String[]{String.valueOf(term)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    studyMaterials.add(getStudyMaterialByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return studyMaterials;
    }

    public void deleteStudyMaterialById(int id) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            database.delete(DATABASE_NAME, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    @NotNull
    public StudyMaterial getStudyMaterialById(int id) {
        String query = "SELECT * FROM " + DATABASE_NAME + " WHERE " + COLUMN_NAME_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            return getStudyMaterialByCursor(cursor);
        }
    }

    public void editStudyMaterialById(int id, @NotNull String subject, int term) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_SUBJECT, subject);
            values.put(COLUMN_NAME_TERM, true);
            database.update(DATABASE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    @NotNull
    public List<StudyMaterial> getUpdatableStudyMaterials() {
        String query = "SELECT * FROM " + DATABASE_NAME +
                " WHERE " + COLUMN_NAME_VERSION + "!=" + "?";
        List<StudyMaterial> studyMaterials = new ArrayList<>();
        String[] selectionArgs = new String[]{"-1"};

        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery(query, selectionArgs)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    studyMaterials.add(getStudyMaterialByCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return studyMaterials;
    }
}
