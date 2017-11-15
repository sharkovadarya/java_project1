package ru.spbau.group202.notdeadbydeadline.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.ContentObservable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        controller.addHomeworkManager.storeDate(year, month, day);
    }
}
