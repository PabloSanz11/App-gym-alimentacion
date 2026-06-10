package com.pablosanz.gymapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat displaySdf = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());

    public static String today() {
        return sdf.format(new Date());
    }

    public static String formatDate(Date date) {
        return sdf.format(date);
    }

    public static String formatForDisplay(String dateStr) {
        try {
            Date date = sdf.parse(dateStr);
            return date != null ? displaySdf.format(date) : dateStr;
        } catch (ParseException e) {
            return dateStr;
        }
    }

    public static Date parseDate(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String addDays(String dateStr, int days) {
        try {
            Date date = sdf.parse(dateStr);
            if (date == null) return dateStr;
            long millis = date.getTime() + (long) days * 24 * 60 * 60 * 1000;
            return sdf.format(new Date(millis));
        } catch (ParseException e) {
            return dateStr;
        }
    }
}
