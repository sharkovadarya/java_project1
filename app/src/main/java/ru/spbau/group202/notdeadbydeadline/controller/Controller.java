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
import ru.spbau.group202.notdeadbydeadline.model.DetailedEntry;
import ru.spbau.group202.notdeadbydeadline.model.DetailedTimedEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;
import ru.spbau.group202.notdeadbydeadline.model.SubjectCredit;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;
import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.ExamEnum;
import ru.spbau.group202.notdeadbydeadline.model.utilities.NoSuchStudyMaterialException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialExistsException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialSourceAccessException;
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

        public void setSubjectCreditForm(@NotNull String subject, @NotNull CreditFormEnum credit) {
            subjectDatabase.setSubjectCreditForm(subject, credit);
        }
    }

    public static class HomeworkController {
        private static HomeworkDatabaseController homeworkDatabase;

        @NotNull
        public static List<List<String>> getHomeworksBySubject(@NotNull String subject) {
            return getEntriesDetailList(homeworkDatabase.getHomeworksBySubject(subject));
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
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
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
            homeworkDatabase.editHomeworkById(deadline, subject, regularity, description, howToSend,
                    expectedScore, id, materials);
            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static Bundle getHomeworkById(int id) {
            return homeworkDatabase.getHomeworkById(id).getDeconstructed();
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
            scheduleDatabase.editScheduleEntryById(subject, dayOfWeek, hour, minute,
                    weekParity, auditorium, teacher, id);
        }

        @NotNull
        public static Bundle getScheduleEntryById(int id) {
            return scheduleDatabase.getScheduleEntryById(id).getDeconstructed();
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
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
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
            examDatabase.editExamById(subject, description, date, examEnum, id);
            if (subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static Bundle getExamById(int id) {
            return examDatabase.getExamById(id).getDeconstructed();
        }
    }

    public static class StudyMaterialsController {
        private static final String STUDY_MATERIAL_SOURCE = "https://cdkrot.me/";
        private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
        private static final int NUMBER_OF_SEARCH_TRIES = 5;
        private static String urlContent;
        private static StudyMaterialDatabaseController studyMaterialDatabase;

        public static void addUpdatableStudyMaterial(@NotNull String name, @NotNull String subject, int term)
                throws UrlDownloadingException, MalformedURLException, NoSuchStudyMaterialException,
                StudyMaterialSourceAccessException, StudyMaterialExistsException {
            File studyMaterialFile = new File(appDirectory.getAbsolutePath(), name);
            if (studyMaterialFile.exists()) {
                throw new StudyMaterialExistsException();
            }

            int id = settings.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(name, subject, term,
                    appDirectory.getAbsolutePath(), 0, id);
            updateUrlContent();
            updateStudyMaterial(studyMaterial);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settings.saveTotalNumberOfStudyMaterials(++id);

            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public static void addLocalStudyMaterial(@NotNull String name, @NotNull String subject,
                                                 int term, @NotNull String path) {
            int id = settings.getTotalNumberOfStudyMaterials();
            StudyMaterial studyMaterial = new StudyMaterial(name, subject, term, path, -1, id);
            studyMaterialDatabase.addStudyMaterial(studyMaterial);
            settings.saveTotalNumberOfStudyMaterials(++id);

            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        @NotNull
        public static List<List<String>> getStudyMaterialsBySubject(@NotNull String subject) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialsBySubject(subject));
        }

        @NotNull
        public static List<List<String>> getStudyMaterialsByTerm(int term) {
            return getEntriesDetailList(studyMaterialDatabase.getStudyMaterialsByTerm(term));
        }

        @NotNull
        public static Bundle getStudyMaterialById(int id) {
            return studyMaterialDatabase.getStudyMaterialById(id).getDeconstructed();
        }

        public static void deleteStudyMaterialById(int id) {
            studyMaterialDatabase.deleteStudyMaterialById(id);
        }

        public static void editStudyMaterialById(int id, @NotNull String subject, int term)
                throws MalformedURLException, UrlDownloadingException {
            studyMaterialDatabase.editStudyMaterialById(id, subject, term);
            if (!subjectList.add(subject)) {
                subjectDatabase.addSubject(subject, CreditFormEnum.NOT_STATED, -1);
            }
        }

        public static void updateStudyMaterials() throws StudyMaterialSourceAccessException,
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
                throw new StudyMaterialsUpdatingException(getEntriesDetailList(failedToUpdate));
            }
        }

        @NotNull
        public static List<String>[] getAvailableStudyMaterials() throws StudyMaterialSourceAccessException {
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
                        if(noTermMatcher.find()) {
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
        public static List<File> searchForStudyMaterials(String query) throws UrlDownloadingException {
            ArrayList<File> results = new ArrayList<>();
            String searchURL = GOOGLE_SEARCH_URL + "?q=" + "filetype:pdf " + "+"
                    + query.replace(" ", "+") + "&num="
                    + Integer.toString(NUMBER_OF_SEARCH_TRIES);

            try {
                Document html = Jsoup.connect(searchURL).userAgent("Mozilla").timeout(5000).get();
                Elements links = html.select("h3.r > a");
                for(Element link : links) {
                    String stringUrl = link.attr("href").replace("/url?q=", "").replaceAll("\\.pdf.*", ".pdf");
                    URL url = new URL(link.attr("href").replace("/url?q=", ""));
                    File searchResultsFolder = new File(appDirectory + File.separator + "search");
                    searchResultsFolder.mkdir();
                    File foundMaterial = new File(searchResultsFolder.getAbsolutePath(), link.text());
                    FileUtils.copyURLToFile(url, foundMaterial);
                    results.add(foundMaterial);
                }
            }
            catch (IOException exception) {
                throw new UrlDownloadingException();
            }

            return results;
        }

        private static void updateStudyMaterial(StudyMaterial studyMaterial)
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

        private static void updateUrlContent() throws StudyMaterialSourceAccessException {
            try {
                URL url = new URL(STUDY_MATERIAL_SOURCE);
                FileUtils.copyURLToFile(url, new File(appDirectory, STUDY_MATERIAL_SOURCE));
                urlContent = FileUtils.readFileToString(new File(url.toURI()), "UTF-8");
            } catch (Exception exception) {
                throw new StudyMaterialSourceAccessException();
            }
        }
    }

    public static void createDatabases(@NotNull Context context) throws StudyMaterialSourceAccessException,
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