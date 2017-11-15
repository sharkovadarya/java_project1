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

    private static ArrayList<ArrayList<String>> getHomeworkDetails(ArrayList<Homework> homeworks) {
        ArrayList<ArrayList<String>> formattedHomeworks = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedHomeworks.add(homework.getHomeworkDetails());
            formattedHomeworks.add(homework.getHomeworkDetails());
        }

        return formattedHomeworks;
    }

    private static ArrayList<ArrayList<String>> getFormattedDeadlines(ArrayList<Homework> homeworks) {
        ArrayList<ArrayList<String>> formattedDeadlines = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedDeadlines.add(homework.getDeadlineDetails());
        }

        return formattedDeadlines;
    }

    public static ArrayList<ArrayList<String>> getFormattedActualDeadlines() {
        return getFormattedDeadlines(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<ArrayList<String>> getFormattedActualHomeworks() {
        return getHomeworkDetails(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<ArrayList<String>> getFormattedHomeworksBySubject(String subject) {
        return getHomeworkDetails(homeworkDatabase.getHomeworksBySubject(subject));
    }

    public static ArrayList<ArrayList<String>> getFormattedHomeworksByDay(int year, int month, int day) {
        return getHomeworkDetails(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    public static ArrayList<String> getSubjectList() {
        Set<String> subjectList = new LinkedHashSet<>();

        for (Homework homework : homeworkDatabase.getActualHomeworks()) {
            subjectList.add(homework.getSubject());
        }

        return new ArrayList<String>(subjectList);
    }

    public static ArrayList<ArrayList<String>>
                    getDeadlinesDetailsByDates(int year1, int month1, int day1,
                                               int year2, int month2, int day2) {
        return null;
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
