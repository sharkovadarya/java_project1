package ru.spbau.group202.notdeadbydeadline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    private void outputCurrentDate() {
        LocalDateTime currentDate = LocalDateTime.now();

        String stringDate = Integer.toString(currentDate.getDayOfMonth()) +
                            "\n" + currentDate.getMonth();

        SpannableString date = new SpannableString(stringDate);
        date.setSpan(new RelativeSizeSpan(2f), 0, 2, 0);


        TextView tv = findViewById(R.id.currentDate);
        tv.setText(date);
        tv.setFocusable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputCurrentDate();
    }
}
