package ru.spbau.group202.notdeadbydeadline.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import org.joda.time.LocalDate;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;

public class DeadlinesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LocalDate localDate;

    // set listView2 height based on listView1 height
    private void setListViewHeightBasedOnChildren(ListView listView1,
                                                  ListView listView2) {
        ListAdapter listAdapter = listView1.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView1);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView2.getLayoutParams();
        params.height = totalHeight + (listView2.getDividerHeight() * (listAdapter.getCount() - 1));
        listView2.setLayoutParams(params);
    }

    private void outputDeadlineByDay( int dayNumber ) {

        ArrayList<ArrayList<String>> deadlinesDetails;

        LocalDate weekDay = localDate.withDayOfWeek(dayNumber);
        deadlinesDetails = Controller.getDeadlinesByDay(weekDay.getYear(),
                weekDay.getMonthOfYear(),
                weekDay.getDayOfMonth());

        ArrayList<SpannableStringBuilder> formattedDeadlines = new ArrayList<>();
        for (ArrayList<String> deadlineDetails : deadlinesDetails) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(deadlineDetails.get(2));


            int position = stringBuilder.length();
            stringBuilder.append("  ");
            stringBuilder.append(deadlineDetails.get(0));
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                    position, stringBuilder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append("\n");
            stringBuilder.append(deadlineDetails.get(1));

            formattedDeadlines.add(stringBuilder);
        }

        ListView lv;
        switch (dayNumber) {
            case DateTimeConstants.MONDAY:
                lv = findViewById(R.id.deadlinesMondayList);
                break;
            case DateTimeConstants.TUESDAY:
                lv = findViewById(R.id.deadlinesTuesdayList);
                break;
            case DateTimeConstants.WEDNESDAY:
                lv = findViewById(R.id.deadlinesWednesdayList);
                break;
            case DateTimeConstants.THURSDAY:
                lv = findViewById(R.id.deadlinesThursdayList);
                break;
            case DateTimeConstants.FRIDAY:
                lv = findViewById(R.id.deadlinesFridayList);
                break;
            case DateTimeConstants.SATURDAY:
                lv = findViewById(R.id.deadlinesSaturdayList);
                break;
            case DateTimeConstants.SUNDAY:
                lv = findViewById(R.id.deadlinesSundayList);
                break;
            default:
                throw new IllegalArgumentException("Illegal dayNumber parameter");
        }

        ArrayAdapter<SpannableStringBuilder> adapter = new ArrayAdapter<>(this,
                R.layout.custom_deadline_listview_entry,
                formattedDeadlines);
        lv.setAdapter(adapter);
    }

    private void setTwoListViewsHeight(ListView listView1, ListView listView2) {
        if (listView1.getAdapter().getCount() > listView2.getAdapter().getCount()) {
            setListViewHeightBasedOnChildren(listView1, listView2);
            setListViewHeightBasedOnChildren(listView1, listView1);
        } else {
            setListViewHeightBasedOnChildren(listView2, listView2);
            setListViewHeightBasedOnChildren(listView2, listView1);
        }
    }

    private void setListViewsHeightAllDays() {
        ListView monday = findViewById(R.id.deadlinesMondayList);
        ListView tuesday = findViewById(R.id.deadlinesTuesdayList);
        ListView wednesday = findViewById(R.id.deadlinesWednesdayList);
        ListView thursday = findViewById(R.id.deadlinesThursdayList);
        ListView friday = findViewById(R.id.deadlinesFridayList);
        ListView saturday = findViewById(R.id.deadlinesSaturdayList);

        setTwoListViewsHeight(monday, thursday);
        setTwoListViewsHeight(tuesday, friday);
        setTwoListViewsHeight(wednesday, saturday);
    }

    private void outputDeadlines() {

        final TableLayout table = findViewById(R.id.deadlinesTableLayout);
        table.setColumnShrinkable(0, true);
        table.setColumnShrinkable(1, true);

        for (int i = 1; i <= 7; ++i)
            outputDeadlineByDay(i);

        setListViewsHeightAllDays();
    }

    private void setHeaders() {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");

        String monday = getResources().getString(R.string.monday,
                formatter.print(localDate.withDayOfWeek(1)));
        String tuesday = getResources().getString(R.string.tuesday,
                formatter.print(localDate.withDayOfWeek(2)));
        String wednesday = getResources().getString(R.string.wednesday,
                formatter.print(localDate.withDayOfWeek(3)));
        String thursday = getResources().getString(R.string.thursday,
                formatter.print(localDate.withDayOfWeek(4)));
        String friday = getResources().getString(R.string.friday,
                formatter.print(localDate.withDayOfWeek(5)));
        String saturday = getResources().getString(R.string.saturday,
                formatter.print(localDate.withDayOfWeek(6)));
        String sunday = getResources().getString(R.string.sunday,
                formatter.print(localDate.withDayOfWeek(7)));

        TextView tv = findViewById(R.id.deadlinesMondayHeader);
        tv.setText(monday);
        tv = findViewById(R.id.deadlinesTuesdayHeader);
        tv.setText(tuesday);
        tv = findViewById(R.id.deadlinesWednesdayHeader);
        tv.setText(wednesday);
        tv = findViewById(R.id.deadlinesThursdayHeader);
        tv.setText(thursday);
        tv = findViewById(R.id.deadlinesFridayHeader);
        tv.setText(friday);
        tv = findViewById(R.id.deadlinesSaturdayHeader);
        tv.setText(saturday);
        tv = findViewById(R.id.deadlinesSundayHeader);
        tv.setText(sunday);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadlines);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle(getString(R.string.deadlineHeader));

        localDate = (LocalDate) getIntent().getSerializableExtra("date");

        setHeaders();
        outputDeadlines();

        Button nextWeekButton = findViewById(R.id.nextWeekButton);
        nextWeekButton.setText(R.string.next_week);
        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                localDate = localDate.plusDays(7);
                outputDeadlines();
            }
        });

        Button button = findViewById(R.id.previousWeekButton);
        button.setText(getResources().getString(R.string.previous_week));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                localDate = localDate.minusDays(7);
                setHeaders();
                outputDeadlines();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deadlines, menu);
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

        } /*else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivityForResult(intent, 1);

        } else if (id == R.id.nav_studymaterials) {
            Intent intent = new Intent(this, StudyMaterialsActivity.class);
            startActivityForResult(intent, 1);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        outputDeadlines();
    }
}
