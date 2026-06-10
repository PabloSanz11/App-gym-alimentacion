package com.pablosanz.gymapp.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.pablosanz.gymapp.receivers.MealNotificationReceiver;

import java.util.Calendar;

public class NotificationHelper {

    public static final String CHANNEL_ID_MEALS = "meal_reminders";
    public static final String EXTRA_MEAL_SLOT = "meal_slot";
    public static final String EXTRA_MEAL_NAME = "meal_name";

    public static void createNotificationChannels(Context context) {
        NotificationChannel mealChannel = new NotificationChannel(
                CHANNEL_ID_MEALS,
                "Recordatorios de Comidas",
                NotificationManager.IMPORTANCE_HIGH);
        mealChannel.setDescription("Notificaciones para recordar registrar tus comidas");

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(mealChannel);
        }
    }

    public static void scheduleAllMealReminders(Context context) {
        // 8:50 AM - Desayuno reminder
        scheduleMealAlarm(context, 8, 50, 1, "desayuno", "Hora del desayuno");
        // 11:50 AM - Almuerzo reminder
        scheduleMealAlarm(context, 11, 50, 2, "almuerzo", "Hora del almuerzo");
        // 3:50 PM - Merienda reminder
        scheduleMealAlarm(context, 15, 50, 3, "merienda", "Hora de la merienda");
        // 7:50 PM - Cena reminder
        scheduleMealAlarm(context, 19, 50, 4, "cena", "Hora de la cena");
    }

    private static void scheduleMealAlarm(Context context, int hour, int minute,
                                           int alarmId, String mealSlot, String mealName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, MealNotificationReceiver.class);
        intent.putExtra(EXTRA_MEAL_SLOT, mealSlot);
        intent.putExtra(EXTRA_MEAL_NAME, mealName);
        intent.putExtra("alarm_id", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public static void cancelAllMealReminders(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        for (int i = 1; i <= 4; i++) {
            Intent intent = new Intent(context, MealNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, i, intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}
