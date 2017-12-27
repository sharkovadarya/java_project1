package ru.spbau.group202.notdeadbydeadline.ui.utilities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;

public abstract class AbstractTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        LocalTime localTime = new LocalTime();
        int hour = localTime.getHourOfDay();
        int minute = localTime.getMinuteOfHour();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
}