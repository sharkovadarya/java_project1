package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.Context;

import java.lang.ref.WeakReference;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.UI.AddHomeworkActivity;

public class Controller {
    private Homeworks homeworks;
    private HomeworkDatabaseController homeworkDatabase;

    public Controller(Context context){
        homeworkDatabase = new HomeworkDatabaseController(context);
        homeworks = new Homeworks(homeworkDatabase.getActualHomeworks());
    }

    public AddHomeworkManager addHomeworkManager = new AddHomeworkManager(homeworks);
}
