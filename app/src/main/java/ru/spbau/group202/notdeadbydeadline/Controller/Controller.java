package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.Context;

import java.util.ArrayList;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;

public class Controller {
    static private HomeworkDatabaseController homeworkDatabase;

    static private ArrayList<String> getFormattedHomeworks(ArrayList<Homework> homeworks) {
        ArrayList<String> formattedHomeworks = new ArrayList<>();

        for (Homework homework : homeworkDatabase.getActualHomeworks()) {
            formattedHomeworks.add(homework.getFormattedHomework());
        }

        return formattedHomeworks;
    }

    static public ArrayList<String> getFormattedHomeworksBySubject(String subject) {
        return getFormattedHomeworks(homeworkDatabase.getHomeworksBySubject(subject));
    }

    static public ArrayList<String> getFormattedHomeworksByDay(int year, int month, int day) {
        return getFormattedHomeworks(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    static public void addHomework(int year, int month, int day, int hour, int minutes,
                            String subject, boolean isRegular, String description,
                            String howToSend,  int expectedScore) {
        Homework newHomework = new Homework(year, month, day, hour, minutes, subject, isRegular,
                description, howToSend, expectedScore);
        homeworkDatabase.addHomework(newHomework);
    }

    static public void createHomeworkDatabase(Context context){
        homeworkDatabase = new HomeworkDatabaseController(context);
    }
}
