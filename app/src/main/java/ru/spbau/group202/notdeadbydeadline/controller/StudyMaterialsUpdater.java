package ru.spbau.group202.notdeadbydeadline.controller;


import android.os.AsyncTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;
import ru.spbau.group202.notdeadbydeadline.model.utilities.ModelUtils;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsSourceAccessException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.StudyMaterialsUpdatingException;
import ru.spbau.group202.notdeadbydeadline.model.utilities.WebStudyMaterialException;


public class StudyMaterialsUpdater extends AsyncTask<StudyMaterial, Void, Void> {
    private StudyMaterialsUpdatingException updatingException;
    private String urlContent;
    private static final String STUDY_MATERIAL_SOURCE = "https://cdkrot.me/";

    public StudyMaterialsUpdater() throws StudyMaterialsSourceAccessException {
        try {
            urlContent = IOUtils.toString(new URL(STUDY_MATERIAL_SOURCE));
        } catch (IOException exception) {
            throw new StudyMaterialsSourceAccessException();
        }
    }

    public StudyMaterialsUpdatingException getError() {
        return updatingException;
    }

    @Override
    protected Void doInBackground(StudyMaterial... studyMaterials) {
        ArrayList<WebStudyMaterialException> errors = new ArrayList<>();
        for (StudyMaterial studyMaterial : studyMaterials) {
            try {
                updateStudyMaterial(studyMaterial);
            } catch (WebStudyMaterialException exception) {
                errors.add(exception);
            }
        }

        if (!errors.isEmpty()) {
            updatingException = new StudyMaterialsUpdatingException(errors);
        }

        return null;
    }

    private void updateStudyMaterial(StudyMaterial studyMaterial) throws WebStudyMaterialException {
        Pattern urlPattern = Pattern.compile("href=\"(/.+/" + studyMaterial.getName() +
                "/(\\d+)\\.pdf)\"");
        Matcher urlMatcher = urlPattern.matcher(urlContent);

        if (!urlMatcher.find()) {
            throw new WebStudyMaterialException("No such study material at study material's resource.",
                    ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST.apply(studyMaterial));
        }
        int newVersion = Integer.valueOf(urlMatcher.group(2));
        if (newVersion > studyMaterial.getVersion()) {
            try {
                URL studyMaterialUrl = new URL(STUDY_MATERIAL_SOURCE + urlMatcher.group(1));
                File studyMaterialFile = new File(studyMaterial.getPath(), studyMaterial.getName());
                FileUtils.copyURLToFile(studyMaterialUrl, studyMaterialFile);
            } catch (MalformedURLException e) {
                throw new WebStudyMaterialException("Malformed URL.",
                        ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST.apply(studyMaterial));
            } catch (IOException e) {
                throw new WebStudyMaterialException("Unable to download study material.",
                        ModelUtils.STUDY_MATERIAL_FIELDS_TO_STRING_LIST.apply(studyMaterial));
            }
            studyMaterial.setVersion(Integer.valueOf(urlMatcher.group(2)));
        }
    }

}
