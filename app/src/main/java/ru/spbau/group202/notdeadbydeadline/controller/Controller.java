package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.model.CreditEnum;
import ru.spbau.group202.notdeadbydeadline.model.DetailedEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;

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

    public static class AcademicProgressController {
        @NotNull
        public List<String> calculateProgress(@NotNull String subject) throws UnrecognizedCreditFormException {
            SubjectCredit subjectCredit = subjectDatabase.getSubjectCredit(subject);
            return subjectCredit.calculateProgress(HomeworkController
                    .homeworkDatabase.getPassedHomeworksBySubject(subject));
        }

        public void setSubjectCreditForm(@NotNull String subject, @NotNull CreditEnum credit) {
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
        public static List<List<String>> getHomeworksByDay(@NotNull LocalDate date) {
            return getEntriesDetailList(homeworkDatabase.getHomeworksByDay(date));
        }

        public static void addHomework(@NotNull LocalDateTime deadline, @NotNull String subject, int regularity,
                                       String description, String howToSend, double expectedScore) {
            int id = settings.getTotalNumberOfHW();
            Homework homework = new Homework(deadline, subject, regularity, description,
                    howToSend, expectedScore, id);
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
        public static List<List<String>> getDeadlinesByDay(@NotNull LocalDate date) {
            return getEntriesDetailList(toDeadlines(homeworkDatabase.getHomeworksByDay(date)));
        }

        public static void generateHomeworks() {
            LocalDate today = LocalDate.now();
            for(Homework homework : homeworkDatabase.getHomeworksByDay(today)) {
                if (homework.getRegularity() != 0) {
                    int id = settings.getTotalNumberOfHW();
                    homeworkDatabase.addHomework(homework.generateNewHomeworkById(id));
                    settings.saveTotalNumberOfHW(++id);
                }
            }
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
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        public static List<List<String>> getScheduleByDayOfWeek(int dayOfWeek, WeekParityEnum weekParity) {
            return getEntriesDetailList(scheduleDatabase.getDaySchedule(dayOfWeek, weekParity));
        }

        public static void addScheduleEntry(@NotNull String subject, int dayOfWeek, int hour,
                                            int minute, WeekParityEnum weekParity,
                                            @NotNull String auditorium, @NotNull String teacher) {
            int id = settings.getTotalNumberOfScheduleEntries();
            ScheduleEntry scheduleEntry = new ScheduleEntry(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
            scheduleDatabase.addScheduleEntry(scheduleEntry);
            settings.saveTotalNumberOfScheduleEntries(++id);
        }

        public static void deleteScheduleEntryById(int id) {
            scheduleDatabase.deleteScheduleEntryById(id);
        }

    }

    public static void createDatabases(@NotNull Context context) {
        HomeworkController.homeworkDatabase = new HomeworkDatabaseController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        ScheduleController.scheduleDatabase = new ScheduleDatabaseController(context);
        settings = new StoredDataController(context);
        subjectList = new HashSet<>();
        subjectList.addAll(subjectDatabase.getAllSubjects());
        HomeworkController.generateHomeworks();
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