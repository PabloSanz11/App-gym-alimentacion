package com.pablosanz.gymapp;

import android.app.Application;
import androidx.work.Configuration;

public class GymApplication extends Application implements Configuration.Provider {

    @Override
    public void onCreate() {
        super.onCreate();

        // Show previous crash on startup before anything else
        android.content.SharedPreferences prefs = getSharedPreferences("crash_log", MODE_PRIVATE);
        String lastCrash = prefs.getString("last_crash", null);
        if (lastCrash != null) {
            prefs.edit().remove("last_crash").apply();
            // Delay so the Activity has a moment to start before the Toast appears
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                int end = Math.min(200, lastCrash.length());
                android.widget.Toast.makeText(this,
                        "Error anterior:\n" + lastCrash.substring(0, end),
                        android.widget.Toast.LENGTH_LONG).show();
            }, 800);
        }

        // Capture crashes to show on next launch
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            android.util.Log.e("GymApp_CRASH", "FATAL: " + throwable.getMessage(), throwable);
            try {
                java.io.StringWriter sw = new java.io.StringWriter();
                throwable.printStackTrace(new java.io.PrintWriter(sw));
                getSharedPreferences("crash_log", MODE_PRIVATE)
                        .edit().putString("last_crash", sw.toString()).apply();
            } catch (Exception ignored) {}
            if (defaultHandler != null) defaultHandler.uncaughtException(thread, throwable);
        });
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
