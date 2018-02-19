package ru.spbau.group202.notdeadbydeadline.model;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;


public class Class extends ScheduleEntry {
    private String subject;
    private String auditorium;
    private String teacher;
    private LocalTime time;
    private int dayOfWeek;
    private int id;
    private WeekParityEnum weekParity;

    public Class(@NotNull String subject, int dayOfWeek, int hour, int minute,
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
    public LocalTime getTime() {
        return time;
    }

    @NotNull
    @Override
    public List<String> getScheduleDescription() {
        ArrayList<String> classDetails = new ArrayList<>();
        classDetails.add(subject);
        classDetails.add(DateTimeFormat.forPattern("HH:mm").print(time));
        classDetails.add(teacher);
        classDetails.add(auditorium);
        classDetails.add(Integer.toString(id));
        return classDetails;
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