package ru.spbau.group202.notdeadbydeadline.controller.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.LocalDate;

import ru.spbau.group202.notdeadbydeadline.model.ClassEntry;
import ru.spbau.group202.notdeadbydeadline.model.Homework;

public class EventsHandler {

    private static final long CLASS_DURATION = 90 * 60 * 1000;

    private static final int GREEN = 0x00B359;
    private static final int BLUE = 0x1976D2;

    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID,                           // 0
            CalendarContract.Events.TITLE                          // 1
    };

    private static final int PROJECTION_ID_INDEX = 0;

    private final ContentResolver contentResolver;

    private final long calID;

    public EventsHandler(Context context, long calID) {
        contentResolver = context.getContentResolver();
        this.calID = calID;
    }

    public void addHomework(Homework hw) {
        long startMillis = hw.getDeadline().getDateTime().toDateTime().getMillis();
        long endMillis = hw.getDeadline().getDateTime().toDateTime().getMillis();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Homework: " + hw.getSubject());
        values.put(CalendarContract.Events.DESCRIPTION, hw.getDescription());
        // TODO fix this
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC+03:00");
        /*values.put(CalendarContract.Events.EVENT_TIMEZONE,
                DateTimeZone.getDefault().getName(DateTime.now().getMillis()));*/

        values.put(CalendarContract.Events.EVENT_COLOR, BLUE);

        Uri uri;

        try {
            uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
        } catch (SecurityException e) {
            Log.i("myLog", e.toString());
            throw e;
        }

        if (uri != null) {
            // remind a day before the deadline
            addReminder(Long.parseLong(uri.getLastPathSegment()), 60 * 24);
        }
    }

    public void addClassEntry(ClassEntry ce, LocalDate localDate) {
        long startMillis = localDate.toDateTime(ce.getTime()).getMillis();
        long endMillis = startMillis + CLASS_DURATION;

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, ce.getSubject());
        if (ce.getAuditorium().trim().length() > 0) {
            if (ce.getTeacher().trim().length() > 0) {
                values.put(CalendarContract.Events.DESCRIPTION, ce.getAuditorium() +
                        ", " + ce.getTeacher());
            } else {
                values.put(CalendarContract.Events.DESCRIPTION, ce.getAuditorium());
            }
        } else if (ce.getTeacher().trim().length() > 0) {
            values.put(CalendarContract.Events.DESCRIPTION, ce.getTeacher());
        }
        // TODO fix this
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC+03:00");
        /*values.put(CalendarContract.Events.EVENT_TIMEZONE,
                DateTimeZone.getDefault().getName(DateTime.now().getMillis()));*/

        values.put(CalendarContract.Events.EVENT_COLOR, GREEN);

        Uri uri;

        try {
            uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
        } catch (SecurityException e) {
            Log.i("myLog", e.toString());
            throw e;
        }

        if (uri != null) {
            // remind 30 minutes before the class
            addReminder(Long.parseLong(uri.getLastPathSegment()), 30);
        }
    }

    public void addReminder(long eventId, int minutes) throws SecurityException {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, minutes);
        values.put(CalendarContract.Reminders.EVENT_ID, eventId);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    public void deleteAll() throws SecurityException {
        contentResolver.delete(CalendarContract.Events.CONTENT_URI, "(" + CalendarContract.Events.CALENDAR_ID + " = ?)",
                new String[]{((Long)calID).toString()});
    }

}

