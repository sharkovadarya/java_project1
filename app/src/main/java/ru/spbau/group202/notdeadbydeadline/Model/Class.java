package ru.spbau.group202.notdeadbydeadline.Model;

import java.util.ArrayList;

public class Class implements DetailedEntry {
    private String subject, auditorium, teacher;
    private int dayOfWeek, hour, minute, id;
    private boolean isOnEvenWeeks;

    public Class(String subject, int dayOfWeek, int hour, int minute, boolean isOnEvenWeeks,
                 String auditorium, String teacher, int id) {
        this.subject = subject;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.minute = minute;
        this.isOnEvenWeeks = isOnEvenWeeks;
        this.auditorium = auditorium;
        this.teacher = teacher;
        this.id = id;
    }

    public ArrayList<String> getDetails() {
        ArrayList<String> classDetails = new ArrayList<>();
        classDetails.add(subject);
        return classDetails;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getSubject() {
        return subject;
    }

    public String getAuditorium() {
        return auditorium;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean isOnEvenWeeks() {
        return isOnEvenWeeks;
    }

    public int getId() {
        return id;
    }
}
