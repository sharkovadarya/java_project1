package ru.spbau.group202.notdeadbydeadline.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;


//TODO rename
public abstract class DetailedTimeEntry implements DetailedEntry, Comparable<DetailedTimeEntry> {
    @NotNull
    protected abstract LocalTime getTime();

    public int compareTo(@NotNull DetailedTimeEntry detailedEntry) {
        return getTime().compareTo(detailedEntry.getTime());
    }
}
