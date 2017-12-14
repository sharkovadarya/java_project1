package ru.spbau.group202.notdeadbydeadline.Model;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreditByPercent extends SubjectCredit {
    double percentForCredit;

    CreditByPercent(@NotNull String subject, double percentForCredit) {
        super(subject);
        this.percentForCredit = percentForCredit;
    }

    @Override
    public List<String> calculateProgress(@NotNull List<Homework> homeworks) {
        return null;
    }
}
