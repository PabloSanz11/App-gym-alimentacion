package com.pablosanz.gymapp.ui.nutrition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentNutritionBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.List;

public class NutritionFragment extends Fragment {

    private FragmentNutritionBinding binding;
    private NutritionRepository nutritionRepository;
    private String currentDate;

    private static final float GOAL_CALORIES = 2500f;
    private static final float GOAL_PROTEIN = 170f;
    private static final float GOAL_CARBS = 280f;
    private static final float GOAL_FAT = 80f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNutritionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());
        currentDate = DateUtils.today();

        updateDateDisplay();
        loadMealData();
        setupClickListeners();
        loadWeeklySummary();
    }

    private void updateDateDisplay() {
        binding.tvCurrentDate.setText(DateUtils.formatForDisplay(currentDate));
    }

    private void setupClickListeners() {
        binding.btnPrevDay.setOnClickListener(v -> {
            currentDate = DateUtils.addDays(currentDate, -1);
            updateDateDisplay();
            loadMealData();
        });

        binding.btnNextDay.setOnClickListener(v -> {
            currentDate = DateUtils.addDays(currentDate, 1);
            updateDateDisplay();
            loadMealData();
        });

        binding.btnAddDesayuno.setOnClickListener(v -> openMealLog("desayuno"));
        binding.btnAddAlmuerzo.setOnClickListener(v -> openMealLog("almuerzo"));
        binding.btnAddMerienda.setOnClickListener(v -> openMealLog("merienda"));
        binding.btnAddCena.setOnClickListener(v -> openMealLog("cena"));
    }

    private void openMealLog(String mealSlot) {
        Bundle args = new Bundle();
        args.putString("mealSlot", mealSlot);
        args.putString("date", currentDate);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nutritionFragment_to_mealLogFragment, args);
    }

    private void loadMealData() {
        nutritionRepository.getMealLogsByDate(currentDate).observe(getViewLifecycleOwner(), mealLogs -> {
            float totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;

            for (MealLog log : mealLogs) {
                totalCalories += log.getTotalCaloriesKcal();
                totalProtein += log.getTotalProteinG();
                totalCarbs += log.getTotalCarbsG();
                totalFat += log.getTotalFatG();

                switch (log.getMealSlot()) {
                    case "desayuno":
                        binding.tvDesayunoMacros.setText(formatMacros(log));
                        break;
                    case "almuerzo":
                        binding.tvAlmuerzoMacros.setText(formatMacros(log));
                        break;
                    case "merienda":
                        binding.tvMeriendaMacros.setText(formatMacros(log));
                        break;
                    case "cena":
                        binding.tvCenaMacros.setText(formatMacros(log));
                        break;
                }
            }

            // Reset slots not in data
            if (!hasMealSlot(mealLogs, "desayuno")) binding.tvDesayunoMacros.setText("Sin registros");
            if (!hasMealSlot(mealLogs, "almuerzo")) binding.tvAlmuerzoMacros.setText("Sin registros");
            if (!hasMealSlot(mealLogs, "merienda")) binding.tvMeriendaMacros.setText("Sin registros");
            if (!hasMealSlot(mealLogs, "cena")) binding.tvCenaMacros.setText("Sin registros");

            // Update totals
            binding.tvTotalCalories.setText(String.format("%.0f / %.0f kcal", totalCalories, GOAL_CALORIES));
            binding.tvTotalProtein.setText(String.format("%.0fg / %.0fg", totalProtein, GOAL_PROTEIN));
            binding.tvTotalCarbs.setText(String.format("%.0fg / %.0fg", totalCarbs, GOAL_CARBS));
            binding.tvTotalFat.setText(String.format("%.0fg / %.0fg", totalFat, GOAL_FAT));

            // Update progress bars
            binding.progressCalories.setMax((int) GOAL_CALORIES);
            binding.progressCalories.setProgress((int) Math.min(totalCalories, GOAL_CALORIES));
            binding.progressProtein.setMax((int) GOAL_PROTEIN);
            binding.progressProtein.setProgress((int) Math.min(totalProtein, GOAL_PROTEIN));
            binding.progressCarbs.setMax((int) GOAL_CARBS);
            binding.progressCarbs.setProgress((int) Math.min(totalCarbs, GOAL_CARBS));
            binding.progressFat.setMax((int) GOAL_FAT);
            binding.progressFat.setProgress((int) Math.min(totalFat, GOAL_FAT));
        });
    }

    private boolean hasMealSlot(List<MealLog> logs, String slot) {
        for (MealLog log : logs) {
            if (slot.equals(log.getMealSlot())) return true;
        }
        return false;
    }

    private String formatMacros(MealLog log) {
        return String.format("%.0f kcal | P:%.0fg C:%.0fg G:%.0fg",
                log.getTotalCaloriesKcal(),
                log.getTotalProteinG(),
                log.getTotalCarbsG(),
                log.getTotalFatG());
    }

    private void loadWeeklySummary() {
        nutritionRepository.getWeeklySummary(logs -> {
            // Group calories by date
            java.util.Map<String, Float> caloriesByDate = new java.util.LinkedHashMap<>();
            for (MealLog log : logs) {
                caloriesByDate.merge(log.getDate(), log.getTotalCaloriesKcal(), Float::sum);
            }
            StringBuilder sb = new StringBuilder();
            if (caloriesByDate.isEmpty()) {
                sb.append("Sin datos esta semana");
            } else {
                for (java.util.Map.Entry<String, Float> entry : caloriesByDate.entrySet()) {
                    sb.append(DateUtils.formatForDisplay(entry.getKey()))
                      .append(": ")
                      .append(String.format("%.0f kcal", entry.getValue()))
                      .append("\n");
                }
            }
            String text = sb.toString().trim();
            requireActivity().runOnUiThread(() -> {
                if (binding != null) binding.tvWeeklySummary.setText(text);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
