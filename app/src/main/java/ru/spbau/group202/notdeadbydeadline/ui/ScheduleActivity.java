package ru.spbau.group202.notdeadbydeadline.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.ListViewUtility;

public class ScheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ScheduleActivity";

    private LocalDate localDate;

    private void outputScheduleByDay(int dayNumber) {
        List<List<String>> scheduleDetails = Controller.ScheduleController.getScheduleByDayOfWeek(dayNumber, localDate.getWeekOfWeekyear() % 2 != 0);

        ArrayList<SpannableStringBuilder> formattedSchedule = new ArrayList<>();
        for (int i = 0; i < scheduleDetails.size(); i++) {
            List<String> schDetails = scheduleDetails.get(i);
            SpannableStringBuilder stringBuilder =
                    new SpannableStringBuilder(Integer.toString(i + 1));

            int position = stringBuilder.length();
            stringBuilder.append("  ");
            stringBuilder.append(schDetails.get(0));
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                    position, stringBuilder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append(", ");
            stringBuilder.append(schDetails.get(1));
            stringBuilder.append("\n");
            for (int j = 0; j < Integer.toString(i + 1).length() + 1; j++) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(schDetails.get(2));
            stringBuilder.append(", ");
            stringBuilder.append(schDetails.get(3));

            formattedSchedule.add(stringBuilder);
        }

        ListView lv;
        switch (dayNumber) {
            case DateTimeConstants.MONDAY:
                lv = findViewById(R.id.scheduleMondayList);
                break;
            case DateTimeConstants.TUESDAY:
                lv = findViewById(R.id.scheduleTuesdayList);
                break;
            case DateTimeConstants.WEDNESDAY:
                lv = findViewById(R.id.scheduleWednesdayList);
                break;
            case DateTimeConstants.THURSDAY:
                lv = findViewById(R.id.scheduleThursdayList);
                break;
            case DateTimeConstants.FRIDAY:
                lv = findViewById(R.id.scheduleFridayList);
                break;
            case DateTimeConstants.SATURDAY:
                lv = findViewById(R.id.scheduleSaturdayList);
                break;
            default:
                Log.e(TAG, "Wrong dayNumber parameter in OutputScheduleByDay");
                return;
        }

        ArrayAdapter<SpannableStringBuilder> adapter = new ArrayAdapter<>(this,
                R.layout.custom_deadline_listview_entry,
                formattedSchedule);
        lv.setAdapter(adapter);
    }

    private void setHeaders() {

        String monday = getResources().getString(R.string.monday, "");
        String tuesday = getResources().getString(R.string.tuesday, "");
        String wednesday = getResources().getString(R.string.wednesday, "");
        String thursday = getResources().getString(R.string.thursday, "");
        String friday = getResources().getString(R.string.friday, "");
        String saturday = getResources().getString(R.string.saturday, "");

        TextView tv = findViewById(R.id.scheduleMondayHeader);
        tv.setText(monday);
        tv = findViewById(R.id.scheduleTuesdayHeader);
        tv.setText(tuesday);
        tv = findViewById(R.id.scheduleWednesdayHeader);
        tv.setText(wednesday);
        tv = findViewById(R.id.scheduleThursdayHeader);
        tv.setText(thursday);
        tv = findViewById(R.id.scheduleFridayHeader);
        tv.setText(friday);
        tv = findViewById(R.id.scheduleSaturdayHeader);
        tv.setText(saturday);
    }

    private void setListViewsHeightAllDays() {
        ListView monday = findViewById(R.id.scheduleMondayList);
        ListView tuesday = findViewById(R.id.scheduleTuesdayList);
        ListView wednesday = findViewById(R.id.scheduleWednesdayList);
        ListView thursday = findViewById(R.id.scheduleThursdayList);
        ListView friday = findViewById(R.id.scheduleFridayList);
        ListView saturday = findViewById(R.id.scheduleSaturdayList);

        ListViewUtility.setTwoListViewsHeight(monday, thursday);
        ListViewUtility.setTwoListViewsHeight(tuesday, friday);
        ListViewUtility.setTwoListViewsHeight(wednesday, saturday);
    }

    private void outputSchedule() {
        final TableLayout table = findViewById(R.id.scheduleTableLayout);
        table.setColumnShrinkable(0, true);
        table.setColumnShrinkable(1, true);

        for (int i = 1; i <= 6; ++i)
            outputScheduleByDay(i);

        setListViewsHeightAllDays();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.addScheduleEntryButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddScheduleEntryActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        localDate = (LocalDate) getIntent().getSerializableExtra("date");

        setTitle("Schedule");
        setHeaders();
        outputSchedule();

        Button nextWeekButton = findViewById(R.id.scheduleNextWeekButton);
        nextWeekButton.setText(R.string.next_week);
        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                localDate = localDate.plusDays(7);
                setHeaders();
                outputSchedule();
            }
        });

        Button button = findViewById(R.id.schedulePreviousWeekButton);
        button.setText(getResources().getString(R.string.previous_week));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                localDate = localDate.minusDays(7);
                setHeaders();
                outputSchedule();
            }
        });
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
        getMenuInflater().inflate(R.menu.schedule, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
