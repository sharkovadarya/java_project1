package ru.spbau.group202.notdeadbydeadline.Model;

import java.util.ArrayList;

public class Homeworks {

    private ArrayList<Homework> homeworks = new ArrayList<>();

    public void addHomework(String year, String month, String day,
                            String hour, String minute, String subject,
                            boolean isRegular, String description, String howToSend, int expectedScore) {
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


}
