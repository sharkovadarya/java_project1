package ru.spbau.group202.notdeadbydeadline.controller;

import android.content.Context;
import android.os.Bundle;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbau.group202.notdeadbydeadline.model.CreditFormEnum;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.Class;
import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;
import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.ExamEnum;
import ru.spbau.group202.notdeadbydeadline.model.utilities.Function;
import ru.spbau.group202.notdeadbydeadline.model.utilities.ModelUtils;
import ru.spbau.group202.notdeadbydeadline.model.utilities.NoSuchStudyMaterialException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialExistsException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialSourceAccessException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsUpdatingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UrlDownloadingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.UnrecognizedCreditFormException;

public class Controller {
    private static Controller instance;
    private File appDirectory;
    private StoredDataController settingsDatabase;
    private SubjectDatabaseController subjectDatabase;
    private ExamController examController;
    private HomeworkController homeworkController;
    private ScheduleController scheduleController;
    private StudyMaterialController studyMaterialController;
    private Set<String> subjectList;

    private Controller(@NotNull Context context) {
        homeworkController = new HomeworkController(context);
        subjectDatabase = new SubjectDatabaseController(context);
        scheduleController = new ScheduleController(context);
        examController = new ExamController(context);
        studyMaterialController = new StudyMaterialController(context);
        settingsDatabase = new StoredDataController(context);
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
        settingsDatabase.saveWeekPairity(isInversed);
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
            return map(homeworkDatabase.getHomeworksBySubject(subject), ModelUtils.HW_FIELDS_TO_STRING_LIST);
        }

        public void addHomework(@NotNull LocalDateTime deadline, @NotNull String subject,
                                int regularity, String description, String howToSend,
                                double expectedScore, @NotNull ArrayList<String> materials) {
            int id = settingsDatabase.getTotalNumberOfHW();
            Homework homework = new Homework(deadline, subject, regularity, description,
                    howToSend, expectedScore, id, materials);
            homeworkDatabase.addHomework(homework);
            settingsDatabase.saveTotalNumberOfHW(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public void deleteHomeworkById(int id) {
            homeworkDatabase.deleteHomeworkById(id);
        }

        public void setHomeworkScoreById(int id, int score) {
            homeworkDatabase.setScoreById(id, score);
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
        }

        @NotNull
        public Bundle getHomeworkById(int id) {
            return homeworkDatabase.getHomeworkById(id).getDeconstructed();
        }

        @NotNull
        public List<List<String>> getDeadlinesByDay(@NotNull LocalDate date) {
            return map(homeworkDatabase.getHomeworksByDay(date), ModelUtils.HW_DEADLINE_FIELDS_TO_STRING_LIST);
        }

        public void generateHomeworks() {
            LocalDate today = LocalDate.now();
            for (Homework homework : homeworkDatabase.getHomeworksByDay(today)) {
                if (homework.getRegularity() != 0) {
                    int id = settingsDatabase.getTotalNumberOfHW();
                    homeworkDatabase.addHomework(homework.generateNewHomeworkById(id));
                    settingsDatabase.saveTotalNumberOfHW(++id);
                }
            }
        }
    }

    public class ScheduleController {
        private ClassDatabaseController classDatabase;

        public ScheduleController(Context context) {
            classDatabase = new ClassDatabaseController(context);
        }

        @NotNull
        public List<List<String>> getScheduleByDay(LocalDate day) {
            WeekParityEnum weekParity = WeekParityEnum.values()[day.getWeekOfWeekyear() % 2];
            if (settingsDatabase.getParityOfWeek()) {
                weekParity = weekParity.inverse();
            }

            List<Class> classes = classDatabase.getDaySchedule(day.getDayOfWeek() - 1,
                    weekParity);
            List<Exam> exams = examController.examDatabase.getExamsByDay(day);
            List<ScheduleEntry> scheduleEntries = ListUtils.union(exams, classes);
            Collections.sort(scheduleEntries);

            return map(scheduleEntries, ModelUtils.SCHEDULE_ENTRY_TO_SCHEDULE_DESCRIPTION);
        }

        public void addClass(@NotNull String subject, int dayOfWeek, int hour,
                                     int minute, @NotNull WeekParityEnum weekParity,
                                     @NotNull String auditorium, @NotNull String teacher) {
            int id = settingsDatabase.getTotalNumberOfScheduleEntries();
            Class aClass = new Class(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
            classDatabase.addClass(aClass);
            settingsDatabase.saveTotalNumberOfScheduleEntries(++id);
        }

        public void deleteClassById(int id) {
            classDatabase.deleteClassById(id);
        }

        public void editClassById(int id, @NotNull String subject, int dayOfWeek,
                                          int hour, int minute, @NotNull WeekParityEnum weekParity,
                                          @NotNull String auditorium, @NotNull String teacher) {
            classDatabase.editClassById(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
        }

        @NotNull
        public Bundle getClassById(int id) {
            return classDatabase.getClassById(id).getDeconstructed();
        }
    }

    public class ExamController {
        private ExamDatabaseController examDatabase;

        public ExamController(Context context) {
            examDatabase = new ExamDatabaseController(context);
        }

        @NotNull
        public List<List<String>> getExamsBySubject(@NotNull String subject) {
            return map(examDatabase.getExamsBySubject(subject), ModelUtils.EXAM_FIELDS_TO_STRING_LIST);
        }

        @NotNull
        public List<List<String>> getExamsByDay(@NotNull LocalDate date) {
            return map(examDatabase.getExamsByDay(date), ModelUtils.EXAM_FIELDS_TO_STRING_LIST);
        }

        public void addExam(@NotNull LocalDateTime date, @NotNull String subject,
                            @NotNull ExamEnum examEnum, String description) {
            int id = settingsDatabase.getTotalNumberOfWorks();
            Exam exam = new Exam(subject, description, date, examEnum, id);
            examDatabase.addExam(exam);
            settingsDatabase.saveTotalNumberOfWorks(++id);

            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public void deleteExamsById(int id) {
            examDatabase.deleteExamById(id);
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
        }

        @NotNull
        public Bundle getExamById(int id) {
            return examDatabase.getExamById(id).getDeconstructed();
        }
    }

    public class StudyMaterialController {
        private final String STUDY_MATERIAL_SOURCE = "https://cdkrot.me/";
        private final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
        private final int NUMBER_OF_SEARCH_TRIES = 5;
        private String urlContent;
        private StudyMaterialDatabaseController studyMaterialDatabase;

        public StudyMaterialController(Context context) {
            studyMaterialDatabase = new StudyMaterialDatabaseController(context);
        }

        public void addUpdatableStudyMaterial(@NotNull String name, @NotNull String subject, int term)
                throws UrlDownloadingException, MalformedURLException, NoSuchStudyMaterialException,
                StudyMaterialSourceAccessException, StudyMaterialExistsException {
            File studyMaterialFile = new File(appDirectory.getAbsolutePath(), name);
            if (studyMaterialFile.exists()) {
                throw new StudyMaterialExistsException();
            }

            int id = settingsDatabase.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(name, subject, term,
                    appDirectory.getAbsolutePath(), 0, id);
            updateUrlContent();
            updateStudyMaterial(studyMaterial);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settingsDatabase.saveTotalNumberOfStudyMaterials(++id);

            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public void addLocalStudyMaterial(@NotNull String name, @NotNull String subject,
                                          int term, @NotNull String path) {
            int id = settingsDatabase.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(name, subject, term, path, -1, id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settingsDatabase.saveTotalNumberOfStudyMaterials(++id);

            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public List<List<String>> getStudyMaterialsBySubject(@NotNull String subject) {
            return map(studyMaterialDatabase.getStudyMaterialsBySubject(subject),
                    ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST);
        }

        @NotNull
        public List<List<String>> getStudyMaterialsByTerm(int term) {
            return map(studyMaterialDatabase.getStudyMaterialsByTerm(term),
                    ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST);
        }

        @NotNull
        public Bundle getStudyMaterialById(int id) {
            return studyMaterialDatabase.getStudyMaterialById(id).getDeconstructed();
        }

        public void deleteStudyMaterialById(int id) {
            studyMaterialDatabase.deleteStudyMaterialById(id);
        }

        public void editStudyMaterialById(int id, @NotNull String subject, int term)
                throws MalformedURLException, UrlDownloadingException {
            studyMaterialDatabase.editStudyMaterialById(id, subject, term);
            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public void updateStudyMaterials() throws StudyMaterialSourceAccessException,
                StudyMaterialsUpdatingException {
            updateUrlContent();
            List<StudyMaterial> studyMaterials = studyMaterialDatabase.getUpdatableStudyMaterials();
            boolean allUpdated = true;
            ArrayList<StudyMaterial> failedToUpdate = new ArrayList<>();
            for (StudyMaterial studyMaterial : studyMaterials) {
                try {
                    updateStudyMaterial(studyMaterial);
                } catch (Exception exception) {
                    allUpdated = false;
                    failedToUpdate.add(studyMaterial);
                }
            }

            if (!allUpdated) {
                throw new StudyMaterialsUpdatingException(map(failedToUpdate,
                        ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST));
            }
        }

        @NotNull
        public List<String>[] getAvailableStudyMaterials() throws StudyMaterialSourceAccessException {
            @SuppressWarnings("unchecked")
            ArrayList<String>[] materialsByTerms = new ArrayList[7];
            for (int term = 0; term < materialsByTerms.length; term++) {
                materialsByTerms[term] = new ArrayList<>();
            }

            try {
                Document html = Jsoup.connect(STUDY_MATERIAL_SOURCE).get();
                Elements materialsUrl = html.select("a[href$=.pdf]");

                Pattern urlPattern = Pattern.compile(".+/(.+/.*?term(\\d).+)/\\d+\\.pdf");
                Pattern noTermPattern = Pattern.compile(STUDY_MATERIAL_SOURCE + "(.+)/\\d+\\.pdf");
                for (Element materialUrl : materialsUrl) {
                    String absUrl = materialUrl.attr("abs:href");
                    Matcher urlMatcher = urlPattern.matcher(absUrl);
                    if (urlMatcher.find()) {
                        materialsByTerms[Integer.valueOf(urlMatcher.group(2))].add(urlMatcher.group(1));
                    } else {
                        Matcher noTermMatcher = noTermPattern.matcher(absUrl);
                        if (noTermMatcher.find()) {
                            materialsByTerms[0].add(noTermMatcher.group(1));
                        }

                    }
                }
            } catch (IOException exception) {
                throw new StudyMaterialSourceAccessException();
            }

            return materialsByTerms;
        }

        @NotNull
        public List<File> searchForStudyMaterials(String query) throws UrlDownloadingException {
            ArrayList<File> results = new ArrayList<>();
            String searchURL = GOOGLE_SEARCH_URL + "?q=" + "filetype:pdf " + "+"
                    + query.replace(" ", "+") + "&num="
                    + Integer.toString(NUMBER_OF_SEARCH_TRIES);

            try {
                Document html = Jsoup.connect(searchURL).userAgent("Mozilla").timeout(5000).get();
                Elements links = html.select("h3.r > a");
                for (Element link : links) {
                    String stringUrl = link.attr("href").replace("/url?q=", "").replaceAll("\\.pdf.*", ".pdf");
                    URL url = new URL(stringUrl);
                    File searchResultsFolder = new File(appDirectory + File.separator + "search");
                    searchResultsFolder.mkdir();
                    File foundMaterial = new File(searchResultsFolder.getAbsolutePath(), link.text());
                    FileUtils.copyURLToFile(url, foundMaterial);
                    results.add(foundMaterial);
                }
            } catch (IOException exception) {
                throw new UrlDownloadingException();
            }

            return results;
        }

        private void updateStudyMaterial(StudyMaterial studyMaterial)
                throws NoSuchStudyMaterialException, MalformedURLException, UrlDownloadingException {
            Pattern urlPattern = Pattern.compile("href=\"(/.+/" + studyMaterial.getName() +
                    "/(\\d+)\\.pdf)\"");
            Matcher urlMatcher = urlPattern.matcher(urlContent);

            if (!urlMatcher.find()) {
                throw new NoSuchStudyMaterialException();
            }
            int newVersion = Integer.valueOf(urlMatcher.group(2));
            if (newVersion > studyMaterial.getVersion()) {
                URL studyMaterialUrl = new URL(STUDY_MATERIAL_SOURCE + urlMatcher.group(1));
                File studyMaterialFile = new File(studyMaterial.getPath(), studyMaterial.getName());
                try {
                    FileUtils.copyURLToFile(studyMaterialUrl, studyMaterialFile);
                } catch (IOException exception) {
                    throw new UrlDownloadingException();
                }
                studyMaterial.setVersion(Integer.valueOf(urlMatcher.group(2)));
            }
        }

        private void updateUrlContent() throws StudyMaterialSourceAccessException {
            try {
                URL url = new URL(STUDY_MATERIAL_SOURCE);
                FileUtils.copyURLToFile(url, new File(appDirectory, STUDY_MATERIAL_SOURCE));
                urlContent = FileUtils.readFileToString(new File(url.toURI()), "UTF-8");
            } catch (Exception exception) {
                throw new StudyMaterialSourceAccessException();
            }
        }
    }


    @NotNull
    private <T, U> List<U> map(@NotNull List<T> list, @NotNull Function<T, U> function) {
        List<U> result = new ArrayList<>();
        for(T element : list) {
            result.add(function.apply(element));
        }
        return result;
    }
}