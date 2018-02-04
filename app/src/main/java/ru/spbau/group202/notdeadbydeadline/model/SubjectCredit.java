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
    public List<String> calculateProgress(@NotNull List<Homework> homeworks, @NotNull List<Work> works) {
        HashMap<WorkEnum, Integer> totalNumber = new HashMap<>();
        HashMap<WorkEnum, Integer> numberOfPassed = new HashMap<>();

        for (Work work : works) {
            WorkEnum kind = work.getKind();
            totalNumber.put(kind, totalNumber.get(kind) + 1);
            if (work.isAccepted()) {
                numberOfPassed.put(kind, numberOfPassed.get(kind) + 1);
            }
        }

        double passedTestsPercent = totalNumber.get(WorkEnum.TEST) == 0 ? 1 :
                (double) numberOfPassed.get(WorkEnum.TEST) / totalNumber.get(WorkEnum.TEST);
        double passedExamsPercent = totalNumber.get(WorkEnum.EXAM) == 0 ? 1 :
                (double) numberOfPassed.get(WorkEnum.EXAM) / totalNumber.get(WorkEnum.EXAM);
        String testCredit = passedTestsPercent == 1.0 ? "Passed class" : "Failed class";
        String examsCredit = passedExamsPercent == 1.0 ? "Passed class" : "Failed class";
        int numberOfNotPassedTests = totalNumber.get(WorkEnum.TEST) - numberOfPassed.get(WorkEnum.TEST);
        int numberOfNotPassedExams = totalNumber.get(WorkEnum.EXAM) - numberOfPassed.get(WorkEnum.EXAM);

        List<String> result = Arrays.asList(Double.toString(passedTestsPercent), testCredit,
                Integer.toBinaryString(numberOfNotPassedTests), Double.toString(passedExamsPercent), examsCredit,
                Integer.toBinaryString(numberOfNotPassedExams));
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