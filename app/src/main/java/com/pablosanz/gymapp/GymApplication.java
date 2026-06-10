package com.pablosanz.gymapp;

import android.app.Application;
import androidx.work.Configuration;

public class GymApplication extends Application implements Configuration.Provider {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
