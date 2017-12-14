package ru.spbau.group202.notdeadbydeadline.Model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubjectCredit {
    private String subject;

    SubjectCredit(@NotNull String subject) {
        this.subject = subject;
    }

    @NotNull
    public List<String> calculateProgress(@NotNull List<Homework> homeworks) {
        return null;
    }
}
