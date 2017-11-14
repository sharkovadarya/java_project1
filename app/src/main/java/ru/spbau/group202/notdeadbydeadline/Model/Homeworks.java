package ru.spbau.group202.notdeadbydeadline.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Homeworks {

    private ArrayList<Homework> homeworks = new ArrayList<>();

    public void addHomework(int year, int month, int day,
                            int hour, int minute, String subject,
                            boolean isRegular, String description,
                            String howToSend, int expectedScore) {
        homeworks.add(new Homework(year, month, day, hour, minute, subject,
                isRegular, description, howToSend, expectedScore));
    }

    // TODO this is unnecessary tbh
    public ArrayList<String> getDeadlines() {
        ArrayList<String> deadlines = new ArrayList<>();

        for (Homework homework : homeworks) {
            String deadline = homework.getFormattedDeadline();
            deadlines.add(homework.getSubject() + ": " + deadline);
        }

        return deadlines;
    }

    public void iterateWithConsumer(Consumer<Homework> c) {
        for (Homework homework : homeworks) {
            c.accept(homework);
        }
    }



}
