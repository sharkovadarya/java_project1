package ru.spbau.group202.notdeadbydeadline.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public abstract class DetailedEntry implements Comparable<DetailedEntry> {
    @NotNull
    public abstract ArrayList<String> getDetails();

    @NotNull
    protected abstract LocalTime getTime();

    public int compareTo(@NotNull DetailedEntry detailedEntry) {
        return getTime().compareTo(detailedEntry.getTime());
    }
}
