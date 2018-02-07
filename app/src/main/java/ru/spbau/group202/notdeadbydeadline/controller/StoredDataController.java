package ru.spbau.group202.notdeadbydeadline.controller;


import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

public class StoredDataController {
    private static final String APP_PREFERENCES = "Settings";
    private static final String APP_PREFERENCES_INVERSE_WEEK_PARITY = "INVERSE_WEEK_PARITY";
    private static final String APP_PREFERENCES_TOTAL_NUMBER_OF_HW = "TOTAL_NUMBER_OF_HW";
    private static final String APP_PREFERENCES_TOTAL_NUMBER_OF_SCHEDULE_ENTRIES
            = "TOTAL_NUMBER_OF_SCHEDULE_ENTRIES";
    private static final String APP_PREFERENCES_TOTAL_NUMBER_OF_EXAMS = "TOTAL_NUMBER_OF_EXAMS";
    private static final String APP_PREFERENCES_TOTAL_NUMBER_OF_STUDY_MATERIALS
            = "TOTAL_NUMBER_OF_STUDY_MATERIALS";
    private SharedPreferences settings;

    public StoredDataController(@NotNull Context context) {
        settings = context.getApplicationContext()
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveWeekPairity(boolean isInversed) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_INVERSE_WEEK_PARITY, isInversed);
        editor.apply();
    }

    public void saveTotalNumberOfHW(int totalNumberOfHW) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_HW, totalNumberOfHW);
        editor.apply();
    }

    public void saveTotalNumberOfScheduleEntries(int totalNumberOfScheduleEntries) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_SCHEDULE_ENTRIES, totalNumberOfScheduleEntries);
        editor.apply();
    }

    public void saveTotalNumberOfWorks(int totalNumberOfWorks) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_EXAMS, totalNumberOfWorks);
        editor.apply();
    }

    public void saveTotalNumberOfStudyMaterials(int totalNumberOfStudyMaterials) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_STUDY_MATERIALS, totalNumberOfStudyMaterials);
        editor.apply();
    }

    public boolean getParityOfWeek() {
        return settings.getBoolean(APP_PREFERENCES_INVERSE_WEEK_PARITY, false);
    }

    public int getTotalNumberOfHW() {
        return settings.getInt(APP_PREFERENCES_TOTAL_NUMBER_OF_HW, 0);
    }

    public int getTotalNumberOfScheduleEntries() {
        return settings.getInt(APP_PREFERENCES_TOTAL_NUMBER_OF_SCHEDULE_ENTRIES, 0);
    }

    public int getTotalNumberOfWorks() {
        return settings.getInt(APP_PREFERENCES_TOTAL_NUMBER_OF_EXAMS, 0);
    }

    public int getTotalNumberOfStudyMaterials() {
        return settings.getInt(APP_PREFERENCES_TOTAL_NUMBER_OF_STUDY_MATERIALS, 0);
    }
}