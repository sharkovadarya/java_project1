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

    private static ArrayList<ArrayList<String>> getHomeworksList(ArrayList<Homework> homeworks) {
        ArrayList<ArrayList<String>> formattedHomeworks = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedHomeworks.add(homework.getHomeworkDetails());
            // TODO why was the second line here???
            // formattedHomeworks.add(homework.getHomeworkDetails());
        }

        return formattedHomeworks;
    }

    private static ArrayList<ArrayList<String>> getDeadlinesList(ArrayList<Homework> homeworks) {
        ArrayList<ArrayList<String>> formattedDeadlines = new ArrayList<>();

        for (Homework homework : homeworks) {
            formattedDeadlines.add(homework.getDeadlineDetails());
        }

        return formattedDeadlines;
    }

    public static ArrayList<ArrayList<String>> getActualDeadlines() {
        return getDeadlinesList(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<ArrayList<String>> getActualHomeworks() {
        return getHomeworksList(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<ArrayList<String>> getHomeworksBySubject(String subject) {
        return getHomeworksList(homeworkDatabase.getHomeworksBySubject(subject));
    }

    public static ArrayList<ArrayList<String>> getHomeworksByDay(int year, int month, int day) {
        return getHomeworksList(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    public static ArrayList<ArrayList<String>> getDeadlinesByDay(int year, int month, int day) {
        return getDeadlinesList(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    public static ArrayList<ArrayList<String>> getDeadlinesBetweenDates(int year1, int month1, int day1,
                                                                       int year2, int month2, int day2) {
        return getDeadlinesList(homeworkDatabase.getHomeworksBetweenDates(year1, month1, day1,
                year2, month2, day2));
    }

    public static ArrayList<ArrayList<String>> getHomeworksBetweenDates(int year1, int month1, int day1,
                                                                        int year2, int month2, int day2) {
        return getHomeworksList(homeworkDatabase.getHomeworksBetweenDates(year1, month1, day1,
                year2, month2, day2));
    }

    public static ArrayList<String> getSubjectList() {
        Set<String> subjectList = new LinkedHashSet<>();

        for (Homework homework : homeworkDatabase.getActualHomeworks()) {
            subjectList.add(homework.getSubject());
        }

        return new ArrayList<>(subjectList);
    }

    public static void addHomework(int year, int month, int day, int hour, int minutes,
                                   String subject, boolean isRegular, String description,
                                   String howToSend, double expectedScore) {
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
