package ru.spbau.group202.notdeadbydeadline.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;


public abstract class DetailedTimedEntry implements DetailedEntry, Comparable<DetailedTimedEntry> {
    @NotNull
    protected abstract LocalTime getTime();

    public int compareTo(@NotNull DetailedTimedEntry detailedEntry) {
        return getTime().compareTo(detailedEntry.getTime());
    }
}
