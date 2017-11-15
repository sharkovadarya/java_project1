package ru.spbau.group202.notdeadbydeadline.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.time.LocalDateTime;

import ru.spbau.group202.notdeadbydeadline.Controller.Controller;
import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Controller.createDatabases(this);

        outputCurrentDate();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_deadlines) {
            Intent intent = new Intent(this, DeadlinesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_homework) {
            Intent intent = new Intent(this, HomeworkActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_schedule) {

        } else if (id == R.id.nav_studymaterials) {

        } 

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
