package ru.spbau.group202.notdeadbydeadline.model.utilities;


import java.util.List;

public class StudyMaterialsUpdatingException extends Exception {
    private List<List<String>> failedToUpdateMaterialsDetails;

    public StudyMaterialsUpdatingException(List<List<String>> failedToUpdateMaterialsDetails) {
        this.failedToUpdateMaterialsDetails = failedToUpdateMaterialsDetails;
    }

    public List<List<String>> getFailedToUpdateMaterialsDetails() {
        return failedToUpdateMaterialsDetails;
    }
}
