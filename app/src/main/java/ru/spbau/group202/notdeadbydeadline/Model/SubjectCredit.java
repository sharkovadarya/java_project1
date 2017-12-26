package ru.spbau.group202.notdeadbydeadline.Model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SubjectCredit {
    private String subject;

    public SubjectCredit(@NotNull String subject) {
        this.subject = subject;
    }

    @NotNull
    public List<String> calculateProgress(@NotNull List<Homework> homeworks) {
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
