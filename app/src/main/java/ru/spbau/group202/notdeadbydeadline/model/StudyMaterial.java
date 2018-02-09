package ru.spbau.group202.notdeadbydeadline.model;


import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.io.File;
import java.net.URL;

import ru.spbau.group202.notdeadbydeadline.model.utilities.UrlDownloadingException;

public class StudyMaterial implements DetailedEntry {
    private String subject, path, name;
    private int term, version, id;

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

/*
    public void update() throws MalformedURLException, UrlDownloadingException {
        URL url = new URL(URL);
        File file = new File(path);

        try{
            FileUtils.copyURLToFile(url, file);
        } catch (IOException exception) {
            throw new UrlDownloadingException();
        }

    }*/

    @NotNull
    @Override
    public ArrayList<String> getDetails() {
        ArrayList<String> studyMaterialDetails = new ArrayList<>();
        studyMaterialDetails.add(subject);
        studyMaterialDetails.add(Integer.toString(term));
        studyMaterialDetails.add(path);
        studyMaterialDetails.add(name);
        studyMaterialDetails.add(Integer.toString(id));
        return studyMaterialDetails;
    }

    @NotNull
    @Override
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