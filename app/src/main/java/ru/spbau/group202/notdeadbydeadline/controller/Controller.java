package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.Context;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.apache.commons.collections4.ListUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.spbau.group202.notdeadbydeadline.model.CreditEnum;
import ru.spbau.group202.notdeadbydeadline.model.DetailedEntry;
import ru.spbau.group202.notdeadbydeadline.model.DetailedTimedEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;
import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.ExamEnum;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialSourcseAccessException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsUpdatingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UrlDownloadingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UnrecognizedCreditFormException;

public class Controller {
    private static File appDirectory;
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
                    ExamController.examDatabase.getExamsBySubject(subject));
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

        public static void setHomeworkDeferralById(int id, int deferral) {
            homeworkDatabase.setDeferralById(id, deferral);
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
        public static Bundle getHomeworkById(int id) {
            List<Homework> homeworks = homeworkDatabase.getHomeworkById(id);
            return getEntriesDeconstructedList(homeworks).get(0);
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
            List<Exam> exams = ExamController.examDatabase.getExamsByDay(day);
            List<DetailedTimedEntry> detailedEntryList = ListUtils.union(exams, classes);
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

        public static void editScheduleEntryById(int id, @NotNull String subject, int dayOfWeek,
                                                 int hour, int minute, @NotNull WeekParityEnum weekParity,
                                                 @NotNull String auditorium, @NotNull String teacher) {
            deleteScheduleEntryById(id);
            ScheduleEntry scheduleEntry = new ScheduleEntry(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
            scheduleDatabase.addScheduleEntry(scheduleEntry);
        }

        @NotNull
        public static Bundle getScheduleEntryById(int id) {
            List<ScheduleEntry> scheduleEntries = (scheduleDatabase.getScheduleEntryById(id));
            return getEntriesDeconstructedList(scheduleEntries).get(0);
        }
    }

    public static class ExamController {
        private static ExamDatabaseController examDatabase;

        @NotNull
        public static List<List<String>> getExamsBySubject(@NotNull String subject) {
            return getEntriesDetailList(examDatabase.getExamsBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getExamsByDay(@NotNull LocalDate date) {
            return getEntriesDetailList(examDatabase.getExamsByDay(date));
        }

        public static void addExam(@NotNull LocalDateTime date, @NotNull String subject,
                                   @NotNull ExamEnum examEnum, String description) {
            int id = settings.getTotalNumberOfWorks();
            Exam exam = new Exam(subject, description, date, examEnum, id);
            examDatabase.addExam(exam);
            settings.saveTotalNumberOfWorks(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        public static void deleteExamsById(int id) {
            examDatabase.deleteExamById(id);
        }

        public static void setExamAcceptedById(int id, boolean isAccepted) {
            examDatabase.setAcceptedById(id, isAccepted);
        }

        public static void editExamById(int id, @NotNull LocalDateTime date, @NotNull String subject,
                                        @NotNull ExamEnum examEnum, String description) {
            examDatabase.deleteExamById(id);
            Exam exam = new Exam(subject, description, date, examEnum, id);
            examDatabase.addExam(exam);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static Bundle getExamById(int id) {
            return getEntriesDeconstructedList(examDatabase.getExamById(id)).get(0);
        }
    }

    public static class StudyMaterialsController {
        private static String studyMaterialResource = "https://cdkrot.me/";
        private static String urlContent;
        private static StudyMaterialDatabaseController studyMaterialDatabase;

        @NotNull
        public static List<List<String>> getStudyMaterialsBySubject(@NotNull String subject) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialsBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getStudyMaterialsByTerm(int term) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialsByTerm(term));
        }

        //TODO path
        public static void addStudyMaterialByURL(@NotNull String subject, int term, @NotNull String URL)
                throws MalformedURLException, UrlDownloadingException {
            int id = settings.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(subject, term, URL, "", id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settings.saveTotalNumberOfStudyMaterials(++id);

            if (!subject.equals("Not stated") && subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
            studyMaterial.update();
        }

        public static void addLocalStudyMaterial(@NotNull String subject, int term, @NotNull String path) {
            int id = settings.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(subject, term, "", path, id);
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
                                                 @NotNull String URL, @NotNull String path)
                throws MalformedURLException, UrlDownloadingException {
            deleteStudyMaterialById(id);
            StudyMaterial studyMaterial = new StudyMaterial(subject, term, URL, path, id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);

            if (!subject.equals("Not stated") && subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditEnum.NOT_STATED, -1);
            }
            studyMaterial.update();
        }

        @NotNull
        public static Bundle getStudyMaterialById(int id) {
            return getEntriesDeconstructedList(studyMaterialDatabase.getStudyMaterialById(id)).get(0);
        }

        public static void updateStudyMaterials() throws StudyMaterialSourcseAccessException,
                StudyMaterialsUpdatingException {
            try {
                URL url = new URL(studyMaterialResource);
                FileUtils.copyURLToFile(url, new File(appDirectory, studyMaterialResource));
                urlContent = FileUtils.readFileToString(new File(url.toURI()), "UTF-8");
            } catch (Exception exception) {
                throw new StudyMaterialSourcseAccessException();
            }

            List<StudyMaterial> studyMaterials = studyMaterialDatabase.getUpdatableStudyMaterials();
            boolean allUpdated = true;
            ArrayList<StudyMaterial> failedToUpdate = new ArrayList<>();
            for (StudyMaterial studyMaterial : studyMaterials) {
                if (isOutdated(studyMaterial)) {
                    try {
                        studyMaterial.update();
                    } catch (Exception exception) {
                        allUpdated = false;
                        failedToUpdate.add(studyMaterial);
                    }
                }
            }

            if (!allUpdated) {
                throw new StudyMaterialsUpdatingException(getEntriesDetailList(failedToUpdate));
            }
        }

        @NotNull
        //TODO cdkrot parsing
        public List<List<String>> getAvaibleStudyMaterialsList() {
            return new ArrayList<>();
        }

        @NotNull
        //TODO google parsing
        public static List<File> searchForStudyMaterials(String query) {
            return new ArrayList<>();
        }

        //TODO cdkrot parsing
        private static boolean isOutdated(StudyMaterial studyMaterial) {
            return true;
        }
    }

    public static void createDatabases(@NotNull Context context) throws StudyMaterialSourcseAccessException,
            StudyMaterialsUpdatingException {
        HomeworkController.homeworkDatabase = new HomeworkDatabaseController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        ScheduleController.scheduleDatabase = new ScheduleDatabaseController(context);
        ExamController.examDatabase = new ExamDatabaseController(context);
        StudyMaterialsController.studyMaterialDatabase = new StudyMaterialDatabaseController(context);
        settings = new StoredDataController(context);
        subjectList = new HashSet<>();
        subjectList.addAll(subjectDatabase.getAllSubjects());
        appDirectory = context.getApplicationContext().getFilesDir();

        HomeworkController.generateHomeworks();
        StudyMaterialsController.updateStudyMaterials();
    }

    @NotNull
    private static <T extends DetailedEntry> List<List<String>> getEntriesDetailList(@NotNull List<T> entries) {
        List<List<String>> entriesDetails = new ArrayList<>();
        for (T entry : entries) {
            entriesDetails.add(entry.getDetails());
        }
        return entriesDetails;
    }


    @NotNull
    private static <T extends DetailedEntry> List<Bundle> getEntriesDeconstructedList(@NotNull List<T> entries) {
        List<Bundle> deconstructedEntries = new ArrayList<>();
        for (T entry : entries) {
            deconstructedEntries.add(entry.getDeconstructed());
        }
        return deconstructedEntries;
    }


}