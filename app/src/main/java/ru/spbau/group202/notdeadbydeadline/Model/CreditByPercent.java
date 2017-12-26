package ru.spbau.group202.notdeadbydeadline.Model;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CreditByPercent extends SubjectCredit {
    private double percentForCredit;

    public CreditByPercent(@NotNull String subject, double percentForCredit) {
        super(subject);
        this.percentForCredit = percentForCredit;
    }

    @Override
    public List<String> calculateProgress(@NotNull List<Homework> homeworks) {
        double totalPoints = 0;
        double earnedPoints = 0;

        for (Homework homework : homeworks){
            if(homework.getActualScore() != -1){
                earnedPoints += homework.getActualScore();
                totalPoints += homework.getExpectedScore();
            }
        }

        double percent = totalPoints == 0 ? 1 : earnedPoints / totalPoints;
        String result = percent >= percentForCredit ? "Class passed" : "Class failed";
        double pointsForCredit = percentForCredit * totalPoints - earnedPoints;
        return Arrays.asList("by percent", Double.toString(percent), result,
                pointsForCredit < 0 ? "0" : Double.toString(pointsForCredit));
    }
}
