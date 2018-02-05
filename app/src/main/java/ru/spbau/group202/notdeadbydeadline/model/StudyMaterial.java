package ru.spbau.group202.notdeadbydeadline.model;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StudyMaterial implements DetailedEntry {
    private String subject = "Not stated", path, URL;
    private int term, id;

    public StudyMaterial(@NotNull String subject, int term, @NotNull String URL,
                         @NotNull String path, int id) {
        this.subject = subject;
        this.term = term;
        this.URL = URL;
        this.path = path;
        this.id = id;
        update();
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getURL() {
        return URL;
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

    //TODO
    public void update() {

    }

    @NotNull
    @Override
    public ArrayList<String> getDetails() {
        ArrayList<String> studyMaterialDetails = new ArrayList<>();
        studyMaterialDetails.add(subject);
        studyMaterialDetails.add(Integer.toString(term));
        studyMaterialDetails.add(path);
        studyMaterialDetails.add(URL);
        studyMaterialDetails.add(Integer.toString(id));

        return studyMaterialDetails;
    }
}
