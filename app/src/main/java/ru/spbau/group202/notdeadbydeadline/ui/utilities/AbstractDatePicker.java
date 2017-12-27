package ru.spbau.group202.notdeadbydeadline.ui.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;

public abstract class AbstractDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        LocalDate localDate = new LocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthOfYear() - 1;
        int day = localDate.getDayOfMonth();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
}