package ru.spbau.group202.notdeadbydeadline.model;


import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class StudyMaterial {
    private String subject;
    private String path;
    private String name;
    private int term;
    private int version;
    private int id;

    public StudyMaterial(@NotNull String name, @NotNull String subject, int term,
                         @NotNull String path, int version, int id) {
        this.subject = subject;
        this.term = term;
        this.name = name;
        this.path = path;
        this.version = version;
        this.id = id;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getTerm() {
        return term;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @NotNull
    public Bundle getDeconstructed() {
        Bundle bundle = new Bundle();
        bundle.putString("subject", subject);
        bundle.putString("path", path);
        bundle.putString("name", name);
        bundle.putInt("id", id);
        bundle.putInt("term", term);
        return bundle;
    }
}