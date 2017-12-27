package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractTimePicker;

public class AddScheduleEntryActivity extends AppCompatActivity {

    private static final ScheduleEntryFieldsAccumulator SEFA = new ScheduleEntryFieldsAccumulator();
    private static boolean isSetTime = false;

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

        Button addSEbutton = findViewById(R.id.scheduleFinishButton);

    }

    public void setWeekDay(View view) {
        Button button = findViewById(R.id.scheduleSetWeekDayButton);
        PopupMenu popup = new PopupMenu(AddScheduleEntryActivity.this, button);
        popup.getMenuInflater()
                .inflate(R.menu.week_day_menu, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                SEFA.storeWeekDay(item.getTitle().toString());// item.getTitle()
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    public void scheduleSetTime(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void processSubject() {
        EditText editText = findViewById(R.id.scheduleGetSubjectET);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SEFA.storeSubject((editText.getText().toString()));


                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG", "Done pressed");
                }
                return false;
            }
        });

        SEFA.storeSubject(editText.getText().toString());
    }

    public void getSubject() {
        EditText editText = findViewById(R.id.scheduleGetSubjectET);
        SEFA.storeSubject(editText.getText().toString());
    }

    // TODO fill this class and add storage methods
    public static class ScheduleEntryFieldsAccumulator{
        private String subject = null;
        private String teacher = null;
        private String auditorium = null;
        private String weekDay = null;
        private int hour;
        private int minute;

        public void storeWeekDay(String weekDay) {
            this.weekDay = weekDay;
        }

        public void storeSubject(String subject) {
            this.subject = subject;
        }

        public void storeAuditorium(String auditorium) {
            this.auditorium = auditorium;
        }

        public void storeTeacher(String teacher) {
            this.teacher = teacher;
        }

        public void storeTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

    }

    public static class TimePickerFragment extends AbstractTimePicker {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            isSetTime = true;
            SEFA.storeTime(hourOfDay, minute);
        }
    }

}
