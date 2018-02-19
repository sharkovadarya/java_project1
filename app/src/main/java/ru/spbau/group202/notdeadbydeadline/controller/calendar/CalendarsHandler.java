package ru.spbau.group202.notdeadbydeadline.controller.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import static android.provider.CalendarContract.ACCOUNT_TYPE_LOCAL;

public class CalendarsHandler {
    private final ContentResolver contentResolver;

    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
            CalendarContract.Calendars.ACCOUNT_TYPE                   // 4
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_ACCOUNT_TYPE_INDEX = 4;


    public CalendarsHandler(Context context) {
        contentResolver = context.getContentResolver();
    }

    public long findCalendarID(String accountName, String name) throws SecurityException {
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION,
                "(" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" +
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ?)",
                new String[]{accountName, name}, null);
        long calID = -1;
        if (cur != null) {
            while (cur.moveToNext()) {
                calID = cur.getLong(PROJECTION_ID_INDEX);
            }
            cur.close();
        }

        return calID;
    }

    public void addNewCalendarIfNotExist(String accountName, String displayName)
                                                      throws SecurityException {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        Cursor cur = contentResolver.query(uri, EVENT_PROJECTION,
                "(" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" +
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ?)",
                new String[]{accountName, displayName}, null);

        if (cur != null && cur.getCount() > 0) {
            Log.i("myLog", "This calendar exists.");
            cur.close();
        } else {
            addNewCalendar(uri, accountName, displayName);
        }

    }

    private Uri addNewCalendar(Uri uri, String accountName, String displayName) {
        Uri sUri = asSyncAdapter(uri, accountName, ACCOUNT_TYPE_LOCAL);

        ContentValues mNewValues = new ContentValues();
        mNewValues.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        mNewValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, displayName);
        mNewValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE_LOCAL);

        return contentResolver.insert(sUri, mNewValues);
    }

    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }

}
