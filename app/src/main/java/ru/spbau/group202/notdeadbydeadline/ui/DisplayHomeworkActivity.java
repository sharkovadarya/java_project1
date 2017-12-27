package ru.spbau.group202.notdeadbydeadline.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;


public class DisplayHomeworkActivity extends AppCompatActivity {


    private void outputHomeworks(String subject) {
        List<List<String>> formattedHomeworksDetails =
                Controller.HomeworkController.getHomeworksBySubject(subject);

        ListView homeworksListView = findViewById(R.id.homeworksListView);
        DetailedEntriesListViewAdapter adapter1 = new DetailedEntriesListViewAdapter(this, formattedHomeworksDetails);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String subject = getIntent().getStringExtra("SUBJECT_NAME");
        setTitle(subject);

        outputHomeworks(subject);
    }

}
