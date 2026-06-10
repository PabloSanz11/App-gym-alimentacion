package com.pablosanz.gymapp.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.pablosanz.gymapp.MainActivity;
import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.util.NotificationHelper;

public class MealNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String mealSlot = intent.getStringExtra(NotificationHelper.EXTRA_MEAL_SLOT);
        String mealName = intent.getStringExtra(NotificationHelper.EXTRA_MEAL_NAME);
        int alarmId = intent.getIntExtra("alarm_id", 1);

        if (mealName == null) mealName = "comida";
        if (mealSlot == null) mealSlot = "comida";

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, alarmId, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificationHelper.CHANNEL_ID_MEALS)
                .setSmallIcon(R.drawable.ic_nutrition)
                .setContentTitle("GymFit Pro - " + capitalize(mealName))
                .setContentText("Es hora de registrar tu " + mealSlot + " en la app.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(alarmId, builder.build());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
