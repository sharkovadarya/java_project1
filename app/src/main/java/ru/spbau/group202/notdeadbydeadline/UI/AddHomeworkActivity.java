package ru.spbau.group202.notdeadbydeadline.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;

public class AddHomeworkActivity extends AppCompatActivity {

    public static HomeworkFieldsAccumulator hfa = new HomeworkFieldsAccumulator();
    private static boolean isSetTime = false;
    private static boolean isSetDate = false;

    public void getSubject() {

        //final ArrayList<String> source = Controller.getSubjectList();
        // TODO fix this
        final ArrayList<String> source = new ArrayList<>();

        final AutoCompleteTextView actv = findViewById(R.id.getSubjectACTV);
        actv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, source));

        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!source.contains(actv.getText().toString())) {
                    source.add(actv.getText().toString());
                }
                hfa.storeSubject((actv.getText().toString()));


                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG", "Done pressed");
                }
                return false;
            }
        });

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hfa.storeSubject((actv.getText().toString()));
                if (!source.contains(actv.getText().toString())) {
                    source.add(actv.getText().toString());
                }

                if (!source.contains(actv.getText().toString())) {
                    source.add(actv.getText().toString());
                }

                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view1.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    public void getDescription() {
        final EditText editText = findViewById(R.id.getDescriptionEditText);

        /*editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hfa.storeDescription(editText.getText().toString());
                return false;
            }
        });*/
        hfa.storeDescription(editText.getText().toString());
    }

    public void getExpectedScore() {
        final EditText editText = findViewById(R.id.expectedScore);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hfa.storeExpectedScore(
                        Double.parseDouble(editText.getText().toString()));
                return false;
            }
        });
    }

    public void getHowToSend() {
        final EditText editText = findViewById(R.id.submitWayEditText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG", "Done pressed");
                }
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hfa.storeHowToSend(editText.getText().toString());


                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view1.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    public void setTime(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        //DialogFragment newFragment = new TimePickerFragment();

        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /*public void getRegularity() {
        CheckBox checkBox = findViewById(R.id.isRegularCheckBox);

    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add h/w entry");
        }

        getSubject();
        getDescription();
        getExpectedScore();
        getHowToSend();

        Button addHomeworkButton = findViewById(R.id.finishButton);
        addHomeworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDescription();

                if (!hfa.isValidHomework()) {
                    Toast.makeText(getApplicationContext(),
                            "Fill 'subject' and input correct date", Toast.LENGTH_LONG).show();
                } else {
                    hfa.addNewHomework();
                    hfa.clear();
                    finish();
                }
            }
        });
    }

    public static class HomeworkFieldsAccumulator {
        private String subject;
        private String description;
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minutes;
        private double expectedScore;
        private boolean isRegular;
        private String howToSend;

        public void storeSubject(String subject) {
            this.subject = subject == null ? "" : subject;
        }

        public void storeDescription(String description) {
            this.description = description == null ? "" : description;
        }

        public void storeExpectedScore(double expectedScore) {
            this.expectedScore = expectedScore;
        }

        public void storeDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public void storeTime(int hour, int minutes) {
            this.hour = hour;
            this.minutes = minutes;
        }

    /*public void storeRegularity(boolean isRegular) {
        this.isRegular = isRegular;
    }*/

        public void storeHowToSend(String howToSend) {
            this.howToSend = howToSend == null ? "" : howToSend;
        }

        public void addNewHomework() {

            if (description == null) {
                description = " ";
            }

            if (howToSend == null) {
                howToSend = " ";
            }

            Controller.HomeworkController.addHomework(year, month, day, hour, minutes,
                    subject, false, description,
                    howToSend, expectedScore);
        }

        public boolean isValidHomework() {
            return subject != null && isSetDate && isSetTime;
        }

        public void clear() {
            subject = null;
            description = null;
            howToSend = null;
            expectedScore = 0;
            year = 0;
            month= 0;
            day = 0;
            hour = 0;
            minutes = 0;
            isSetTime = false;
            isSetDate = false;
        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            isSetTime = true;
            hfa.storeTime(hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            isSetDate = true;
            hfa.storeDate(year, month + 1, day);
        }
    }


}
