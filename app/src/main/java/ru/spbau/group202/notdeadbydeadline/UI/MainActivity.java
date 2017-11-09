package ru.spbau.group202.notdeadbydeadline.UI;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Model.Deadline;
import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.R;

public class MainActivity extends AppCompatActivity {

    Homeworks homeworks = new Homeworks();

    private void outputCurrentDate() {
        LocalDateTime currentDate = LocalDateTime.now();

        StringBuilder dateStringBuilder =
                new StringBuilder(Integer.toString(currentDate.getDayOfMonth()));
        dateStringBuilder.append("\n");
        dateStringBuilder.append(currentDate.getMonth());
        dateStringBuilder.append("\n");
        int pos = dateStringBuilder.length();
        dateStringBuilder.append(currentDate.getDayOfWeek());
        String dateString = dateStringBuilder.toString();

        SpannableString date = new SpannableString(dateString);
        date.setSpan(new RelativeSizeSpan(3f), 0, 2, 0);
        date.setSpan(new StyleSpan(Typeface.ITALIC), pos, dateString.length(), 0);

        TextView tv = findViewById(R.id.currentDate);
        tv.setText(date);
        tv.setFocusable(false);
    }

    private void outputDeadlines() {
        // TODO delete this this is for debugging
        homeworks.addHomework("2017", "11", "10",
                "14", "30",
                "Discrete mathematics");
        homeworks.addHomework("2017", "11", "11",
                "23", "59",
                "Algorithms contest");
        ArrayList<String> deadlines = homeworks.getDeadlines();

        ListView deadlinesLV = findViewById(R.id.deadlinesList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, deadlines);
        deadlinesLV.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputCurrentDate();
        outputDeadlines();
    }
}
