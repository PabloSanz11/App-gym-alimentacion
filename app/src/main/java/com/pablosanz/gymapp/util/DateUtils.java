package com.pablosanz.gymapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_FORMAT = "dd/MM/yyyy";
    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(
            () -> new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()));
    private static final ThreadLocal<SimpleDateFormat> displaySdf = ThreadLocal.withInitial(
            () -> new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault()));

    public static String today() {
        return sdf.get().format(new Date());
    }

    public static String formatDate(Date date) {
        return sdf.get().format(date);
    }

    public static String formatForDisplay(String dateStr) {
        try {
            Date date = sdf.get().parse(dateStr);
            return date != null ? displaySdf.get().format(date) : dateStr;
        } catch (ParseException e) {
            return dateStr;
        }
    }

    public static Date parseDate(String dateStr) {
        try {
            return sdf.get().parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatShort(String dateStr) {
        try {
            Date date = sdf.get().parse(dateStr);
            SimpleDateFormat shortFmt = new SimpleDateFormat("EEE dd", new Locale("es", "MX"));
            return date != null ? shortFmt.format(date) : dateStr;
        } catch (ParseException e) {
            return dateStr;
        }
    }

    public static String addDays(String dateStr, int days) {
        try {
            Date date = sdf.get().parse(dateStr);
            if (date == null) return dateStr;
            long millis = date.getTime() + (long) days * 24 * 60 * 60 * 1000;
            return sdf.get().format(new Date(millis));
        } catch (ParseException e) {
            return dateStr;
        }
    }
}
