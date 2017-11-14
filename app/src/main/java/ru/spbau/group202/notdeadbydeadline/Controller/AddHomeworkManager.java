package ru.spbau.group202.notdeadbydeadline.Controller;

import ru.spbau.group202.notdeadbydeadline.Model.Homework;
import ru.spbau.group202.notdeadbydeadline.Model.Homeworks;
import ru.spbau.group202.notdeadbydeadline.UI.AddHomeworkActivity;

public class AddHomeworkManager {

    // TODO make Homeworks a singleton so the following will be unnecessary
    private Homeworks homeworks;

    private String subject;
    private String description;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;
    private int expectedScore;

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

    public void addNewHomework() {

        homeworks.addHomework(year, month, day, hour, minutes, );


    }

}
