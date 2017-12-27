package ru.spbau.group202.notdeadbydeadline.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ru.spbau.group202.notdeadbydeadline.R;

public class AddScheduleEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add a new schedule entry");
        }

    }

    public void setWeekDay(View view) {

    }

    public void setTime(View view) {

    }

    // TODO fill this class and add storage methods
    public static class ScheduleEntryFieldsAccumulator{
        private String subject;
        private String teacher;
        private String auditorium;
        private String weekDay;

    }

}
