package ru.spbau.group202.notdeadbydeadline.Controller;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.Model.CreditEnum;
import ru.spbau.group202.notdeadbydeadline.Model.DetailedEntry;
import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.Model.SubjectCredit;

public class Controller {
    private static StoredDataController settings;
    private static SubjectDatabaseController subjectDatabase;
    private static Set<String> subjectList;

    @NotNull
    public static List<String> getSubjectList() {
        return new ArrayList<>(subjectList);
    }

    public static void setWeekPairity(boolean isInversed) {
        settings.saveWeekPairity(isInversed);
    }

    public static class AcademicProgressController{
        @NotNull
        public List<String> calculateProgress(String subject) throws UnrecognizedCreditFormException{
            SubjectCredit subjectCredit = subjectDatabase.getSubjectCredit(subject);
            return subjectCredit.calculateProgress(HomeworkController
                    .homeworkDatabase.getPassedHomeworksBySubject(subject));
        }

        public void setSubjectCreditForm(@NotNull String subject, CreditEnum credit) {
            subjectDatabase.setSubjectCreditForm(subject, credit);
        }
    }

    public static class HomeworkController {
        private static HomeworkDatabaseController homeworkDatabase;

        @NotNull
        public static List<List<String>> getHomeworksBySubject(@NotNull String subject) {
            return getEntriesDetailList(homeworkDatabase.getHomeworksBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getPassedHomeworksBySubject(@NotNull String subject) {
            return getEntriesDetailList(homeworkDatabase.getPassedHomeworksBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getHomeworksByDay(int year, int month, int day) {
            return getEntriesDetailList(homeworkDatabase.getHomeworksByDay(year, month, day));
        }

        public static void addHomework(int year, int month, int day, int hour, int minutes,
                                       @NotNull String subject, boolean isRegular, String description,
                                       String howToSend, double expectedScore) {
            int id = settings.getTotalNumberOfHW();
            Homework homework = new Homework(year, month, day, hour, minutes, subject, isRegular,
                    description, howToSend, expectedScore, id);
            homeworkDatabase.addHomework(homework);
            settings.saveTotalNumberOfHW(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NotStated, -1);
            }
        }

        public static void deleteHomeworkById(int id) {
            homeworkDatabase.deleteHomeworkById(id);
        }

        public static void setHomeworkScoreById(int id, int score) {
            homeworkDatabase.setScoreById(id, score);
        }

        @NotNull
        public static List<List<String>> getDeadlinesByDay(int year, int month, int day) {
            return getEntriesDetailList(toDeadlines(homeworkDatabase.getHomeworksByDay(year, month, day)));
        }

        @NotNull
        private static List<Homework.Deadline> toDeadlines(@NotNull List<Homework> homeworks) {
            List<Homework.Deadline> deadlines = new ArrayList<>();
            for (Homework homework : homeworks) {
                deadlines.add(homework.getDeadline());
            }
            return deadlines;
        }
    }

    public static class ScheduleController {
        private static ScheduleDatabaseController scheduleDatabase;

        @NotNull
        public static List<List<String>> getScheduleByDayOfWeek(int dayOfWeek, boolean isOnEvenWeek) {
            return getEntriesDetailList(scheduleDatabase.getDaySchedule(dayOfWeek, isOnEvenWeek));
        }

        public static void addScheduleEntry(@NotNull String subject, int dayOfWeek, int hour,
                                            int minute, boolean isOnEvenWeeks,
                                            @NotNull String auditorium, @NotNull String teacher) {
            int id = settings.getTotalNumberOfHW();
            ScheduleEntry scheduleEntry = new ScheduleEntry(subject, dayOfWeek, hour, minute,
                    isOnEvenWeeks, auditorium, teacher, id);
            scheduleDatabase.addScheduleEntry(scheduleEntry);
            settings.saveTotalNumberOfScheduleEntries(++id);
        }

        public static void deleteScheduleEntryById(int id) {
            HomeworkController.homeworkDatabase.deleteHomeworkById(id);
        }

    }

    public static void createDatabases(@NotNull Context context) {
        HomeworkController.homeworkDatabase = new HomeworkDatabaseController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        ScheduleController.scheduleDatabase = new ScheduleDatabaseController(context);
        settings = new StoredDataController(context);
        subjectList = new HashSet<>();
        subjectList.addAll(subjectDatabase.getAllSubjects());
    }

    @NotNull
    private static <T extends DetailedEntry> List<List<String>> getEntriesDetailList(@NotNull List<T> entries) {
        List<List<String>> entriesDetails = new ArrayList<>();
        for (T entry : entries) {
            entriesDetails.add(entry.getDetails());
        }
        return entriesDetails;
    }


}