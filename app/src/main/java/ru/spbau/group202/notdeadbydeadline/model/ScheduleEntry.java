package ru.spbau.group202.notdeadbydeadline.model;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

public class ScheduleEntry extends DetailedTimedEntry {
    private String subject, auditorium, teacher;
    private LocalTime time;
    private int dayOfWeek, id;
    private WeekParityEnum weekParity;

    public ScheduleEntry(@NotNull String subject, int dayOfWeek, int hour, int minute,
                         WeekParityEnum weekParity, String auditorium, String teacher, int id) {
        time = new LocalTime(hour, minute);
        this.subject = subject;
        this.dayOfWeek = dayOfWeek;
        this.weekParity = weekParity;
        this.auditorium = auditorium;
        this.teacher = teacher;
        this.id = id;
    }

    @NotNull
    public ArrayList<String> getDetails() {
        ArrayList<String> classDetails = new ArrayList<>();
        classDetails.add(subject);
        classDetails.add(DateTimeFormat.forPattern("HH:mm").print(time));
        classDetails.add(teacher);
        classDetails.add(auditorium);
        classDetails.add(Integer.toString(id));
        return classDetails;
    }

    @NotNull
    @Override
    public Bundle getDeconstructed() {
        Bundle bundle = new Bundle();
        bundle.putString("subject", subject);
        bundle.putString("auditorium", auditorium);
        bundle.putString("teacher", teacher);
        bundle.putInt("id", id);
        bundle.putInt("dayOfWeek", dayOfWeek);
        bundle.putSerializable("weekParity", weekParity);
        bundle.putSerializable("time", time);
        return bundle;
    }

    @NotNull
    @Override
    protected LocalTime getTime() {
        return time;
    }

    public int getHour() {
        return time.getHourOfDay();
    }

    public int getMinute() {
        return time.getMinuteOfHour();
    }

    @NotNull
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

    public WeekParityEnum getWeekParity() {
        return weekParity;
    }

    public int getId() {
        return id;
    }
}