package ru.spbau.group202.notdeadbydeadline.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;

public class DisplayHomeworkActivity extends AppCompatActivity {


    public void outputHomeworks(String subject) {
        ArrayList<String> formattedHomeworks =
                Controller.getFormattedHomeworksBySubject(subject);

        ListView homeworksListView = findViewById(R.id.homeworksListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, formattedHomeworks);
        homeworksListView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String subject = getIntent().getStringExtra("SUBJECT_NAME");
        TextView subjectNameHeader = findViewById(R.id.displayHomeworksHeader);
        subjectNameHeader.setText(subject);

        outputHomeworks(subject);
    }

}
