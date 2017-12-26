package ru.spbau.group202.notdeadbydeadline.Model;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CreditByAcceptedHomeworks extends SubjectCredit {
    public CreditByAcceptedHomeworks(@NotNull String subject) {
        super(subject);
    }

    @Override
    public List<String> calculateProgress(@NotNull List<Homework> homeworks) {
        int numberOfCheckedHomeworks = 0;
        int numberOfAcceptedHomeworks = 0;

        for (Homework homework : homeworks) {
            if (homework.getActualScore() != -1) {
                numberOfCheckedHomeworks++;
                if (homework.hasPassed()) {
                    numberOfAcceptedHomeworks++;
                }
            }
        }
        double percent = numberOfCheckedHomeworks == 0 ? 1 : (double) numberOfAcceptedHomeworks / numberOfCheckedHomeworks;
        String credit = percent == 1.0 ? "Passed class" : "Failed class";
        int numberOfNotAcceptedHomeworks = numberOfCheckedHomeworks - numberOfAcceptedHomeworks;

        return Arrays.asList("by accepted homeworks", Double.toString(percent), credit,
                Integer.toString(numberOfNotAcceptedHomeworks));
    }
}
