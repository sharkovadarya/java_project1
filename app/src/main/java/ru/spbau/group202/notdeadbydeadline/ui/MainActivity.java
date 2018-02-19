package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.AbstractDatePicker;
import ru.spbau.group202.notdeadbydeadline.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // used for Google Calendar connection
    private static LocalDate endTermDate;

    private void outputCurrentDate() {
        LocalDateTime currentDate = LocalDateTime.now();

        StringBuilder dateStringBuilder =
                new StringBuilder(Integer.toString(currentDate.getDayOfMonth()));
        dateStringBuilder.append("\n");
        dateStringBuilder.append(currentDate.monthOfYear().getAsText());
        dateStringBuilder.append("\n");
        int pos = dateStringBuilder.length();
        dateStringBuilder.append(currentDate.dayOfWeek().getAsText());
        String dateString = dateStringBuilder.toString();

        SpannableString date = new SpannableString(dateString);
        date.setSpan(new RelativeSizeSpan(3f), 0, 2, 0);
        date.setSpan(new StyleSpan(Typeface.ITALIC), pos, dateString.length(), 0);

        TextView tv = findViewById(R.id.currentDate);
        tv.setText(date);
        tv.setFocusable(false);
    }

    private void outputDeadlines() {
        Controller.getInstance(this).homeworkController().generateHomeworks();
        List<List<String>> deadlinesDetails = Controller.getInstance(this).homeworkController()
                           .getDeadlinesByDay(LocalDate.now());


        LocalDateTime ldt = LocalDateTime.now().plusDays(1);
        deadlinesDetails.addAll(Controller.getInstance(this).homeworkController()
                            .getDeadlinesByDay(ldt.toLocalDate()));


        ArrayList<SpannableStringBuilder> formattedDeadlines = new ArrayList<>();
        for (List<String> deadlineDetails : deadlinesDetails) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(
                    deadlineDetails.get(2));
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
            LocalDateTime deadline = formatter.parseLocalDateTime(deadlineDetails.get(2));
            LocalDate deadlineDate = new LocalDate(deadline);

            if (deadlineDate.compareTo(LocalDate.now()) <= 0
                    && deadline.compareTo(LocalDateTime.now()) < 0) {
                stringBuilder.setSpan(new ForegroundColorSpan(android.graphics.Color.rgb(172, 7, 7)),
                        0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            int position = stringBuilder.length();
            stringBuilder.append(" ");
            stringBuilder.append(deadlineDetails.get(0));
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                    position, stringBuilder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (!deadlineDetails.get(1).isEmpty()) {
                stringBuilder.append("\n");
                stringBuilder.append(deadlineDetails.get(1));
            }

            formattedDeadlines.add(stringBuilder);
        }

        ListView lv = findViewById(R.id.deadlinesList2);
        ArrayAdapter<SpannableStringBuilder> adapter = new ArrayAdapter<>(this,
                R.layout.custom_mainscreen_listview_entry,
                formattedDeadlines);
        lv.setAdapter(adapter);
    }

    private void outputTodaySchedule() {
        LocalDate localDate = LocalDate.now();
        List<List<String>> scheduleDetails = Controller.getInstance(this).scheduleController()
                .getScheduleByDay(localDate);
        /*List<List<String>> scheduleDetails = Controller.ScheduleController.getScheduleByDayOfWeek(localDate.getDayOfWeek() - 1,
                WeekParityEnum.values()[localDate.getWeekOfWeekyear() % 2]);*/

        ArrayList<SpannableStringBuilder> formattedSchedule = new ArrayList<>();
        for (int i = 0; i < scheduleDetails.size(); i++) {
            List<String> schDetails = scheduleDetails.get(i);
            SpannableStringBuilder stringBuilder =
                    new SpannableStringBuilder(schDetails.get(1));

            stringBuilder.append("  ");
            int position = stringBuilder.length();
            stringBuilder.append(schDetails.get(0));
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                    position, stringBuilder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (schDetails.get(2).trim().length() > 0
                || schDetails.get(3).trim().length() > 0) {
                stringBuilder.append(", \n");
                if (schDetails.get(2).trim().length() > 0) {
                    stringBuilder.append(schDetails.get(2));
                    stringBuilder.append(", ");
                }
                stringBuilder.append(schDetails.get(3));
            }

            formattedSchedule.add(stringBuilder);
        }

        ListView lv = findViewById(R.id.scheduleListViewMainScreen);
        ArrayAdapter<SpannableStringBuilder> adapter = new ArrayAdapter<>(this,
                R.layout.custom_mainscreen_listview_entry,
                formattedSchedule);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        JodaTimeAndroid.init(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        outputCurrentDate();
        outputDeadlines();
        outputTodaySchedule();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
            return true;

        //return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_deadlines) {
            Intent intent = new Intent(this, DeadlinesActivity.class);
            intent.putExtra("date", new LocalDate());
            startActivityForResult(intent, 1);
        } else if (id == R.id.nav_homework) {
            Intent intent = new Intent(this, HomeworkActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("date", new LocalDate());
            startActivityForResult(intent, 1);
        } /*else if (id == R.id.nav_studymaterials) {
            Intent intent = new Intent(this, StudyMaterialsActivity.class);
            startActivityForResult(intent, 1);
        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        outputDeadlines();
        outputTodaySchedule();
    }
}
