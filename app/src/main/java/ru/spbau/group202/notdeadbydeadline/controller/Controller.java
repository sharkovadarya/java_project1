package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.model.CreditEnum;
import ru.spbau.group202.notdeadbydeadline.model.DetailedEntry;
import ru.spbau.group202.notdeadbydeadline.model.DetailedTimeEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;
import ru.spbau.group202.notdeadbydeadline.model.Work;
import ru.spbau.group202.notdeadbydeadline.model.WorkEnum;

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
            return subjectCredit.calculateProgress(HomeworkController.homeworkDatabase.getPassedHomeworksBySubject(subject),
                    WorkController.workDatabase.getWorksBySubject(subject));
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

        public static void addHomework(@NotNull LocalDateTime deadline, @NotNull String subject,
                                       int regularity, String description, String howToSend,
                                       double expectedScore, @NotNull ArrayList<String> materials) {
            int id = settings.getTotalNumberOfHW();
            Homework homework = new Homework(deadline, subject, regularity, description,
                    howToSend, expectedScore, id, materials);
            homeworkDatabase.addHomework(homework);
            settings.saveTotalNumberOfHW(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        public static void deleteHomeworkById(int id) {
            homeworkDatabase.deleteHomeworkById(id);
        }

        public static void setHomeworkScoreById(int id, int score) {
            homeworkDatabase.setScoreById(id, score);
        }

        public static void editHomeworkById(int id, @NotNull LocalDateTime deadline, @NotNull String subject,
                                            int regularity, String description, String howToSend,
                                            double expectedScore, @NotNull ArrayList<String> materials) {
            deleteHomeworkById(id);
            Homework homework = new Homework(deadline, subject, regularity, description,
                    howToSend, expectedScore, id, materials);
            homeworkDatabase.addHomework(homework);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static List<String> getHomeworkById(int id) {
            return getEntriesDetailList(homeworkDatabase.getHomeworkById(id)).get(0);
        }

        @NotNull
        public static List<List<String>> getDeadlinesByDay(@NotNull LocalDate date) {
            return getEntriesDetailList(toDeadlines(homeworkDatabase.getHomeworksByDay(date)));
        }

        public static void generateHomeworks() {
            LocalDate today = LocalDate.now();
            for (Homework homework : homeworkDatabase.getHomeworksByDay(today)) {
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
        public static List<List<String>> getScheduleByDay(LocalDate day) {
            WeekParityEnum weekParity = WeekParityEnum.values()[day.getWeekOfWeekyear() % 2];
            if (settings.getParityOfWeek()) {
                weekParity = weekParity.inverse();
            }

            List<ScheduleEntry> classes = scheduleDatabase.getDaySchedule(day.getDayOfWeek() - 1,
                    weekParity);
            List<Work> works = WorkController.workDatabase.getWorksByDay(day);
            List<DetailedTimeEntry> detailedEntryList = ListUtils.union(works, classes);
            Collections.sort(detailedEntryList);

            return getEntriesDetailList(detailedEntryList);
        }

        public static void addScheduleEntry(@NotNull String subject, int dayOfWeek, int hour,
                                            int minute, @NotNull WeekParityEnum weekParity,
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

    //TODO rename
    public static class WorkController {
        private static WorkDatabaseController workDatabase;

        @NotNull
        public static List<List<String>> getWorksBySubject(@NotNull String subject) {
            return getEntriesDetailList(workDatabase.getWorksBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getWorksByDay(@NotNull LocalDate date) {
            return getEntriesDetailList(workDatabase.getWorksByDay(date));
        }

        public static void addWork(@NotNull LocalDateTime date, @NotNull String subject,
                                   @NotNull WorkEnum workEnum, String description) {
            int id = settings.getTotalNumberOfWorks();
            Work work = new Work(subject, description, date, workEnum, id);
            workDatabase.addWork(work);
            settings.saveTotalNumberOfWorks(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        public static void deleteWorkById(int id) {
            workDatabase.deleteWorkById(id);
        }

        public static void setWorkAcceptedById(int id, boolean isAccepted) {
            workDatabase.setAcceptedById(id, isAccepted);
        }

        public static void editWorkById(int id, @NotNull LocalDateTime date, @NotNull String subject,
                                        @NotNull WorkEnum workEnum, String description) {
            deleteWorkById(id);
            Work work = new Work(subject, description, date, workEnum, id);
            workDatabase.addWork(work);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static List<String> getWorkById(int id) {
            return getEntriesDetailList(workDatabase.getWorkById(id)).get(0);
        }
    }

    public static class StudyMaterialsController {
        private static StudyMaterialDatabaseController studyMaterialDatabase;

        @NotNull
        public static List<List<String>> getStudyMaterialsBySubject(@NotNull String subject) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialsBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getStudyMaterialsByTerm(int term) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialsByTerm(term));
        }

        public static void addStudyMaterial(@NotNull String subject, int term, @NotNull String URL,
                                            @NotNull String path) {
            int id = settings.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(subject, term, URL, path, id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settings.saveTotalNumberOfStudyMaterials(++id);

            if (!subject.equals("Not stated") && subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        public static void deleteStudyMaterialById(int id) {
            studyMaterialDatabase.deleteStudyMaterialById(id);
        }

        public static void editStudyMaterialById(int id, @NotNull String subject, int term,
                                                 @NotNull String URL, @NotNull String path) {
            deleteStudyMaterialById(id);
            StudyMaterial studyMaterial = new StudyMaterial(subject, term, URL, path, id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);

            if (!subject.equals("Not stated") && subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static List<String> getStudyMaterialById(int id) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialById(id)).get(0);
        }

    }

    public static void createDatabases(@NotNull Context context) {
        HomeworkController.homeworkDatabase = new HomeworkDatabaseController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        ScheduleController.scheduleDatabase = new ScheduleDatabaseController(context);
        WorkController.workDatabase = new WorkDatabaseController(context);
        StudyMaterialsController.studyMaterialDatabase = new StudyMaterialDatabaseController(context);
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