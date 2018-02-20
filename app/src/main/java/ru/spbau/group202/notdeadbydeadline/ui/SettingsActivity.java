package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractDatePicker;

public class SettingsActivity extends AppCompatActivity {

    public void onAddHomeworksToGC(View view) {
        Controller.getInstance(SettingsActivity.this).homeworkController()
                .resetHomeworksInGoogleCalendar();
    }

    public void onAddClassEntriesToGC(View view) {
        if (Controller.getInstance(SettingsActivity.this).getEndTermDate() == null) {
            Toast.makeText(getApplicationContext(),
                    "Set term end date", Toast.LENGTH_LONG).show();
        } else {
            Controller.getInstance(SettingsActivity.this).scheduleController()
                    .resetClassEntriesInGoogleCalendar();
        }
    }

    public void onInvertWeekParity(View view) {
        Controller.getInstance(SettingsActivity.this).setWeekParity(
                !Controller.getInstance(SettingsActivity.this).getWeekParity());
        outputWeekParity();
    }

    public void onSetTermEndDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setContext(SettingsActivity.this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void outputWeekParity() {
        TextView parityTextView = findViewById(R.id.currentWeekParityTV);
        String parity;
        if (!Controller.getInstance(SettingsActivity.this).getWeekParity()) {
            parity = getResources().getString(R.string.week_parity,
                    LocalDate.now().getWeekOfWeekyear() % 2 == 0 ? "even" : "odd");
        } else {
            parity = getResources().getString(R.string.week_parity,
                    LocalDate.now().getWeekOfWeekyear() % 2 == 1 ? "even" : "odd");
        }
        parityTextView.setText(parity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        outputWeekParity();
    }

    public static class DatePickerFragment extends AbstractDatePicker {
        Context context;

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Controller.getInstance(context)
                    .setEndTermDate(new LocalDate(year, month + 1, dayOfMonth));
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }
}
