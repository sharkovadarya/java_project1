package ru.spbau.group202.notdeadbydeadline.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;

import static android.graphics.Typeface.BOLD;



public class DisplayHomeworkActivity extends AppCompatActivity {


    public void outputHomeworks(String subject) {
        ArrayList<ArrayList<String>> formattedHomeworksDetails =
                Controller.getHomeworksBySubject(subject);
        // local utility variables
        String deadlinesField = "\nDeadlines: ";
        String descriptionFiled = "\nDescription: ";
        String submitField = "\nSubmit: ";
        String expectedScoreField = "\nExpected Score: ";

        ArrayList<SpannableStringBuilder> formattedHomeworks = new ArrayList<>();
        for (ArrayList<String> homeworkDetails : formattedHomeworksDetails) {

            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(descriptionFiled);
            stringBuilder.setSpan(new StyleSpan(BOLD),
                    0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(homeworkDetails.get(0));
            stringBuilder.append(deadlinesField);
            stringBuilder.setSpan(new StyleSpan(BOLD),
                    stringBuilder.length() - deadlinesField.length(),
                    stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(homeworkDetails.get(1));
            stringBuilder.append(submitField);
            stringBuilder.setSpan(new StyleSpan(BOLD),
                    stringBuilder.length() - submitField.length(),
                    stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(homeworkDetails.get(2));
            stringBuilder.append(expectedScoreField);
            stringBuilder.setSpan(new StyleSpan(BOLD),
                    stringBuilder.length() - expectedScoreField.length(),
                    stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(homeworkDetails.get(3));

            formattedHomeworks.add(stringBuilder);
        }

        ListView homeworksListView = findViewById(R.id.homeworksListView);
        DetailedEntriesListViewAdapter adapter1 = new DetailedEntriesListViewAdapter(this, formattedHomeworksDetails);
        /*ArrayAdapter<SpannableStringBuilder> adapter = new ArrayAdapter<>(this,
                R.layout.custom_homework_listview_entry,
                formattedHomeworks);*/
        //homeworksListView.setAdapter(adapter);
        homeworksListView.setAdapter(adapter1);
    }

    private void processOnClickHomeworks() {
        ListView homeworksListView = findViewById(R.id.homeworksListView);
        homeworksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String subject = getIntent().getStringExtra("SUBJECT_NAME");
        setTitle(subject);

        outputHomeworks(subject);
    }

}
