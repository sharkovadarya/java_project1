package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractDatePicker;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractTimePicker;

public class AddHomeworkActivity extends AppCompatActivity {

    private static final String TAG = "AddHomeworkActivity";
    private static final int REQUEST_CHOOSER = 1234;
    public static final HomeworkFieldsAccumulator HFA = new HomeworkFieldsAccumulator();
    private static boolean isSetTime = false;
    private static boolean isSetDate = false;
    private static boolean isSetSubject = false;
    private static boolean isSetDescription = false;
    private static boolean isSetExpectedScore = false;
    private static boolean isSetHowToSend = false;
    private static boolean isSetRegularity = false;

    private static Bundle homeworkEntry;

    private int id = -1;

    public void processSubject() {

        final List<String> source = Controller.getSubjectList();

        final AutoCompleteTextView actv = findViewById(R.id.getSubjectACTV);
        actv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, source));


        HFA.storeSubject(actv.getText().toString());

        if (id != -1 && !isSetSubject) {
            actv.setText(homeworkEntry.getString("subject"));
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
            editText.setText(homeworkEntry.getString("description"));
        }
    }

    public void processExpectedScore() {
        final EditText editText = findViewById(R.id.expectedScore);

        if (id != -1 && !isSetExpectedScore) {
            double score = homeworkEntry.getDouble("expectedScore");
            if (score != -1) {
                editText.setText(String.format(Locale.getDefault(),
                        "%s", homeworkEntry.getDouble("expectedScore")));
            } else {
                editText.setText(getResources().getString(R.string.not_specified));
            }
        }

        String expectedScore = editText.getText().toString();
        if (isParseableDouble(expectedScore)) {
            HFA.storeExpectedScore(Double.parseDouble(expectedScore));
        }
    }

    public void getExpectedScore() {
        final EditText editText = findViewById(R.id.expectedScore);

        String expectedScore = editText.getText().toString();
        if (isParseableDouble(expectedScore)) {
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
            editText.setText(homeworkEntry.getString("howToSend"));
        }
    }

    public void getHowToSend() {
        final EditText editText = findViewById(R.id.submitWayEditText);

        HFA.storeHowToSend(editText.getText().toString());
    }

    public void setTime(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        if (id != -1 && !isSetTime) {
            timePickerFragment.setValues(homeworkEntry.getInt("hour"),
                                        homeworkEntry.getInt("minute"));
        }

        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        if (id != -1 && !isSetDate) {
            datePickerFragment.setValues(homeworkEntry.getInt("year"),
                                         homeworkEntry.getInt("month") - 1,
                                         homeworkEntry.getInt("day"));
        }

        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void processRegularity() {
        EditText editText = findViewById(R.id.setRegularityEditText);

        if (id != -1 && !isSetRegularity) {
            editText.setText(String.format(Locale.getDefault(),
                    "%d", homeworkEntry.getInt("regularity")));
        }

        String regularity = editText.getText().toString();
        if (isParseableInteger(regularity)) {
            HFA.storeRegularity(Integer.parseInt(regularity));
        }
    }

    public void getRegularity() {
        EditText editText = findViewById(R.id.setRegularityEditText);
        String regularity = editText.getText().toString();
        if (regularity.isEmpty()) {
            HFA.storeRegularity(0);
        } else if (isParseableInteger(regularity)) {
            HFA.storeRegularity(Integer.parseInt(regularity));
        }
    }

    public void setFiles(View view) {
        Intent getContentIntent = FileUtils.createGetContentIntent();

        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    final Uri uri = data.getData();
                    String path;

                    // Get the File path from the Uri
                    path = FileUtils.getPath(this, uri);

                    /*// Alternatively, use FileUtils.getFile(Context, Uri)
                    if (path != null && FileUtils.isLocal(path)) {
                        File file = new File(path);
                        path = file.getAbsolutePath();
                    }*/

                    HFA.storeMaterials(path);
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        HFA.clear();

        id = getIntent().getIntExtra("id", -1);

        if (id != -1) {
            TextView header = findViewById(R.id.addNewHWHeader);
            header.setText(getResources().getString(R.string.edit_hw_entry));
            homeworkEntry = Controller.HomeworkController.getHomeworkById(id);

            ArrayList<String> files = homeworkEntry.getStringArrayList("materials");
            if (files != null) {
                for (String file : files) {
                    HFA.storeMaterials(file);
                }
            }

        }

        processSubject();
        getDescription();
        processExpectedScore();
        processHowToSend();
        processRegularity();

        Button addHomeworkButton = findViewById(R.id.finishButton);
        addHomeworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDescription();
                processSubject();
                getSubject();
                getExpectedScore();
                getHowToSend();
                getRegularity();

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

    private boolean isParseableInteger(String string) {
        try {
            Integer.parseInt(string);
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
        private double expectedScore = Double.MIN_VALUE;
        private int regularity = 0;
        private String howToSend;
        private ArrayList<String> materials = new ArrayList<>();
        private ArrayList<Uri> files = new ArrayList<>();

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
            if (this.expectedScore != expectedScore) {
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

        public void storeRegularity(int regularity) {
            if (this.regularity != regularity) {
                isSetRegularity = true;
            }
            this.regularity = regularity;
        }

        public void storeHowToSend(String howToSend) {
            if (this.howToSend != null && !this.howToSend.equals(howToSend)) {
                isSetHowToSend = true;
            }
            this.howToSend = howToSend == null ? "" : howToSend;
        }

        public void storeMaterials(String filepath) {
            materials.add(filepath);
        }

        public void storeFiles(Uri uri) {
            files.add(uri);
        }

        public void addNewHomework() {

            if (description == null) {
                description = " ";
            }

            if (howToSend == null) {
                howToSend = " ";
            }

            Controller.HomeworkController.addHomework(new LocalDateTime(year, month, day, hour, minutes),
                    subject, regularity, description,
                    howToSend, expectedScore, materials);
        }

        public void editHomework( int id ) {
            if (description == null) {
                description = " ";
            }

            if (howToSend == null) {
                howToSend = " ";
            }

            if (!isSetDate) {
                year = homeworkEntry.getInt("year");
                month = homeworkEntry.getInt("month");
                day = homeworkEntry.getInt("day");
            }

            if (!isSetTime) {
                hour = homeworkEntry.getInt("hour");
                minutes = homeworkEntry.getInt("minute");
            }

            Controller.HomeworkController.editHomeworkById(id, new LocalDateTime(year, month, day, hour, minutes),
                    subject, regularity, description,
                    howToSend, expectedScore, materials);

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
            expectedScore = Double.MIN_VALUE;
            year = 0;
            month= 0;
            day = 0;
            hour = 0;
            minutes = 0;
            regularity = 0;
            materials.clear();
            files.clear();

            isSetTime = false;
            isSetDate = false;
            isSetExpectedScore = false;
            isSetSubject = false;
            isSetDescription = false;
            isSetHowToSend = false;
            isSetRegularity = false;
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
