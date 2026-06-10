package com.pablosanz.gymapp.util;

import android.content.Context;

/**
 * Stub for future Samsung Watch integration.
 */
public class WearableDataManager {

    private static WearableDataManager instance;
    private final Context context;

    private WearableDataManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static WearableDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new WearableDataManager(context);
        }
        return instance;
    }

    public void connect() {
        // TODO: Phase 2 - Samsung Watch integration
    }

    public void syncCalories(float calories) {
        // TODO: Phase 2 - Sync calorie data to watch
    }

    public void disconnect() {
        // TODO: Phase 2 - Disconnect from watch
    }
}
