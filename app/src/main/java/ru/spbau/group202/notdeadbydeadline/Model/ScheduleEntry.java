package ru.spbau.group202.notdeadbydeadline.Model;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

public class ScheduleEntry implements DetailedEntry {
    private String subject, auditorium, teacher;
    private LocalTime time;
    private int dayOfWeek, id;
    private boolean isOnEvenWeeks;

    public ScheduleEntry(String subject, int dayOfWeek, int hour, int minute, boolean isOnEvenWeeks,
                         String auditorium, String teacher, int id) {
        time = new LocalTime(hour, minute);
        this.subject = subject;
        this.dayOfWeek = dayOfWeek;
        this.isOnEvenWeeks = isOnEvenWeeks;
        this.auditorium = auditorium;
        this.teacher = teacher;
        this.id = id;
    }

    public ArrayList<String> getDetails() {
        ArrayList<String> classDetails = new ArrayList<>();
        classDetails.add(subject);
        classDetails.add(DateTimeFormat.forPattern("hh:mm").print(time));
        classDetails.add(Integer.toString(id));
        return classDetails;
    }

    public int getHour() {
        return time.getHourOfDay();
    }

    public int getMinute() {
        return time.getMinuteOfHour();
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
