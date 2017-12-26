package ru.spbau.group202.notdeadbydeadline.Model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SubjectCredit {
    private String subject;

    SubjectCredit(@NotNull String subject) {
        this.subject = subject;
    }

    @NotNull
    public List<String> calculateProgress(@NotNull List<Homework> homeworks) {
        int totalPoints = 0;
        int earnedPoints = 0;

        for (Homework homework : homeworks){
            if(homework.getActualScore() != -1){
                earnedPoints += homework.getActualScore();
                totalPoints += homework.getExpectedScore();
            }
        }

        double percent = totalPoints == 0 ? 1 : (double) earnedPoints / totalPoints;
        return Arrays.asList("not stated", Double.toString(percent));
    }
}
