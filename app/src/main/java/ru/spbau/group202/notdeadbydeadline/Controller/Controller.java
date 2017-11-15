package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;

public class Controller {
    private static HomeworkDatabaseController homeworkDatabase;
    private static SubjectDatabaseController subjectDatabase;
    private static Set<String> subjectList;

    private static ArrayList<String> getFormattedHomeworks(ArrayList<Homework> homeworks) {
        ArrayList<String> formattedHomeworks = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedHomeworks.add(homework.getFormattedHomework());
        }

        return formattedHomeworks;
    }

    private static ArrayList<String> getFormattedDeadlines(ArrayList<Homework> homeworks) {
        ArrayList<String> formattedDeadlines = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedDeadlines.add(homework.getFormattedDeadline());
        }

        return formattedDeadlines;
    }

    public static ArrayList<String> getFormattedActualDeadlines() {
        return getFormattedDeadlines(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<String> getFormattedActualHomeworks() {
        return getFormattedHomeworks(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<String> getFormattedHomeworksBySubject(String subject) {
        return getFormattedHomeworks(homeworkDatabase.getHomeworksBySubject(subject));
    }

    public static ArrayList<String> getFormattedHomeworksByDay(int year, int month, int day) {
        return getFormattedHomeworks(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    public static ArrayList<String> getSubjectList() {
        Set<String> subjectList = new LinkedHashSet<>();

        for (Homework homework : homeworkDatabase.getActualHomeworks()) {
            subjectList.add(homework.getSubject());
        }

        return new ArrayList<String>(subjectList);
    }

    public static void addHomework(int year, int month, int day, int hour, int minutes,
                                   String subject, boolean isRegular, String description,
                                   String howToSend, int expectedScore) {
        Homework newHomework = new Homework(year, month, day, hour, minutes, subject, isRegular,
                description, howToSend, expectedScore);
        homeworkDatabase.addHomework(newHomework);

        if(subjectList.add(subject)){
            subjectDatabase.addSubject(subject);
        }
    }

    public static void createDatabases(Context context) {
        homeworkDatabase = new HomeworkDatabaseController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        subjectList = new LinkedHashSet<>();
        subjectList.addAll(subjectDatabase.getAllSubjects());
    }
}
