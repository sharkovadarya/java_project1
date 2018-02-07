package ru.spbau.group202.notdeadbydeadline.model;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SubjectCredit {
    private String subject;

    public SubjectCredit(@NotNull String subject) {
        this.subject = subject;
    }

    @NotNull
    public List<String> calculateProgress(@NotNull List<Homework> homeworks, @NotNull List<Exam> exams) {
        HashMap<ExamEnum, Integer> totalNumber = new HashMap<>();
        HashMap<ExamEnum, Integer> numberOfPassed = new HashMap<>();

        for (Exam exam : exams) {
            ExamEnum kind = exam.getExamType();
            totalNumber.put(kind, totalNumber.get(kind) + 1);
            if (exam.isAccepted()) {
                numberOfPassed.put(kind, numberOfPassed.get(kind) + 1);
            }
        }

        double passedTestsPercent = totalNumber.get(ExamEnum.TEST) == 0 ? 1 :
                (double) numberOfPassed.get(ExamEnum.TEST) / totalNumber.get(ExamEnum.TEST);
        double passedExamsPercent = totalNumber.get(ExamEnum.FINAL_EXAM) == 0 ? 1 :
                (double) numberOfPassed.get(ExamEnum.FINAL_EXAM) / totalNumber.get(ExamEnum.FINAL_EXAM);
        String testCredit = passedTestsPercent == 1.0 ? "Passed class" : "Failed class";
        String examsCredit = passedExamsPercent == 1.0 ? "Passed class" : "Failed class";
        int numberOfNotPassedTests = totalNumber.get(ExamEnum.TEST) - numberOfPassed.get(ExamEnum.TEST);
        int numberOfNotPassedExams = totalNumber.get(ExamEnum.FINAL_EXAM) - numberOfPassed.get(ExamEnum.FINAL_EXAM);

        List<String> result = Arrays.asList(Double.toString(passedTestsPercent), testCredit,
                Integer.toString(numberOfNotPassedTests), Double.toString(passedExamsPercent), examsCredit,
                Integer.toString(numberOfNotPassedExams));
        return ListUtils.union(calculateHomeworkProgress(homeworks), result);
    }

    @NotNull
    protected List<String> calculateHomeworkProgress(@NotNull List<Homework> homeworks) {
        double totalPoints = 0;
        double earnedPoints = 0;

        for (Homework homework : homeworks) {
            if (homework.getActualScore() != -1) {
                earnedPoints += homework.getActualScore();
                totalPoints += homework.getExpectedScore();
            }
        }

        double percent = totalPoints == 0 ? 1 : earnedPoints / totalPoints;
        return Arrays.asList("not stated", Double.toString(percent));
    }
}