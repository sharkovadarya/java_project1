package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.Model.DetailedEntry;
import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.ScheduleEntry;

public class Controller {
    private static HomeworkDatabaseController homeworkDatabase;
    private static SubjectDatabaseController subjectDatabase;
    private static ScheduleDatabaseController scheduleDatabase;
    private static StoredDataController settings;
    private static Set<String> subjectList;

    public static ArrayList<ArrayList<String>> getActualHomeworks() {
        return getEntriesDetailList(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<ArrayList<String>> getHomeworksBySubject(String subject) {
        return getEntriesDetailList(homeworkDatabase.getHomeworksBySubject(subject));
    }

    public static ArrayList<ArrayList<String>> getPassedHomeworksBySubject(String subject) {
        ArrayList<Homework> passedHomeworks = new ArrayList<>();
        for(Homework homework : homeworkDatabase.getHomeworksBySubject(subject)){
            if(homework.hasPassed()){
                passedHomeworks.add(homework);
            }
        }

        return getEntriesDetailList(passedHomeworks);
    }

    public static ArrayList<ArrayList<String>> getHomeworksByDay(int year, int month, int day) {
        return getEntriesDetailList(homeworkDatabase.getHomeworksByDay(year, month, day));
    }

    public static ArrayList<ArrayList<String>> getHomeworksBetweenDates(int year1, int month1, int day1,
                                                                        int year2, int month2, int day2) {
        return getEntriesDetailList(homeworkDatabase.getHomeworksBetweenDates(year1, month1, day1,
                year2, month2, day2));
    }

    public static void addHomework(int year, int month, int day, int hour, int minutes,
                                   String subject, boolean isRegular, String description,
                                   String howToSend, double expectedScore) {
        int id = settings.getTotalNumberOfHW();
        Homework homework = new Homework(year, month, day, hour, minutes, subject, isRegular,
                description, howToSend, expectedScore, id);
        homeworkDatabase.addHomework(homework);
        settings.saveTotalNumberOfHW(++id);

        if (subjectList.add(subject)) {
            subjectDatabase.addSubject(subject);
        }
    }

    public static void deleteHomeworkById(int id) {
        homeworkDatabase.deleteHomeworkById(id);
    }

    public static void setHomeworkScoreById(int id, int score) {
        homeworkDatabase.setScoreById(id, score);
    }

    public static ArrayList<ArrayList<String>> getActualDeadlines() {
        return getEntriesDetailList(homeworkDatabase.getActualHomeworks());
    }

    public static ArrayList<ArrayList<String>> getDeadlinesByDay(int year, int month, int day) {
        return getEntriesDetailList(toDeadlines(homeworkDatabase.getHomeworksByDay(year, month, day)));
    }

    public static ArrayList<ArrayList<String>> getDeadlinesBetweenDates(int year1, int month1, int day1,
                                                                        int year2, int month2, int day2) {
        return getEntriesDetailList(homeworkDatabase.getHomeworksBetweenDates(year1, month1, day1,
                year2, month2, day2));
    }

    public static ArrayList<ArrayList<String>> getScheduleByDayOfWeek(int dayOfWeek, boolean isOnEvenWeek) {
        return getEntriesDetailList(scheduleDatabase.getDaySchedule(dayOfWeek, isOnEvenWeek));
    }

    public static void addScheduleEntry(String subject, int dayOfWeek, int hour, int minute,
                                        boolean isOnEvenWeeks, String auditorium, String teacher) {
        int id = settings.getTotalNumberOfHW();
        ScheduleEntry scheduleEntry = new ScheduleEntry(subject, dayOfWeek, hour, minute,
                isOnEvenWeeks, auditorium, teacher, id);
        scheduleDatabase.addScheduleEntry(scheduleEntry);
        settings.saveTotalNumberOfScheduleEntries(++id);
    }

    public static void deleteScheduleEntryById(int id) {
        homeworkDatabase.deleteHomeworkById(id);
    }

    public static ArrayList<String> getSubjectList() {
        Set<String> subjectList = new LinkedHashSet<>();

        subjectList.addAll(Controller.subjectList);

        return new ArrayList<>(subjectList);
    }

    public static void createDatabases(Context context) {
        homeworkDatabase = new HomeworkDatabaseController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        scheduleDatabase = new ScheduleDatabaseController(context);
        settings = new StoredDataController(context);
        subjectList = new LinkedHashSet<>();
        subjectList.addAll(subjectDatabase.getAllSubjects());
    }

    private static <T extends DetailedEntry> ArrayList<ArrayList<String>> getEntriesDetailList(ArrayList<T> entries) {
        ArrayList<ArrayList<String>> entriesDetails = new ArrayList<>();
        for (T entry : entries) {
            entriesDetails.add(entry.getDetails());
        }
        return entriesDetails;
    }

    private static ArrayList<Homework.Deadline> toDeadlines(ArrayList<Homework> homeworks) {
        ArrayList<Homework.Deadline> deadlines = new ArrayList<>();
        for (Homework homework : homeworks) {
            deadlines.add(homework.getDeadline());
        }
        return deadlines;

    }

}