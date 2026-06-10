package com.pablosanz.gymapp;

import android.app.Application;
import androidx.work.Configuration;

public class GymApplication extends Application implements Configuration.Provider {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            android.util.Log.e("GymApp_CRASH", "FATAL: " + throwable.getMessage(), throwable);
            // Persist crash message for user to share
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("crash_log", MODE_PRIVATE);
                prefs.edit().putString("last_crash", throwable.toString() + " | " + throwable.getMessage()).apply();
            } catch (Exception ignored) {}
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
