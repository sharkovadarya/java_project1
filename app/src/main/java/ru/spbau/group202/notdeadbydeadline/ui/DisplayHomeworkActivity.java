package ru.spbau.group202.notdeadbydeadline.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.group202.notdeadbydeadline.controller.Controller;
import ru.spbau.group202.notdeadbydeadline.R;
import ru.spbau.group202.notdeadbydeadline.ui.utilities.CustomDialogFragment;


public class DisplayHomeworkActivity extends AppCompatActivity {


    private void outputHomeworksBySubject(String subject) {
        List<List<String>> formattedHomeworksDetails =
                Controller.HomeworkController.getHomeworksBySubject(subject);

        ListView homeworksListView = findViewById(R.id.homeworksListView);
        HomeworkListViewAdapter adapter1 = new HomeworkListViewAdapter(this, formattedHomeworksDetails);
        homeworksListView.setAdapter(adapter1);
    }

    private void processOnLongTapHomework() {
        ListView homeworksListView = findViewById(R.id.homeworksListView);
        homeworksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup = new PopupMenu(DisplayHomeworkActivity.this, view);
                popup.getMenuInflater()
                        .inflate(R.menu.homework_listview_item_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString().equals(getResources()
                                        .getString(R.string.lv_entry_edit))) {
                            List<String> detailedEntryList = (List<String>) parent.getItemAtPosition(position);
                            int id = Integer.parseInt(detailedEntryList.get(detailedEntryList.size() - 1));
                            Intent intent = new Intent(DisplayHomeworkActivity.this, AddHomeworkActivity.class);
                            intent.putExtra("id", id);
                            startActivityForResult(intent, 1);
                            return true;
                        } else if (item.getTitle().toString().equals(getResources()
                                .getString(R.string.lv_entry_delete))) {
                            List<String> detailedEntryList = (List<String>) parent.getItemAtPosition(position);
                            Controller.HomeworkController.deleteHomeworkById(Integer.parseInt(detailedEntryList.get(detailedEntryList.size() - 1)));
                            recreate();
                            return true;
                        } else if (item.getTitle().toString().equals(getResources()
                                .getString(R.string.lv_entry_open_attached))) {
                            List<String> detailedEntryList = (List<String>) parent.getItemAtPosition(position);
                            CustomDialogFragment cdf = new CustomDialogFragment();
                            Bundle homeworkEntry = Controller.HomeworkController
                                    .getHomeworkById(Integer.parseInt(detailedEntryList
                                            .get(detailedEntryList.size() - 1)));
                            ArrayList<String> src = homeworkEntry.getStringArrayList("materials");
                            cdf.setFilepaths(src);
                            cdf.show(getSupportFragmentManager(), "files");
                        }

                        return false;
                    }
                });

                popup.show(); //showing popup menu
                return true;
            }
        });
    }



    private void outputHomeworks() {
        String subject = getIntent().getStringExtra("SUBJECT_NAME");
        setTitle(subject);

        outputHomeworksBySubject(subject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        outputHomeworks();
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

        //outputHomeworksBySubject(subject);
        outputHomeworks();
        processOnLongTapHomework();
    }

}
