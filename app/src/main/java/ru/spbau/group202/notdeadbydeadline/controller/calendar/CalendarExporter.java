package ru.spbau.group202.notdeadbydeadline.controller.calendar;

import android.content.Context;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.ClassEntry;
import ru.spbau.group202.notdeadbydeadline.model.Exam;
import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.WeekParityEnum;

public class CalendarExporter {

    private EventsHandler homeworkEventsHandler;
    private EventsHandler classesEventsHandler;
    private EventsHandler examsEventsHandler;

    public CalendarExporter(Context context) {
        CalendarsHandler calendarsHandler = new CalendarsHandler(context);

        String defaultAccountName = "NDBD";
        String defaultDisplayNameHomeworks = "NDBD homeworks";
        String defaultDisplayNameClasses = "NDBD classes";
        String defaultDisplayNameExams = "NDBD exams";
        calendarsHandler.addNewCalendarIfNotExist(defaultAccountName, defaultDisplayNameHomeworks);
        calendarsHandler.addNewCalendarIfNotExist(defaultAccountName, defaultDisplayNameClasses);
        calendarsHandler.addNewCalendarIfNotExist(defaultAccountName, defaultDisplayNameExams);

        homeworkEventsHandler = new EventsHandler(context,
                calendarsHandler.findCalendarID(defaultAccountName,
                                                defaultDisplayNameHomeworks));
        classesEventsHandler = new EventsHandler(context,
                calendarsHandler.findCalendarID(defaultAccountName,
                                                defaultDisplayNameClasses));
        examsEventsHandler = new EventsHandler(context,
                calendarsHandler.findCalendarID(defaultAccountName,
                        defaultDisplayNameExams));

    }

    public void addHomeworks(List<Homework> homeworks) {
        homeworkEventsHandler.deleteAll();
        for (Homework homework : homeworks) {
            homeworkEventsHandler.addHomework(homework);
        }
    }

    public void addClassEntries(List<ClassEntry> classes, LocalDate endTermDate) {
        //LocalDate localDate = LocalDate.now().plusDays(ce.getDayOfWeek());
        classesEventsHandler.deleteAll();
        for (ClassEntry classEntry : classes) {
            LocalDate localDate = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
            if (localDate.getWeekOfWeekyear() % 2 == 0) { // even week
                if (classEntry.getWeekParity() == WeekParityEnum.ON_EVEN_WEEK ||
                        classEntry.getWeekParity() == WeekParityEnum.ALWAYS) {
                    localDate = localDate.plusDays(classEntry.getDayOfWeek());
                } else {
                    localDate = localDate.plusDays(classEntry.getDayOfWeek() + 7);
                }
            } else {
                if (classEntry.getWeekParity() == WeekParityEnum.ON_ODD_WEEK ||
                        classEntry.getWeekParity() == WeekParityEnum.ALWAYS) {
                    localDate = localDate.plusDays(classEntry.getDayOfWeek());
                } else {
                    localDate = localDate.plusDays(classEntry.getDayOfWeek() + 7);
                }
            }
            classesEventsHandler.addClassEntry(classEntry, localDate);
            while (localDate.compareTo(endTermDate) < 0) {
                localDate = localDate.plusDays(
                        classEntry.getWeekParity() == WeekParityEnum.ALWAYS ? 7 : 14);
                classesEventsHandler.addClassEntry(classEntry, localDate);
            }
        }
    }

    public void addExamEntries(List<Exam> exams) {
        examsEventsHandler.deleteAll();
        for (Exam exam : exams) {
            // TODO add this method and call it
        }
    }

}
