package ru.spbau.group202.notdeadbydeadline.controller;


import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;



public class StoredDataController {
    private static final String APP_PREFERENCES = "Settings";
    private static final String APP_PREFERENCES_INVERSE_WEEK_PARITY = "INVERSE_WEEK_PARITY";
    private static final String APP_PREFERENCES_IS_GC_SYNC_HW = "IS_GC_SYNC_HW";
    private static final String APP_PREFERENCES_IS_GC_SYNC_EXAMS = "IS_GC_SYNC_EXAMS";
    private static final String APP_PREFERENCES_IS_GC_SYNC_CLASSES = "GC_SYNC_CLASSES";
    private static final String APP_PREFERENCES_END_TERM_DATE = "END_TERM_DATE";
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

    public void setEndTermDate(LocalDate date) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(APP_PREFERENCES_END_TERM_DATE, date.toString());
        editor.apply();
    }

    public void setWeekParity(boolean isInversed) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_INVERSE_WEEK_PARITY, isInversed);
        editor.apply();
    }

    public void setGoogleCalendarHomeworksSync(boolean isSync) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_IS_GC_SYNC_HW, isSync);
        editor.apply();
    }

    public void setGoogleCalendarExamsSync(boolean isSync) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_IS_GC_SYNC_EXAMS, isSync);
        editor.apply();
    }

    public void setGoogleCalendarClassesSync(boolean isSync) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_IS_GC_SYNC_CLASSES, isSync);
        editor.apply();
    }

    public void setTotalNumberOfHW(int totalNumberOfHW) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_HW, totalNumberOfHW);
        editor.apply();
    }

    public void setTotalNumberOfScheduleEntries(int totalNumberOfScheduleEntries) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_SCHEDULE_ENTRIES, totalNumberOfScheduleEntries);
        editor.apply();
    }

    public void setTotalNumberOfWorks(int totalNumberOfWorks) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_EXAMS, totalNumberOfWorks);
        editor.apply();
    }

    public void setTotalNumberOfStudyMaterials(int totalNumberOfStudyMaterials) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_STUDY_MATERIALS, totalNumberOfStudyMaterials);
        editor.apply();
    }

    public boolean getWeekParity() {
        return settings.getBoolean(APP_PREFERENCES_INVERSE_WEEK_PARITY, false);
    }

    public boolean isGoogleCalendarSyncHomework() {
        return settings.getBoolean(APP_PREFERENCES_IS_GC_SYNC_HW, false);
    }

    public boolean isGoogleCalendarSyncExams() {
        return settings.getBoolean(APP_PREFERENCES_IS_GC_SYNC_EXAMS, false);
    }

    public boolean isGoogleCalendarSyncClasses() {
        return settings.getBoolean(APP_PREFERENCES_IS_GC_SYNC_CLASSES, false);
    }

    public LocalDate getEndTermDate() {
        String endTermDate = settings.getString(APP_PREFERENCES_END_TERM_DATE, "");
        return endTermDate.isEmpty() ? null : new LocalDate(endTermDate);
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