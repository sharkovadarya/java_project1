package ru.spbau.group202.notdeadbydeadline.controller.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ru.spbau.group202.notdeadbydeadline.model.Homework;
import ru.spbau.group202.notdeadbydeadline.model.ScheduleEntry;

public class EventsHandler {

    private final ContentResolver contentResolver;

    private final long calID;

    private static final int GREEN = 0x00B359;
    private static final int BLUE = 0x1976D2;

    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID,                           // 0
            CalendarContract.Events.TITLE                          // 1
    };

    private static final int PROJECTION_ID_INDEX = 0;

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
            addReminder(Long.parseLong(uri.getLastPathSegment()));
        }

    }

    public void addReminder(long eventId) throws SecurityException {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 60 * 24);
        values.put(CalendarContract.Reminders.EVENT_ID, eventId);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    public void deleteAll() throws SecurityException {
        contentResolver.delete(CalendarContract.Events.CONTENT_URI, "(" + CalendarContract.Events.CALENDAR_ID + " = ?)",
                new String[]{((Long)calID).toString()});
    }

}

