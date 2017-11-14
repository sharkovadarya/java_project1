package ru.spbau.group202.notdeadbydeadline.Controller;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.UI.AddHomeworkActivity;

public class AddHomeworkManager {
    private Homeworks homeworks;
    private String subject;
    private String description;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;
    private int expectedScore;
    private boolean isRegular;
    private String howToSend;

    public AddHomeworkManager(Homeworks homeworks) {
        this.homeworks = homeworks;
    }

    public void storeSubject(String subject) {
        this.subject = subject;
    }

    public void storeDescription(String description) {
        this.description = description;
    }

    public void storeExpectedSCore(int expectedScore) {
        this.expectedScore = expectedScore;
    }

    public void storeDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void storeTime(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    /*public void storeRegularity(boolean isRegular) {
        this.isRegular = isRegular;
    }*/

    public void storeHowToSend(String howToSend) {
        this.howToSend = howToSend;
    }

    public void addNewHomework() {

        homeworks.addHomework(year, month, day, hour, minutes,
                subject, false, description, howToSend, expectedScore);

        // don't mind this line, it's for debug
        int abs = 80;
    }

}
