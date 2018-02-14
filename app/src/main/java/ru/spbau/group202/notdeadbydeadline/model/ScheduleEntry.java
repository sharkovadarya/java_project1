package ru.spbau.group202.notdeadbydeadline.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;

import java.util.List;


public abstract class ScheduleEntry implements Comparable<ScheduleEntry> {
    @NotNull
    protected abstract LocalTime getTime();

    @NotNull
    public abstract List<String> getScheduleDescription();

    public int compareTo(@NotNull ScheduleEntry detailedEntry) {
        return getTime().compareTo(detailedEntry.getTime());
    }
}
