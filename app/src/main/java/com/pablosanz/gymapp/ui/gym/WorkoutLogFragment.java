package com.pablosanz.gymapp.ui.gym;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.data.model.ExerciseData;
import com.pablosanz.gymapp.data.model.ExerciseLog;
import com.pablosanz.gymapp.data.model.WorkoutSession;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentWorkoutLogBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutLogFragment extends Fragment {

    private FragmentWorkoutLogBinding binding;
    private GymRepository gymRepository;
    private ExerciseCarouselAdapter carouselAdapter;
    private int dayType = 1;
    private List<Exercise> exercises;

    // Current session state
    private final Map<String, List<ExerciseCarouselAdapter.SeriesEntry>> currentSets = new HashMap<>();
    private float totalWeightKg = 0f;

    // Timer
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int elapsedSeconds = 0;
    private boolean timerRunning = false;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds++;
            updateTimerDisplay();
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            dayType = getArguments().getInt("dayType", 1);
        }

        gymRepository = new GymRepository(requireActivity().getApplication());
        exercises = ExerciseData.getExercisesForDay(dayType);

        String dayName = ExerciseData.getDayName(dayType);
        binding.toolbarWorkout.setTitle("Día " + dayType + "  ·  " + dayName);
        binding.toolbarWorkout.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        binding.tvExerciseIndicator.setText("Ejercicio 1 de " + exercises.size());

        // Load history for all exercises, then build carousel
        List<String> names = new ArrayList<>();
        for (Exercise e : exercises) names.add(e.getName());

        gymRepository.getRecentHistoryForExercises(names, history -> {
            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;
                setupCarousel(history);
                buildDots(exercises.size());
                startTimer();
            });
        });

        binding.btnFinishWorkout.setOnClickListener(v -> showCompletionDialog());
    }

    private void setupCarousel(Map<String, List<ExerciseLog>> history) {
        carouselAdapter = new ExerciseCarouselAdapter(exercises, history, currentSets,
                (exerciseName, weightKg, reps) -> {
                    totalWeightKg += weightKg * reps;
                    if (binding != null)
                        binding.tvTotalWeight.setText(String.format("%.0f", totalWeightKg));
                });

        binding.viewPagerExercises.setAdapter(carouselAdapter);
        binding.viewPagerExercises.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tvExerciseIndicator.setText(
                        "Ejercicio " + (position + 1) + " de " + exercises.size());
                updateDots(position);
            }
        });
    }

    private void buildDots(int count) {
        binding.layoutDots.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int dp6 = (int)(6 * density);
        int dp4 = (int)(4 * density);
        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(dp6, dp6);
            params.setMargins(dp4, 0, dp4, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundColor(i == 0 ? Color.parseColor("#1B3A6B") : Color.parseColor("#C7C7CC"));
            binding.layoutDots.addView(dot);
        }
    }

    private void updateDots(int selected) {
        if (binding == null) return;
        float density = getResources().getDisplayMetrics().density;
        int dp6 = (int)(6 * density);
        int dp10 = (int)(10 * density);
        int dp4 = (int)(4 * density);
        for (int i = 0; i < binding.layoutDots.getChildCount(); i++) {
            View dot = binding.layoutDots.getChildAt(i);
            android.widget.LinearLayout.LayoutParams p =
                    (android.widget.LinearLayout.LayoutParams) dot.getLayoutParams();
            p.width = (i == selected) ? dp10 : dp6;
            p.height = dp6;
            dot.setLayoutParams(p);
            dot.setBackgroundColor(i == selected ? Color.parseColor("#1B3A6B") : Color.parseColor("#C7C7CC"));
        }
    }

    private void startTimer() {
        timerRunning = true;
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void updateTimerDisplay() {
        if (binding == null) return;
        int m = elapsedSeconds / 60;
        int s = elapsedSeconds % 60;
        binding.tvTimer.setText(String.format("%02d:%02d", m, s));
    }

    private void showCompletionDialog() {
        timerHandler.removeCallbacks(timerRunnable);
        timerRunning = false;

        int m = elapsedSeconds / 60;
        int s = elapsedSeconds % 60;
        String timeStr = String.format("%02d:%02d", m, s);

        // Count total sets logged
        int totalSets = 0;
        for (List<ExerciseCarouselAdapter.SeriesEntry> sets : currentSets.values()) {
            totalSets += sets.size();
        }

        String message = "⏱  Tiempo: " + timeStr + "\n" +
                "🏋️  Total cargado: " + String.format("%.0f", totalWeightKg) + " kg\n" +
                "✅  Series completadas: " + totalSets + "\n\n" +
                "Próximamente con la integración Galaxy Watch 6: " +
                "calorías quemadas, frecuencia cardíaca y análisis de rendimiento detallado.";

        new AlertDialog.Builder(requireContext())
                .setTitle("¡Entrenamiento completado! 💪")
                .setMessage(message)
                .setPositiveButton("Guardar y salir", (d, w) -> saveSessionAndExit())
                .setNegativeButton("Seguir entrenando", (d, w) -> {
                    // Resume timer
                    timerHandler.postDelayed(timerRunnable, 1000);
                    timerRunning = true;
                })
                .setCancelable(false)
                .show();
    }

    private void saveSessionAndExit() {
        WorkoutSession session = new WorkoutSession(DateUtils.today(), dayType, elapsedSeconds / 60, "");
        gymRepository.insertSession(session, sessionId -> {
            for (Map.Entry<String, List<ExerciseCarouselAdapter.SeriesEntry>> entry : currentSets.entrySet()) {
                String exName = entry.getKey();
                List<ExerciseCarouselAdapter.SeriesEntry> sets = entry.getValue();
                for (int i = 0; i < sets.size(); i++) {
                    ExerciseCarouselAdapter.SeriesEntry s = sets.get(i);
                    ExerciseLog log = new ExerciseLog(sessionId, exName, i + 1, s.reps, s.weightKg, "");
                    gymRepository.insertExerciseLog(log);
                }
            }
            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;
                Navigation.findNavController(requireView()).popBackStack();
            });
        });
    }

    @Override
    public void onDestroyView() {
        timerHandler.removeCallbacks(timerRunnable);
        super.onDestroyView();
        binding = null;
    }
}
