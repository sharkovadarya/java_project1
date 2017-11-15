package ru.spbau.group202.notdeadbydeadline.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

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

    // TODO that's not how it's supposed to be
    ArrayList<String> subjects = new ArrayList<>();

    private void debuggingThrashMethod() {
        subjects.add("Java");
        subjects.add("Algebra");
        subjects.add("Algorithms");
        subjects.add("Functional Programming");
        subjects.add("Discrete Mathematics");
    }

    public void displaySubjects() {
        debuggingThrashMethod();

        ListView lv = (ListView) findViewById(R.id.subjectListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.custom_textview_for_bigger_listview, subjects);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    /*public void addButtons() {

        // TODO Literally not how it's supposed to be
        debuggingThrashMethod();

        LinearLayout linearLayout = findViewById(R.id.homeworkActivityLinearLayout);
        // TODO do I need to? It's already set in XML
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < subjects.size(); ++i) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            //layoutParams.setMargins(30, 20 + i);

            Button button = new Button(this);
            //Button button = new Button(this, null, R.style.Widget_AppCompat_Button_Colored);

            String subject = subjects.get(i);
            button.setText(subject);
            button.setId(i);
            row.addView(button);

            linearLayout.addView(row);
        }



    }*/

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

        displaySubjects();
    }

}
