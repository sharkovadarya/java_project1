package ru.spbau.group202.notdeadbydeadline.UI;

import android.support.v4.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.TextView;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;

public class AddHomeworkActivity extends AppCompatActivity {

    private Controller controller;

    public void getSubject() {

        // TODO fetch subjects array from a list of subjects which will be fetched from Schedule
        // TODO you aren't allowed to touch Schedule here. You will get a list from Controller.
        String[] source = new String[]{"Algebra", "Discrete mathematics",
                "Algorithms", "Functional programming",
                "Calculus"};

        final AutoCompleteTextView actv = findViewById(R.id.getSubjectACTV);
        actv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, source));

        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
                controller.addHomeworkManager.storeSubject((actv.getText().toString()));

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

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                controller.addHomeworkManager.storeDescription(editText.getText().toString());
                return false; // TODO because we didn't consume any action?
            }
        });
    }

    public void getExpectedScore() {
        final EditText editText = findViewById(R.id.expectedScore);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                controller.addHomeworkManager.storeExpectedSCore(
                        Integer.parseInt(editText.getText().toString()));
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
                controller.addHomeworkManager.storeHowToSend(editText.getText().toString());

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
        timePickerFragment.setController(controller);
        //DialogFragment newFragment = new TimePickerFragment();

        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setController(controller);

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

        controller = new Controller(this);

        getSubject();
        getDescription();
        getExpectedScore();
        getHowToSend();

        Button addHomeworkButton = findViewById(R.id.finishButton);
        addHomeworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.addHomeworkManager.addNewHomework();
                finish();
            }
        });
    }


}
