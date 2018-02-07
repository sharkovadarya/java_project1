package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractDatePicker;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractTimePicker;

public class AddHomeworkActivity extends AppCompatActivity {

    private static final String TAG = "AddHomeworkActivity";
    public static final HomeworkFieldsAccumulator HFA = new HomeworkFieldsAccumulator();
    private static boolean isSetTime = false;
    private static boolean isSetDate = false;
    private static boolean isSetSubject = false;
    private static boolean isSetDescription = false;
    private static boolean isSetExpectedScore = false;
    private static boolean isSetHowToSend = false;

    private static List<String> homeworkEntry = new ArrayList<>();

    private int id = -1;

    public void processSubject() {

        final List<String> source = Controller.getSubjectList();

        final AutoCompleteTextView actv = findViewById(R.id.getSubjectACTV);
        actv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, source));


        HFA.storeSubject(actv.getText().toString());

        if (id != -1 && !isSetSubject) {
            actv.setText(homeworkEntry.get(0));
        }

        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!source.contains(actv.getText().toString())) {
                    source.add(actv.getText().toString());
                }
                HFA.storeSubject((actv.getText().toString()));


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
                //HFA.storeSubject((actv.getText().toString()));
                if (!source.contains(actv.getText().toString())) {
                    source.add(actv.getText().toString());
                }

                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);

                    if (inputManager != null) {
                        inputManager.hideSoftInputFromWindow(view1.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                }
            }
        });
    }

    public void getSubject() {
        final AutoCompleteTextView actv = findViewById(R.id.getSubjectACTV);

        HFA.storeSubject(actv.getText().toString());
    }

    public void getDescription() {
        final EditText editText = findViewById(R.id.getDescriptionEditText);

        HFA.storeDescription(editText.getText().toString());

        if (id != -1 && !isSetDescription) {
            editText.setText(homeworkEntry.get(1));
        }
    }

    public void getExpectedScore() {
        final EditText editText = findViewById(R.id.expectedScore);

        if (id != -1 && !isSetExpectedScore) {
            editText.setText(homeworkEntry.get(4));
        }

        String expectedScore = editText.getText().toString();
        if (!expectedScore.equals("") && isParseableDouble(expectedScore)) {
            HFA.storeExpectedScore(Double.parseDouble(expectedScore));
        } else {
            HFA.storeExpectedScore(-1.0);
        }
    }

    public void processHowToSend() {
        final EditText editText = findViewById(R.id.submitWayEditText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.d(TAG, "Done pressed");
                }
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                HFA.storeHowToSend(editText.getText().toString());


                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.hideSoftInputFromWindow(view1.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });


        if (id != -1 && !isSetHowToSend) {
            editText.setText(homeworkEntry.get(3));
        }
    }

    public void getHowToSend() {
        final EditText editText = findViewById(R.id.submitWayEditText);

        HFA.storeHowToSend(editText.getText().toString());
    }

    public void setTime(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        if (id != -1 && !isSetTime) {
            String[] date = homeworkEntry.get(2).split("[\\s:.]+");
            timePickerFragment.setValues(Integer.parseInt(date[3]),
                                        Integer.parseInt(date[4]));
        }

        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        if (id != -1 && !isSetDate) {
            String[] date = homeworkEntry.get(2).split("[\\s:.]+");
            datePickerFragment.setValues(Integer.parseInt(date[2]),
                                         Integer.parseInt(date[1]) - 1,
                                         Integer.parseInt(date[0]));
        }

        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /*public void getRegularity() {
        CheckBox checkBox = findViewById(R.id.isRegularCheckBox);

    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        id = getIntent().getIntExtra("id", -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (id != -1) {
            TextView header = findViewById(R.id.addNewHWHeader);
            header.setText(getResources().getString(R.string.edit_hw_entry));
            homeworkEntry = Controller.HomeworkController.getHomeworkById(id);
        }

        processSubject();
        getDescription();
        getExpectedScore();
        processHowToSend();

        Button addHomeworkButton = findViewById(R.id.finishButton);
        addHomeworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDescription();
                processSubject();
                getSubject();
                getExpectedScore();
                getHowToSend();

                if (id == -1 && !HFA.isValidForAdding()) {
                    Toast.makeText(getApplicationContext(),
                            "Fill 'subject' and input correct date",
                            Toast.LENGTH_LONG).show();
                } else if (id != -1 && !HFA.isValidForEditing()) {
                    Toast.makeText(getApplicationContext(),
                            "Fill 'subject'",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (id == -1) {
                        HFA.addNewHomework();
                    } else {
                        HFA.editHomework(id);
                    }
                    HFA.clear();
                    finish();
                }
            }
        });
    }

    private boolean isParseableDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
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
            if (this.subject != null && !this.subject.equals(subject)) {
                isSetSubject = true;
            }
            this.subject = subject == null ? "" : subject;
        }

        public void storeDescription(String description) {
            if (this.description != null && !this.description.equals(description)) {
                isSetDescription = true;
            }
            this.description = description == null ? "" : description;
        }

        public void storeExpectedScore(double expectedScore) {
            if (this.expectedScore != expectedScore) { //expectedScore != -1.0 &&
                isSetExpectedScore = true;
            }
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
            if (this.howToSend != null && !this.howToSend.equals(howToSend)) {
                isSetHowToSend = true;
            }
            this.howToSend = howToSend == null ? "" : howToSend;
        }

        public void addNewHomework() {

            if (description == null) {
                description = " ";
            }

            if (howToSend == null) {
                howToSend = " ";
            }

            // TODO add lists
            Controller.HomeworkController.addHomework(new LocalDateTime(year, month, day, hour, minutes),
                    subject, 0, description,
                    howToSend, expectedScore, new ArrayList<>());
        }

        public void editHomework( int id ) {
            if (description == null) {
                description = " ";
            }

            if (howToSend == null) {
                howToSend = " ";
            }

            if (!isSetDate) {
                String[] date = AddHomeworkActivity.homeworkEntry.get(2).split("[\\s:.]+");
                year = Integer.parseInt(date[2]);
                month = Integer.parseInt(date[1]);
                day = Integer.parseInt(date[0]);
            }

            if (!isSetTime) {
                String[] date = AddHomeworkActivity.homeworkEntry.get(2).split("[\\s:.]+");
                hour = Integer.parseInt(date[3]);
                minutes = Integer.parseInt(date[4]);
            }

            Controller.HomeworkController.editHomeworkById(id, new LocalDateTime(year, month, day, hour, minutes),
                    subject, 0, description,
                    howToSend, expectedScore, new ArrayList<>());

            }

        public boolean isValidForAdding() {
            return subject != null && isSetDate && isSetTime;
        }

        public boolean isValidForEditing() {
            return subject.trim().length() > 0;
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
            isSetExpectedScore = false;
            isSetSubject = false;
            isSetDescription = false;
            isSetHowToSend = false;
        }

    }

    public static class TimePickerFragment extends AbstractTimePicker {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            isSetTime = true;
            HFA.storeTime(hourOfDay, minute);
        }

    }

    public static class DatePickerFragment extends AbstractDatePicker {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            isSetDate = true;
            HFA.storeDate(year, month + 1, day);
        }
    }


}
