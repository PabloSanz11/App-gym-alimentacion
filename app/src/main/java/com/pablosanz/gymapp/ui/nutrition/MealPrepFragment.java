package com.pablosanz.gymapp.ui.nutrition;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.data.model.MealPlanEntry;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.ShoppingListItem;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentMealPrepBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealPrepFragment extends Fragment {

    private static final String[] DAYS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
    private static final String[] SLOTS = {"desayuno", "almuerzo", "merienda", "cena"};
    private static final String[] SLOT_LABELS = {"Desayuno", "Almuerzo", "Merienda", "Cena"};

    private FragmentMealPrepBinding binding;
    private NutritionRepository nutritionRepository;
    private final Map<String, MealPlanEntry> planMap = new HashMap<>();
    private List<Recipe> allRecipes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMealPrepBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        binding.toolbarMealPrep.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        binding.btnGenerateShoppingList.setOnClickListener(v -> generatePlan());

        nutritionRepository.getAllRecipes(recipes -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            allRecipes = recipes;
            loadPlanAndBuildGrid();
        }));
    }

    private void loadPlanAndBuildGrid() {
        nutritionRepository.getMealPlan(entries -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            planMap.clear();
            for (MealPlanEntry e : entries) {
                planMap.put(e.getDayOfWeek() + "_" + e.getMealSlot(), e);
            }
            buildGrid();
        }));
    }

    private void buildGrid() {
        binding.layoutMealPlanGrid.removeAllViews();
        for (String day : DAYS) {
            TextView dayHeader = new TextView(requireContext());
            dayHeader.setText(day);
            dayHeader.setTextSize(14f);
            dayHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            dayHeader.setTextColor(Color.parseColor("#1B3A6B"));
            dayHeader.setPadding(0, dpToPx(12), 0, dpToPx(4));
            binding.layoutMealPlanGrid.addView(dayHeader);

            for (int i = 0; i < SLOTS.length; i++) {
                String slot = SLOTS[i];
                String key = day + "_" + slot;
                MealPlanEntry existing = planMap.get(key);

                LinearLayout row = new LinearLayout(requireContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                row.setPadding(0, dpToPx(6), 0, dpToPx(6));

                TextView tvSlot = new TextView(requireContext());
                tvSlot.setText(SLOT_LABELS[i]);
                tvSlot.setTextSize(13f);
                tvSlot.setTextColor(Color.parseColor("#6C6C70"));
                LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(
                        dpToPx(90), LinearLayout.LayoutParams.WRAP_CONTENT);
                tvSlot.setLayoutParams(slotParams);

                TextView tvRecipe = new TextView(requireContext());
                tvRecipe.setText(existing != null ? existing.getRecipeName() : "Tocar para asignar");
                tvRecipe.setTextSize(14f);
                tvRecipe.setTextColor(existing != null ? Color.parseColor("#1B3A6B") : Color.parseColor("#9CA3AF"));
                tvRecipe.setTypeface(null, existing != null ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
                LinearLayout.LayoutParams recipeParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                tvRecipe.setLayoutParams(recipeParams);

                row.addView(tvSlot);
                row.addView(tvRecipe);
                row.setOnClickListener(v -> showRecipePicker(day, slot, tvRecipe));
                binding.layoutMealPlanGrid.addView(row);
            }
        }
    }

    private void showRecipePicker(String day, String slot, TextView targetView) {
        String[] names = new String[allRecipes.size()];
        for (int i = 0; i < allRecipes.size(); i++) names[i] = allRecipes.get(i).getName();

        new AlertDialog.Builder(requireContext())
                .setTitle("Asignar platillo")
                .setItems(names, (d, index) -> {
                    Recipe selected = allRecipes.get(index);
                    nutritionRepository.setMealPlan(day, slot, selected.getId(), selected.getName(), () ->
                            requireActivity().runOnUiThread(() -> {
                                planMap.put(day + "_" + slot, new MealPlanEntry(day, slot, selected.getId(), selected.getName()));
                                targetView.setText(selected.getName());
                                targetView.setTextColor(Color.parseColor("#1B3A6B"));
                                targetView.setTypeface(null, android.graphics.Typeface.BOLD);
                            }));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void generatePlan() {
        nutritionRepository.generateShoppingList((items, plan) -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            showPrepOrder(plan);
            showShoppingList(items);
        }));
    }

    private void showPrepOrder(List<MealPlanEntry> plan) {
        binding.layoutPrepOrder.removeAllViews();
        if (plan.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("Asigna al menos un platillo para generar el orden.");
            empty.setTextColor(Color.parseColor("#6C6C70"));
            binding.layoutPrepOrder.addView(empty);
        } else {
            Map<Long, String> uniqueRecipes = new java.util.LinkedHashMap<>();
            for (MealPlanEntry e : plan) uniqueRecipes.put(e.getRecipeId(), e.getRecipeName());
            int step = 1;
            for (Map.Entry<Long, String> e : uniqueRecipes.entrySet()) {
                TextView tv = new TextView(requireContext());
                tv.setText(step + ". " + e.getValue());
                tv.setTextSize(14f);
                tv.setTextColor(Color.parseColor("#1C1C1E"));
                tv.setPadding(0, dpToPx(4), 0, dpToPx(4));
                binding.layoutPrepOrder.addView(tv);
                step++;
            }
        }
        binding.cardPrepOrder.setVisibility(View.VISIBLE);
    }

    private void showShoppingList(List<ShoppingListItem> items) {
        ShoppingListAdapter adapter = new ShoppingListAdapter(items, item ->
                nutritionRepository.updateShoppingItem(item));
        binding.rvShoppingList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvShoppingList.setAdapter(adapter);
        binding.cardShoppingList.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
