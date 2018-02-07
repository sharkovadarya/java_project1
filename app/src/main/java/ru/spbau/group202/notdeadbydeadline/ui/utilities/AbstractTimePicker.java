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

    private int hour;
    private int minute;

    public AbstractTimePicker() {
        // Use the current time as the default values for the picker
        LocalTime localTime = new LocalTime();
        hour = localTime.getHourOfDay();
        minute = localTime.getMinuteOfHour();
    }

    public void setValues(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
}