package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.Context;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.apache.commons.collections4.ListUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import ru.spbau.group202.notdeadbydeadline.controller.calendar.CalendarExporter;
import ru.spbau.group202.notdeadbydeadline.model.ClassEntry;
import ru.spbau.group202.notdeadbydeadline.model.CreditFormEnum;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;
import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.ExamEnum;
import ru.spbau.group202.notdeadbydeadline.model.utilities.AsyncTaskResult;
import ru.spbau.group202.notdeadbydeadline.model.utilities.ModelUtils;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsSourceAccessException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsUpdatingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UnrecognizedCreditFormException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UrlDownloadingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.WebStudyMaterialException;

public class Controller {
    private static Controller instance;
    private File appDirectory;
    private StoredDataController settingsDatabase;
    private SubjectDatabaseController subjectDatabase;
    private ExamController examController;
    private HomeworkController homeworkController;
    private ScheduleController scheduleController;
    private StudyMaterialController studyMaterialController;
    private CalendarExporter calendarExporter;
    private Set<String> subjectList;

    private Controller(@NotNull Context context) {
        homeworkController = new HomeworkController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        scheduleController = new ScheduleController(context);
        examController = new ExamController(context);
        studyMaterialController = new StudyMaterialController(context);
        settingsDatabase = new StoredDataController(context);
        calendarExporter = new CalendarExporter(context);
        subjectList = new HashSet<>();
        subjectList.addAll(subjectDatabase.getAllSubjects());
        appDirectory = context.getApplicationContext().getFilesDir();
    }

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context.getApplicationContext());
        }
        return instance;
    }

    public ExamController examController() {
        return examController;
    }

    public HomeworkController homeworkController() {
        return homeworkController;
    }

    public ScheduleController scheduleController() {
        return scheduleController;
    }

    public StudyMaterialController studyMaterialController() {
        return studyMaterialController;
    }

    @NotNull
    public List<String> getSubjectList() {
        return new ArrayList<>(subjectList);
    }

    public void setWeekPairity(boolean isInversed) {
        settingsDatabase.setWeekPairity(isInversed);
    }

    public boolean getWeekPairity() {
        return settingsDatabase.getWeekParity();
    }

    public void setGoogleCalendarSync(boolean isSync) {
        settingsDatabase.setGoogleCalendarSync(isSync);
    }

    public boolean getGoogleCalendarSync() {
        return settingsDatabase.getGoogleCalendarSync();
    }

    public void setEndTermDate(LocalDate date) {
        settingsDatabase.setEndTermDate(date);
    }

    public LocalDate getEndTermDate() {
        return settingsDatabase.getEndTermDate();
    }

    @NotNull
    public List<String> calculateProgress(@NotNull String subject) throws UnrecognizedCreditFormException {
        SubjectCredit subjectCredit = subjectDatabase.getSubjectCredit(subject);
        return subjectCredit.calculateProgress(homeworkController.homeworkDatabase.getPassedHomeworksBySubject(subject),
                examController.examDatabase.getExamsBySubject(subject));
    }

    public void setSubjectCreditForm(@NotNull String subject, @NotNull CreditFormEnum credit) {
        subjectDatabase.setSubjectCreditForm(subject, credit);
    }

    public class HomeworkController {
        private HomeworkDatabaseController homeworkDatabase;

        public HomeworkController(Context context) {
            homeworkDatabase = new HomeworkDatabaseController(context);
        }

        @NotNull
        public List<List<String>> getHomeworksBySubject(@NotNull String subject) {
            return ModelUtils.map(homeworkDatabase.getHomeworksBySubject(subject), ModelUtils.HW_FIELDS_TO_STRING_LIST);
        }

        public void addHomework(@NotNull LocalDateTime deadline, @NotNull String subject,
                                int regularity, String description, String howToSend,
                                double expectedScore, @NotNull ArrayList<String> materials) {
            int id = settingsDatabase.getTotalNumberOfHW();
            Homework homework = new Homework(deadline, subject, regularity, description,
                    howToSend, expectedScore, id, materials);
            homeworkDatabase.addHomework(homework);
            settingsDatabase.setTotalNumberOfHW(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }

            if (settingsDatabase.getGoogleCalendarSync()) {
                calendarExporter.addHomework(homework);
            }
        }

        public void deleteHomeworkById(int id) {
            homeworkDatabase.deleteHomeworkById(id);
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetHomeworksInGoogleCalendar();
            }
        }

        public void setHomeworkScoreById(int id, int score) {
            homeworkDatabase.setScoreById(id, score);
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetHomeworksInGoogleCalendar();
            }
        }

        public void setHomeworkDeferralById(int id, int deferral) {
            homeworkDatabase.setDeferralById(id, deferral);
        }

        public void editHomeworkById(int id, @NotNull LocalDateTime deadline, @NotNull String subject,
                                     int regularity, String description, String howToSend,
                                     double expectedScore, @NotNull ArrayList<String> materials) {
            homeworkDatabase.editHomeworkById(deadline, subject, regularity, description, howToSend,
                    expectedScore, id, materials);
            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetHomeworksInGoogleCalendar();
            }
        }

        @NotNull
        public Bundle getHomeworkById(int id) {
            return homeworkDatabase.getHomeworkById(id).getDeconstructed();
        }

        @NotNull
        public List<List<String>> getDeadlinesByDay(@NotNull LocalDate date) {
            return ModelUtils.map(homeworkDatabase.getHomeworksByDay(date), ModelUtils.HW_DEADLINE_FIELDS_TO_STRING_LIST);
        }

        public void generateHomeworks() {
            LocalDate today = LocalDate.now();
            for (Homework homework : homeworkDatabase.getHomeworksByDay(today)) {
                if (homework.getRegularity() != 0) {
                    int id = settingsDatabase.getTotalNumberOfHW();
                    homeworkDatabase.addHomework(homework.generateNewHomeworkById(id));
                    settingsDatabase.setTotalNumberOfHW(++id);
                    if (settingsDatabase.getGoogleCalendarSync()) {
                        calendarExporter.addHomework(homework);
                    }
                }
            }
        }

        public void resetHomeworksInGoogleCalendar() {
            calendarExporter.resetHomeworks(homeworkDatabase.getActualHomeworks());
        }
    }

    public class ScheduleController {
        private ClassEntriesDatabaseController classEntriesDatabase;

        public ScheduleController(Context context) {
            classEntriesDatabase = new ClassEntriesDatabaseController(context);
        }

        @NotNull
        public List<List<String>> getScheduleByDay(LocalDate day) {
            WeekParityEnum weekParity = WeekParityEnum.values()[day.getWeekOfWeekyear() % 2];
            if (settingsDatabase.getWeekParity()) {
                weekParity = weekParity.inverse();
            }

            List<ClassEntry> classes = classEntriesDatabase.getDaySchedule(day.getDayOfWeek() - 1,
                    weekParity);
            List<Exam> exams = examController.examDatabase.getExamsByDay(day);
            List<ScheduleEntry> scheduleEntries = ListUtils.union(exams, classes);
            Collections.sort(scheduleEntries);

            return ModelUtils.map(scheduleEntries, ModelUtils.SCHEDULE_ENTRY_TO_SCHEDULE_DESCRIPTION);
        }

        public void addClassEntry(@NotNull String subject, int dayOfWeek, int hour,
                                  int minute, @NotNull WeekParityEnum weekParity,
                                  @NotNull String auditorium, @NotNull String teacher) {
            int id = settingsDatabase.getTotalNumberOfScheduleEntries();
            ClassEntry aClass = new ClassEntry(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
            classEntriesDatabase.addClassEntry(aClass);
            settingsDatabase.setTotalNumberOfScheduleEntries(++id);

            if (settingsDatabase.getGoogleCalendarSync()) {
                resetClassEntriesInGoogleCalendar();
            }
        }

        public void deleteClassEntryById(int id) {
            classEntriesDatabase.deleteClassEntryById(id);
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetClassEntriesInGoogleCalendar();
            }
        }

        public void editClassEntryById(int id, @NotNull String subject, int dayOfWeek,
                                       int hour, int minute, @NotNull WeekParityEnum weekParity,
                                       @NotNull String auditorium, @NotNull String teacher) {
            classEntriesDatabase.editClassEntryById(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetClassEntriesInGoogleCalendar();
            }
        }

        @NotNull
        public Bundle getClassEntryById(int id) {
            return classEntriesDatabase.getClassEntryById(id).getDeconstructed();
        }

        public void resetClassEntriesInGoogleCalendar() {
            LocalDate endTermDate = settingsDatabase.getEndTermDate();
            calendarExporter.resetClassEntries(classEntriesDatabase.getAllClassEntries(), endTermDate);
        }

    }

    public class ExamController {
        private ExamDatabaseController examDatabase;

        public ExamController(Context context) {
            examDatabase = new ExamDatabaseController(context);
        }

        @NotNull
        public List<List<String>> getExamsBySubject(@NotNull String subject) {
            return ModelUtils.map(examDatabase.getExamsBySubject(subject), ModelUtils.EXAM_FIELDS_TO_STRING_LIST);
        }

        @NotNull
        public List<List<String>> getExamsByDay(@NotNull LocalDate date) {
            return ModelUtils.map(examDatabase.getExamsByDay(date), ModelUtils.EXAM_FIELDS_TO_STRING_LIST);
        }

        public void addExam(@NotNull LocalDateTime date, @NotNull String subject,
                            @NotNull ExamEnum examEnum, String description) {
            int id = settingsDatabase.getTotalNumberOfWorks();
            Exam exam = new Exam(subject, description, date, examEnum, id);
            examDatabase.addExam(exam);
            settingsDatabase.setTotalNumberOfWorks(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetExamsInGoogleCalendar();
            }
        }

        public void deleteExamsById(int id) {
            examDatabase.deleteExamById(id);
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetExamsInGoogleCalendar();
            }
        }

        public void setExamAcceptedById(int id, boolean isAccepted) {
            examDatabase.setAcceptedById(id, isAccepted);
        }

        public void editExamById(int id, @NotNull LocalDateTime date, @NotNull String subject,
                                 @NotNull ExamEnum examEnum, String description) {
            examDatabase.editExamById(subject, description, date, examEnum, id);
            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
            if (settingsDatabase.getGoogleCalendarSync()) {
                resetExamsInGoogleCalendar();
            }
        }

        @NotNull
        public Bundle getExamById(int id) {
            return examDatabase.getExamById(id).getDeconstructed();
        }

        public void resetExamsInGoogleCalendar() {
            calendarExporter.resetExamEntries(examDatabase.getAllExams());
        }
    }

    public class StudyMaterialController {
        private StudyMaterialDatabaseController studyMaterialDatabase;

        public StudyMaterialController(Context context) {
            studyMaterialDatabase = new StudyMaterialDatabaseController(context);
        }

        public void addUpdatableStudyMaterial(@NotNull String name, @NotNull String subject, int term)
                throws WebStudyMaterialException, StudyMaterialsSourceAccessException {
            int id = settingsDatabase.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(name, subject, term,
                    appDirectory.getAbsolutePath(), 0, id);
            File studyMaterialFile = new File(appDirectory.getAbsolutePath(), name);

            if (studyMaterialFile.exists()) {
                throw new WebStudyMaterialException("The study material already exists.",
                        ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST.apply(studyMaterial));
            }
            updateStudyMaterial(studyMaterial);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settingsDatabase.setTotalNumberOfStudyMaterials(++id);

            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public void addLocalStudyMaterial(@NotNull String name, @NotNull String subject,
                                          int term, @NotNull String path) {
            int id = settingsDatabase.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(name, subject, term, path, -1, id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settingsDatabase.setTotalNumberOfStudyMaterials(++id);

            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public List<List<String>> getStudyMaterialsBySubject(@NotNull String subject) {
            return ModelUtils.map(studyMaterialDatabase.getStudyMaterialsBySubject(subject),
                    ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST);
        }

        @NotNull
        public List<List<String>> getStudyMaterialsByTerm(int term) {
            return ModelUtils.map(studyMaterialDatabase.getStudyMaterialsByTerm(term),
                    ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST);
        }

        @NotNull
        public Bundle getStudyMaterialById(int id) {
            return studyMaterialDatabase.getStudyMaterialById(id).getDeconstructed();
        }

        public void deleteStudyMaterialById(int id) {
            studyMaterialDatabase.deleteStudyMaterialById(id);
        }

        public void editStudyMaterialById(int id, @NotNull String subject, int term) {
            studyMaterialDatabase.editStudyMaterialById(id, subject, term);
            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public void updateStudyMaterials() throws StudyMaterialsUpdatingException, UrlDownloadingException {
            StudyMaterialsUpdater studyMaterialsUpdater;
            StudyMaterial[] studyMaterials = studyMaterialDatabase.getUpdatableStudyMaterials()
                    .toArray(new StudyMaterial[0]);

            try {
                studyMaterialsUpdater = new StudyMaterialsUpdater();
                studyMaterialsUpdater.execute(studyMaterials);
                studyMaterialsUpdater.get(1, TimeUnit.MINUTES);
            } catch (Exception exception) {
                throw new UrlDownloadingException();
            }

            StudyMaterialsUpdatingException exception = studyMaterialsUpdater.getError();
            if (exception != null) {
                throw exception;
            }
        }

        @NotNull
        public List<String>[] getAvailableStudyMaterialsList() throws StudyMaterialsSourceAccessException {
            WebStudyMaterialsListGetter webStudyMaterialsListGetter = new WebStudyMaterialsListGetter();
            webStudyMaterialsListGetter.execute();
            AsyncTaskResult<List<String>[], StudyMaterialsSourceAccessException> result;

            try {
                result = webStudyMaterialsListGetter.get(1, TimeUnit.MINUTES);
            } catch (Exception exception) {
                throw new StudyMaterialsSourceAccessException();
            }

            StudyMaterialsSourceAccessException exception = result.getError();
            if (exception == null) {
                return result.getResult();
            } else {
                throw exception;
            }
        }

        @NotNull
        public List<File> searchForStudyMaterials(String query) throws UrlDownloadingException {
            GoogleSearchRequester googleSearchRequester = new GoogleSearchRequester();
            googleSearchRequester.execute(query, appDirectory.getAbsolutePath());
            AsyncTaskResult<List<File>, UrlDownloadingException> result;

            try {
                result = googleSearchRequester.get(1, TimeUnit.MINUTES);
            } catch (Exception exception) {
                throw new UrlDownloadingException();
            }

            UrlDownloadingException exception = result.getError();
            if (exception == null) {
                return result.getResult();
            } else {
                throw exception;
            }

        }

        private void updateStudyMaterial(StudyMaterial studyMaterial)
                throws StudyMaterialsSourceAccessException, WebStudyMaterialException {
            StudyMaterialsUpdater studyMaterialsUpdater;
            try {
                studyMaterialsUpdater = new StudyMaterialsUpdater();
                studyMaterialsUpdater.execute(studyMaterial);
                studyMaterialsUpdater.get(1, TimeUnit.MINUTES);
            } catch (Exception exception) {
                throw new WebStudyMaterialException("Unable to download study material.",
                        ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST.apply(studyMaterial));
            }

            StudyMaterialsUpdatingException exception = studyMaterialsUpdater.getError();
            if (exception != null) {
                throw exception.getErrors().get(0);
            }
        }
    }


}