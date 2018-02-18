package ru.spbau.group202.notdeadbydeadline.model.utilities;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;
import ru.spbau.group202.notdeadbydeadline.model.StudyMaterial;

public class ModelUtils {
    public static final Function<Homework, List<String>> HW_FIELDS_TO_STRING_LIST = hw -> {
        ArrayList<String> fields = new ArrayList<>();
        fields.add(hw.getDescription());
        fields.add(hw.getFormattedDeadline());
        fields.add(hw.getHowToSend());
        if (hw.getExpectedScore() == -1.0) {
            fields.add("Not specified");
        } else {
            fields.add(Double.toString(hw.getExpectedScore()));
        }
        fields.add(Integer.toString(hw.getRegularity()));
        fields.addAll(hw.getMaterials());
        fields.add(Integer.toString(hw.getId()));
        return fields;
    };

    public static final Function<Homework, List<String>> HW_DEADLINE_FIELDS_TO_STRING_LIST = hw ->
            Arrays.asList(hw.getSubject(), hw.getDescription(), hw.getFormattedDeadline());

    public static final Function<Exam, List<String>> EXAM_FIELDS_TO_STRING_LIST = exam ->
            Arrays.asList(exam.getSubject(), exam.getFormattedTime(), exam.getExamType().getDescription(),
                    exam.getDescription(), Integer.toString(exam.getId()));

    public static final Function<StudyMaterial, List<String>> STUDY_MATERIAL_FIELDS_TO_STRING_LIST =
            material ->  Arrays.asList(material.getSubject(), Integer.toString(material.getTerm()),
                    material.getPath(), material.getName(), Integer.toString(material.getId()));

    public static final Function<ScheduleEntry, List<String>> SCHEDULE_ENTRY_TO_SCHEDULE_DESCRIPTION =
            ScheduleEntry::getScheduleDescription;
}
