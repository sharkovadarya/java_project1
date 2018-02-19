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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalTime;

import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractTimePicker;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.WeekDayEnum;

public class AddScheduleEntryActivity extends AppCompatActivity {

    private static final String TAG = "AddSchEntryActivity";
    private static final ScheduleEntryFieldsAccumulator SEFA = new ScheduleEntryFieldsAccumulator();
    private static boolean isSetTime = false;
    private static boolean isSetSubject = false;
    private static boolean isSetTeacher = false;
    private static boolean isSetAuditorium = false;
    private static boolean isSetParity = false;

    private int id;
    private Bundle scheduleEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SEFA.clear();

        id = getIntent().getIntExtra("id", -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(id == -1 ?
                getResources().getString(R.string.add_schedule_entry_header)
                : getResources().getString(R.string.edit_schedule_entry));
        }

        if (id != -1) {
            scheduleEntry = Controller.getInstance(this).scheduleController().getClassEntryById(id);
            SEFA.dayOfWeek = scheduleEntry.getInt("dayOfWeek");
            LocalTime localTime = (LocalTime) scheduleEntry.getSerializable("time");
            if (localTime != null) {
                SEFA.hour = localTime.getHourOfDay();
                SEFA.minute = localTime.getMinuteOfHour();
            }
        }

        TextView header = findViewById(R.id.addNewSEHeader);
        header.setText(id == -1 ? getResources().getString(R.string
                                     .add_schedule_entry_header) :
                                  getResources().getString(R.string
                                          .edit_schedule_entry));

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

                if (id == -1 && SEFA.isValidForAddingSE()) {
                    SEFA.addScheduleEntry(AddScheduleEntryActivity.this);
                    SEFA.clear();
                    finish();
                } else if (id != -1 && SEFA.isValidForEditing()){
                    SEFA.editScheduleEntry(id, AddScheduleEntryActivity.this);
                    SEFA.clear();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Fill subject, weekday and time",
                            Toast.LENGTH_LONG).show();
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
        if (id != -1 && !isSetTime) {
            LocalTime localTime = (LocalTime) scheduleEntry.getSerializable("time");
            if (localTime != null) {
                timePickerFragment.setValues(localTime.getHourOfDay(),
                        localTime.getMinuteOfHour());
            }
        }


        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void getSubject() {
        EditText editText = findViewById(R.id.scheduleGetSubjectET);

        SEFA.storeSubject(editText.getText().toString());

        if (id != -1 && !isSetSubject) {
            editText.setText(scheduleEntry.getString("subject"));
        }

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
    }

    public void getTeacher() {
        EditText editText = findViewById(R.id.scheduleTeacherET);

        SEFA.storeTeacher(editText.getText().toString());

        if (id != -1 && !isSetTeacher) {
            editText.setText(scheduleEntry.getString("teacher"));
        }

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
    }

    public void getAuditorium() {
        EditText editText = findViewById(R.id.scheduleAuditoriumET);

        SEFA.storeAuditorium(editText.getText().toString());

        if (id != -1 && !isSetAuditorium) {
            editText.setText(scheduleEntry.getString("auditorium"));
        }

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
    }

    public void getParity() {
        String parity[] = {"even weeks", "odd weeks", "every week"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, parity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.weekParitySpinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Week Parity");
        if (id == -1) {
            spinner.setSelection(2);
        } else {
            WeekParityEnum entryParity = isSetParity ? SEFA.parity : (WeekParityEnum) scheduleEntry
                    .getSerializable("weekParity");
            if (entryParity != null) {
                switch (entryParity) {
                    case ALWAYS:
                        spinner.setSelection(2);
                        break;
                    case ON_EVEN_WEEK:
                        spinner.setSelection(0);
                        break;
                    case ON_ODD_WEEK:
                        spinner.setSelection(1);
                        break;
                }
            }

        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                switch (text) {
                    case "even weeks":
                        SEFA.storeParity(WeekParityEnum.ON_EVEN_WEEK);
                        break;
                    case "odd weeks":
                        SEFA.storeParity(WeekParityEnum.ON_ODD_WEEK);
                        break;
                    case "every week":
                        SEFA.storeParity(WeekParityEnum.ALWAYS);
                        break;
                    default:
                        Log.e(TAG,"wrong week parity type");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    public static class ScheduleEntryFieldsAccumulator{
        private String subject = null;
        private String teacher = null;
        private String auditorium = null;
        private String weekDay = null;
        private WeekParityEnum parity;
        private int hour;
        private int minute;
        private int dayOfWeek; // used for editing

        public void storeWeekDay(String weekDay) {
            this.weekDay = weekDay;
        }

        public void storeSubject(String subject) {
            if (this.subject != null && !this.subject.equals(subject)) {
                isSetSubject = true;
            }
            this.subject = subject;
        }

        public void storeAuditorium(String auditorium) {
            if (this.auditorium != null && !this.auditorium.equals(auditorium)) {
                isSetAuditorium = true;
            }
            this.auditorium = auditorium;
        }

        public void storeTeacher(String teacher) {
            if (this.teacher != null && !this.teacher.equals(teacher)) {
                isSetTeacher = true;
            }
            this.teacher = teacher;
        }

        public void storeTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public void storeParity(WeekParityEnum parity) {
            if (this.parity != null && !this.parity.equals(parity)) {
                isSetParity = true;
            }
            this.parity = parity;
        }

        public boolean isValidForAddingSE() {
            return subject != null && weekDay != null && isSetTime;
        }

        public boolean isValidForEditing() {
            return subject != null && subject.trim().length() > 0;
        }

        public void addScheduleEntry(Context context) {
            Controller.getInstance(context).scheduleController().addClassEntry(subject,
                    WeekDayEnum.valueOf(weekDay).ordinal(), hour, minute,
                    parity, auditorium, teacher);
        }

        public void editScheduleEntry(int id, Context context) {
            Controller.getInstance(context).scheduleController().editClassEntryById(id, subject,
                    weekDay == null ? dayOfWeek :
                            WeekDayEnum.valueOf(weekDay).ordinal(), hour, minute,
                    parity, auditorium, teacher);
        }

        public void clear() {
            subject = null;
            teacher = null;
            auditorium = null;
            weekDay = null;
            hour = 0;
            minute = 0;

            isSetParity = false;
            isSetTeacher = false;
            isSetAuditorium = false;
            isSetSubject = false;
            isSetTime = false;
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
