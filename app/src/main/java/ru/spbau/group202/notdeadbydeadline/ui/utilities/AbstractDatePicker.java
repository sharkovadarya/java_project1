package ru.spbau.group202.notdeadbydeadline.ui.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;

public abstract class AbstractDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private int year;
    private int month;
    private int day;

    public AbstractDatePicker() {

        // Use the current date as the default date in the picker
        LocalDate localDate = new LocalDate();
        year = localDate.getYear();
        month = localDate.getMonthOfYear() - 1;
        day = localDate.getDayOfMonth();
    }

    public void setValues(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
}