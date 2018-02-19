package ru.spbau.group202.notdeadbydeadline.controller.calendar;

import android.content.Context;

import java.util.List;

import ru.spbau.group202.notdeadbydeadline.model.Homework;

public class CalendarExporter {

    private EventsHandler eventsHandler;

    public CalendarExporter(Context context) {
        CalendarsHandler calendarsWorker = new CalendarsHandler(context);

        String defaultAccountName = "NDBD";
        String defaultDisplayName = "NDBD";
        calendarsWorker.addNewCalendarIfNotExist(defaultAccountName, defaultDisplayName);

        eventsHandler= new EventsHandler(context,
                calendarsWorker.findCalendarID(defaultAccountName, defaultDisplayName));

    }

    public void addTasks(List<Homework> homeworks) {
        eventsHandler.deleteAll();
        for (Homework homework : homeworks) {
            eventsHandler.addHomework(homework);
        }
    }

}
