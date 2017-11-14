package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.UI.AddHomeworkActivity;

public class Controller {
    //TODO maybe should be replaced by ArrayList<Homework> homeworks.
    private Homeworks homeworks;
    private HomeworkDatabaseController homeworkDatabase;

    static private ArrayList<String> getFormattedHomeworks(ArrayList<Homework> homeworks) {
        ArrayList<String> formattedHomeworks = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedHomeworks.add(homework.getFormattedHomework());
        }

        return formattedHomeworks;
    }

    //TODO maybe should be moved to AddHomeworkActivity.
    public AddHomeworkManager addHomeworkManager;

    public Controller(Context context) {
        homeworkDatabase = new HomeworkDatabaseController(context);
        homeworks = new Homeworks(homeworkDatabase.getActualHomeworks());
        addHomeworkManager = new AddHomeworkManager(homeworks);
    }

    public ArrayList<String> getFormattedHomeworksBySubject(String subject) {
        return getFormattedHomeworks(homeworkDatabase.getHomeworksBySubject(subject));
    }

    public ArrayList<String> getFormattedHomeworksByDay(int year, int month, int day) {
        return getFormattedHomeworks(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    public void addHomework(int year, int month, int day, int hour, int minutes,
                            String subject, boolean isRegular, String description,
                            String howToSend,  int expectedScore) {
        Homework newHomework = new Homework(year, month, day, hour, minutes, subject, isRegular,
                description, howToSend, expectedScore);
        homeworkDatabase.addHomework(newHomework);
        homeworks.addHomework(newHomework);
    }
}
