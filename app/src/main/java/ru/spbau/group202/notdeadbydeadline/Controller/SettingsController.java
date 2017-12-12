package ru.spbau.group202.notdeadbydeadline.Controller;


import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

public class SettingsController {
    private static final String APP_PREFERENCES = "Settings";
    private static final String APP_PREFERENCES_INVERSE_WEEK_PARITY = "INVERSE_WEEK_PARITY";
    private static final String APP_PREFERENCES_TOTAL_NUMBER_OF_HW = "TOTAL_NUMBER_OF_HW";
    private SharedPreferences settings;

    public SettingsController(@NotNull Context context) {
        settings = context.getApplicationContext()
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void savePairityOfWeek(boolean isInversed) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_INVERSE_WEEK_PARITY, isInversed);
        editor.apply();
    }

    public void saveTotalNumberOfHW(int totalNumberOfHW) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_TOTAL_NUMBER_OF_HW, totalNumberOfHW);
        editor.apply();
    }

    public boolean getParityOfWeek() {
        return settings.getBoolean(APP_PREFERENCES_INVERSE_WEEK_PARITY, false);
    }

    public int getTotalNumberOfHW() {
        return settings.getInt(APP_PREFERENCES_TOTAL_NUMBER_OF_HW, 0);
    }
}