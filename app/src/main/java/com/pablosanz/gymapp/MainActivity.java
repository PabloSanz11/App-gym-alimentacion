package com.pablosanz.gymapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pablosanz.gymapp.databinding.ActivityMainBinding;
import com.pablosanz.gymapp.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Permission result handled
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }

        // Show previous crash info for debugging
        android.content.SharedPreferences prefs = getSharedPreferences("crash_log", MODE_PRIVATE);
        String lastCrash = prefs.getString("last_crash", null);
        if (lastCrash != null) {
            android.widget.Toast.makeText(this, "Crash anterior: " + lastCrash.substring(0, Math.min(100, lastCrash.length())), android.widget.Toast.LENGTH_LONG).show();
            prefs.edit().remove("last_crash").apply();
        }

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Create notification channels and schedule meal reminders
        try {
            NotificationHelper.createNotificationChannels(this);
            NotificationHelper.scheduleAllMealReminders(this);
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Notification setup failed", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
