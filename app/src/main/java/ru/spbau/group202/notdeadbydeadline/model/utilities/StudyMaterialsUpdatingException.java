package ru.spbau.group202.notdeadbydeadline.model.utilities;


import java.util.List;

public class StudyMaterialsUpdatingException extends Exception {
    private List<WebStudyMaterialException> exceptions;

    public StudyMaterialsUpdatingException(List<WebStudyMaterialException> exceptions) {
        this.exceptions = exceptions;
    }

    public List<WebStudyMaterialException> getErrors() {
        return exceptions;
    }
}
