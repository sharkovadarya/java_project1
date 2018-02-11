package ru.spbau.group202.notdeadbydeadline.model;


import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;


public class Exam extends DetailedTimedEntry {
    private String subject;
    private String description;
    private LocalDateTime date;
    private boolean isAccepted = false;
    private ExamEnum examType;
    private int id;

    public Exam(@NotNull String subject, String description, @NotNull LocalDateTime date,
                @NotNull ExamEnum examType, int id) {
        this.subject = subject;
        this.description = description;
        this.date = date;
        this.examType = examType;
        this.id = id;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public int getYear() {
        return date.getYear();
    }

    public int getMonth() {
        return date.getMonthOfYear();
    }

    public int getDay() {
        return date.getDayOfMonth();
    }

    public int getHour() {
        return date.getHourOfDay();
    }

    public int getMinute() {
        return date.getMinuteOfHour();
    }

    @NotNull
    public ExamEnum getExamType() {
        return examType;
    }

    public int getId() {
        return id;
    }

    @Override
    @NotNull
    public ArrayList<String> getDetails() {
        ArrayList<String> workDetails = new ArrayList<>();
        workDetails.add(subject);
        workDetails.add(DateTimeFormat.forPattern("HH:mm").print(date));
        workDetails.add(examType.getDescription());
        workDetails.add(description);
        workDetails.add(Integer.toString(id));
        return workDetails;
    }

    @NotNull
    @Override
    public Bundle getDeconstructed() {
        Bundle bundle = new Bundle();
        bundle.putString("subject", subject);
        bundle.putString("description", description);
        bundle.putSerializable("type", examType);
        bundle.putBoolean("isAccepted", isAccepted);
        bundle.putInt("id", id);
        bundle.putSerializable("date", date);
        return bundle;
    }

    @NotNull
    @Override
    protected LocalTime getTime() {
        return date.toLocalTime();
    }
}