package ru.spbau.group202.notdeadbydeadline.model;


import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;


//TODO rename
public class Work extends DetailedEntry {
    private String subject, description;
    private LocalDateTime date;
    private boolean isAccepted = false;
    private WorkEnum kind;
    private int id;

    public Work(@NotNull String subject, String description, @NotNull LocalDateTime date,
                @NotNull WorkEnum kind, int id) {
        this.subject = subject;
        this.description = description;
        this.date = date;
        this.kind = kind;
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
    public WorkEnum getKind() {
        return kind;
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
        workDetails.add(kind.getDescription());
        workDetails.add(description);
        workDetails.add(Integer.toString(id));
        return workDetails;
    }

    @NotNull
    @Override
    protected LocalTime getTime() {
        return date.toLocalTime();
    }

    public boolean hasPassed() {
        return LocalDateTime.now().compareTo(date) > 0;
    }

}
