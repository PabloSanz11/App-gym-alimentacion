package com.pablosanz.gymapp.ui.nutrition;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentNutritionBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NutritionFragment extends Fragment {

    private FragmentNutritionBinding binding;
    private NutritionRepository nutritionRepository;
    private String currentDate;
    private WeeklyHistoryAdapter weeklyHistoryAdapter;

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

        weeklyHistoryAdapter = new WeeklyHistoryAdapter(new ArrayList<>());
        binding.rvWeeklyHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvWeeklyHistory.setAdapter(weeklyHistoryAdapter);

        setupHistoryChips();
        updateDateDisplay();
        loadMealData();
        setupClickListeners();
        loadWeeklyHistory();
    }

    private void setupHistoryChips() {
        binding.chipGroupHistory.removeAllViews();
        for (int i = 0; i <= 6; i++) {
            String date = DateUtils.addDays(DateUtils.today(), -i);
            Chip chip = new Chip(requireContext());
            chip.setText(i == 0 ? "Hoy" : DateUtils.formatShort(date));
            chip.setCheckable(true);
            chip.setChecked(date.equals(currentDate));
            chip.setTag(date);
            chip.setTypeface(null, Typeface.NORMAL);
            chip.setOnClickListener(v -> {
                currentDate = (String) v.getTag();
                updateDateDisplay();
                loadMealData();
            });
            binding.chipGroupHistory.addView(chip);
        }
    }

    private void updateDateDisplay() {
        binding.tvCurrentDate.setText(DateUtils.formatForDisplay(currentDate));
        // Sync chip selection
        for (int i = 0; i < binding.chipGroupHistory.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupHistory.getChildAt(i);
            chip.setChecked(currentDate.equals(chip.getTag()));
        }
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

        binding.cardDesayuno.setOnClickListener(v -> openMealDetail("desayuno"));
        binding.cardAlmuerzo.setOnClickListener(v -> openMealDetail("almuerzo"));
        binding.cardMerienda.setOnClickListener(v -> openMealDetail("merienda"));
        binding.cardCena.setOnClickListener(v -> openMealDetail("cena"));

        binding.btnMealPrep.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nutritionFragment_to_mealPrepFragment));
    }

    private void openMealLog(String mealSlot) {
        Bundle args = new Bundle();
        args.putString("mealSlot", mealSlot);
        args.putString("date", currentDate);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nutritionFragment_to_mealLogFragment, args);
    }

    private void openMealDetail(String mealSlot) {
        nutritionRepository.getOrCreateMealLog(currentDate, mealSlot, mealLog -> {
            Bundle args = new Bundle();
            args.putString("mealSlot", mealSlot);
            args.putString("date", currentDate);
            args.putInt("mealLogId", (int) mealLog.getId());
            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_nutritionFragment_to_mealDetailFragment, args);
            });
        });
    }

    private void loadMealData() {
        nutritionRepository.getMealLogsByDate(currentDate).observe(getViewLifecycleOwner(), mealLogs -> {
            if (binding == null) return;
            float totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;

            for (MealLog log : mealLogs) {
                totalCalories += log.getTotalCaloriesKcal();
                totalProtein += log.getTotalProteinG();
                totalCarbs += log.getTotalCarbsG();
                totalFat += log.getTotalFatG();

                switch (log.getMealSlot()) {
                    case "desayuno":
                        binding.tvDesayunoMacros.setText(formatMacros(log));
                        loadRecipeButton(log.getId(), binding.btnViewRecipeDesayuno); break;
                    case "almuerzo":
                        binding.tvAlmuerzoMacros.setText(formatMacros(log));
                        loadRecipeButton(log.getId(), binding.btnViewRecipeAlmuerzo); break;
                    case "merienda":
                        binding.tvMeriendaMacros.setText(formatMacros(log));
                        loadRecipeButton(log.getId(), binding.btnViewRecipeMerienda); break;
                    case "cena":
                        binding.tvCenaMacros.setText(formatMacros(log));
                        loadRecipeButton(log.getId(), binding.btnViewRecipeCena); break;
                }
            }

            if (!hasMealSlot(mealLogs, "desayuno")) {
                binding.tvDesayunoMacros.setText("Sin registros");
                binding.btnViewRecipeDesayuno.setVisibility(View.GONE);
            }
            if (!hasMealSlot(mealLogs, "almuerzo")) {
                binding.tvAlmuerzoMacros.setText("Sin registros");
                binding.btnViewRecipeAlmuerzo.setVisibility(View.GONE);
            }
            if (!hasMealSlot(mealLogs, "merienda")) {
                binding.tvMeriendaMacros.setText("Sin registros");
                binding.btnViewRecipeMerienda.setVisibility(View.GONE);
            }
            if (!hasMealSlot(mealLogs, "cena")) {
                binding.tvCenaMacros.setText("Sin registros");
                binding.btnViewRecipeCena.setVisibility(View.GONE);
            }

            // Header protein bar
            binding.tvHeaderProteinCurrent.setText(String.format("%.0f", totalProtein));
            binding.progressHeaderProtein.setMax((int) GOAL_PROTEIN);
            binding.progressHeaderProtein.setProgress((int) Math.min(totalProtein, GOAL_PROTEIN));

            // Header calories
            float remaining = Math.max(0, GOAL_CALORIES - totalCalories);
            binding.tvHeaderCalories.setText(String.format("%.0f / %.0f", totalCalories, GOAL_CALORIES));
            binding.tvHeaderCaloriesRemaining.setText(String.format("%.0f restantes", remaining));

            // Summary card
            binding.tvTotalCalories.setText(String.format("%.0f / %.0f kcal", totalCalories, GOAL_CALORIES));
            binding.tvTotalProtein.setText(String.format("%.0fg / %.0fg", totalProtein, GOAL_PROTEIN));
            binding.tvTotalCarbs.setText(String.format("%.0fg / %.0fg", totalCarbs, GOAL_CARBS));
            binding.tvTotalFat.setText(String.format("%.0fg / %.0fg", totalFat, GOAL_FAT));

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

    private void loadRecipeButton(long mealLogId, android.widget.TextView button) {
        nutritionRepository.getFoodEntriesByMealLog(mealLogId, entries -> {
            FoodEntry recipeEntry = null;
            for (FoodEntry e : entries) {
                if (e.getRecipeId() > 0) { recipeEntry = e; break; }
            }
            final FoodEntry found = recipeEntry;
            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;
                if (found != null) {
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(v -> {
                        Bundle args = new Bundle();
                        args.putLong("recipeId", found.getRecipeId());
                        args.putString("recipeName", found.getFoodName());
                        args.putBoolean("viewOnly", true);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_nutritionFragment_to_recipeDetailFragment, args);
                    });
                } else {
                    button.setVisibility(View.GONE);
                }
            });
        });
    }

    private boolean hasMealSlot(List<MealLog> logs, String slot) {
        for (MealLog log : logs) {
            if (slot.equals(log.getMealSlot())) return true;
        }
        return false;
    }

    private String formatMacros(MealLog log) {
        return String.format("%.0f kcal  P:%.0fg  C:%.0fg  G:%.0fg",
                log.getTotalCaloriesKcal(), log.getTotalProteinG(),
                log.getTotalCarbsG(), log.getTotalFatG());
    }

    private void loadWeeklyHistory() {
        nutritionRepository.getWeeklySummary(logs -> {
            Map<String, float[]> byDate = new LinkedHashMap<>();
            for (MealLog log : logs) {
                byDate.computeIfAbsent(log.getDate(), k -> new float[2]);
                byDate.get(log.getDate())[0] += log.getTotalCaloriesKcal();
                byDate.get(log.getDate())[1] += log.getTotalProteinG();
            }

            List<WeeklyHistoryAdapter.DaySummary> summaries = new ArrayList<>();
            for (Map.Entry<String, float[]> e : byDate.entrySet()) {
                summaries.add(new WeeklyHistoryAdapter.DaySummary(
                        e.getKey(), e.getValue()[0], e.getValue()[1]));
            }

            requireActivity().runOnUiThread(() -> {
                if (binding != null) weeklyHistoryAdapter.setItems(summaries);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
