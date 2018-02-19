package ru.spbau.group202.notdeadbydeadline.model.utilities;


import java.util.List;

public class WebStudyMaterialException extends Exception {
    private List<String> studyMaterial;

    public WebStudyMaterialException(String message, List<String> studyMaterial) {
        super(message);
    }

    public List<String> getStudyMaterial() {
        return studyMaterial;
    }
}