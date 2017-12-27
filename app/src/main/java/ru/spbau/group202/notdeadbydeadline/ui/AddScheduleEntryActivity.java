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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractTimePicker;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.WeekDayEnum;

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

        getSubject();
        getAuditorium();
        getTeacher();
        getParity();

        Button addSEbutton = findViewById(R.id.scheduleFinishButton);
        addSEbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSubject();
                getAuditorium();
                getTeacher();
                getParity();

                if (SEFA.isValidSE()) {
                    SEFA.addScheduleEntry();
                    SEFA.clear();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Fill all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public void getSubject() {
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

    public void getTeacher() {
        EditText editText = findViewById(R.id.scheduleTeacherET);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SEFA.storeTeacher((editText.getText().toString()));


                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG", "Done pressed");
                }
                return false;
            }
        });

        SEFA.storeTeacher(editText.getText().toString());
    }

    public void getAuditorium() {
        EditText editText = findViewById(R.id.scheduleAuditoriumET);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SEFA.storeAuditorium((editText.getText().toString()));


                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG", "Done pressed");
                }
                return false;
            }
        });

        SEFA.storeAuditorium(editText.getText().toString());
    }

    public void getParity() {
        CheckBox checkBox = findViewById(R.id.scheduleParityCheckBox);
        SEFA.storeParity(checkBox.isChecked());
    }


    public static class ScheduleEntryFieldsAccumulator{
        private String subject = null;
        private String teacher = null;
        private String auditorium = null;
        private String weekDay = null;
        private boolean isOnEvenWeeks = false;
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

        public void storeParity(boolean isOnEvenWeeks) {
            this.isOnEvenWeeks = isOnEvenWeeks;
        }

        public boolean isValidSE() {
            return subject != null && teacher != null && auditorium != null
                   && weekDay != null && isSetTime;
        }

        public void addScheduleEntry() {
            Controller.ScheduleController.addScheduleEntry(subject,
                    WeekDayEnum.valueOf(weekDay).ordinal(), hour, minute,
                    isOnEvenWeeks, auditorium, teacher);
        }

        public void clear() {
            subject = null;
            teacher = null;
            auditorium = null;
            weekDay = null;
            isOnEvenWeeks = false;
            hour = 0;
            minute = 0;
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
