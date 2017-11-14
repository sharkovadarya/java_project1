package ru.spbau.group202.notdeadbydeadline.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.R;

public class HomeworkActivity extends AppCompatActivity {


    ArrayList<String> outputValues = new ArrayList<>();
    private void putStringToOutputValues(String outputValue) {
        outputValues.add(outputValue);
    }

    public void outputHomeworks() {

        Homeworks homeworks = Controller.getHomeworks();

        homeworks.iterateWithConsumer(new Consumer<Homework>() {
            @Override
            public void accept(Homework homework) {
                StringBuilder outputValue = new StringBuilder(homework.getSubject());

                outputValue.append("\n");
                outputValue.append(homework.getDescription());
                outputValue.append("\n");
                outputValue.append(homework.getFormattedDeadline());
                outputValue.append("\n");
                outputValue.append(homework.getHowToSend());
                outputValue.append("\n Expected score: ");
                outputValue.append(homework.getExpectedScore());

                putStringToOutputValues(outputValue.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddHomeworkActivity.class);
                startActivity(intent);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Homework");
        }
    }

}
