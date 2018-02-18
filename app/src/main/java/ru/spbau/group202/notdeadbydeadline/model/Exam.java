package ru.spbau.group202.notdeadbydeadline.model;


import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.utilities.ModelUtils;


public class Exam extends ScheduleEntry {
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

    public String getFormattedTime() {
        return DateTimeFormat.forPattern("HH:mm").print(date);
    }

    @Override
    @NotNull
    public List<String> getScheduleDescription() {
        return ModelUtils.EXAM_FIELDS_TO_STRING_LIST.apply(this);
    }

    @NotNull
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
    public LocalTime getTime() {
        return date.toLocalTime();
    }
}