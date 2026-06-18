package com.pablosanz.gymapp.ui.nutrition;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String[] DAY_SHORT = {"Lun", "Mar", "Mié", "Jue", "Vie"};
    private static final String[] SLOTS = {"desayuno", "almuerzo", "merienda", "cena"};
    private static final String[] SLOT_LABELS = {"Desayuno", "Almuerzo", "Merienda", "Cena"};

    private FragmentMealPrepBinding binding;
    private NutritionRepository nutritionRepository;
    private final Map<String, MealPlanEntry> planMap = new HashMap<>();
    private List<Recipe> allRecipes = new ArrayList<>();

    /** {day, slot} of a cell waiting to be swapped, or null if no swap in progress. */
    private String[] swapPending;

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
        swapPending = null;
        binding.layoutMealPlanGrid.removeAllViews();

        // Header row: corner + day abbreviations
        LinearLayout headerRow = new LinearLayout(requireContext());
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        TextView corner = new TextView(requireContext());
        corner.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(72), LinearLayout.LayoutParams.WRAP_CONTENT));
        headerRow.addView(corner);
        for (String dayShort : DAY_SHORT) {
            TextView tv = new TextView(requireContext());
            tv.setText(dayShort);
            tv.setTextSize(12f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setTextColor(Color.parseColor("#1B3A6B"));
            tv.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(lp);
            headerRow.addView(tv);
        }
        binding.layoutMealPlanGrid.addView(headerRow);

        for (int s = 0; s < SLOTS.length; s++) {
            String slot = SLOTS[s];

            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(0, dpToPx(3), 0, dpToPx(3));

            TextView slotLabel = new TextView(requireContext());
            slotLabel.setText(SLOT_LABELS[s]);
            slotLabel.setTextSize(12f);
            slotLabel.setTextColor(Color.parseColor("#6C6C70"));
            slotLabel.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(72), LinearLayout.LayoutParams.WRAP_CONTENT));
            row.addView(slotLabel);

            for (String day : DAYS) {
                String key = day + "_" + slot;
                MealPlanEntry existing = planMap.get(key);

                TextView cell = new TextView(requireContext());
                cell.setGravity(android.view.Gravity.CENTER);
                cell.setMinHeight(dpToPx(48));
                cell.setTextSize(10f);
                cell.setPadding(dpToPx(2), dpToPx(4), dpToPx(2), dpToPx(4));
                cell.setMaxLines(3);

                LinearLayout.LayoutParams cellParams =
                        new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                cellParams.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
                cell.setLayoutParams(cellParams);

                updateCellAppearance(cell, existing, false);

                final String fDay = day;
                final String fSlot = slot;
                cell.setOnClickListener(v -> onCellClick(fDay, fSlot, cell));
                cell.setOnLongClickListener(v -> {
                    onCellLongClick(fDay, fSlot, cell);
                    return true;
                });

                row.addView(cell);
            }
            binding.layoutMealPlanGrid.addView(row);
        }
    }

    private void updateCellAppearance(TextView cell, MealPlanEntry existing, boolean highlighted) {
        cell.setText(existing != null ? shortenRecipeName(existing.getRecipeName()) : "+");
        cell.setTextColor(existing != null ? Color.WHITE : Color.parseColor("#9CA3AF"));
        cell.setTypeface(null, existing != null ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dpToPx(8));
        bg.setColor(existing != null ? Color.parseColor("#1B3A6B") : Color.parseColor("#F2F2F7"));
        if (highlighted) {
            bg.setStroke(dpToPx(2), Color.parseColor("#FF9500"));
        }
        cell.setBackground(bg);
    }

    private String shortenRecipeName(String name) {
        if (name == null) return "";
        return name.length() > 16 ? name.substring(0, 14) + "…" : name;
    }

    private void onCellClick(String day, String slot, TextView cell) {
        if (swapPending != null) {
            String[] pending = swapPending;
            swapPending = null;
            if (pending[0].equals(day) && pending[1].equals(slot)) {
                buildGrid();
                return;
            }
            swapCells(pending[0], pending[1], day, slot);
            return;
        }
        showRecipePicker(day, slot);
    }

    private void onCellLongClick(String day, String slot, TextView cell) {
        swapPending = new String[]{day, slot};
        updateCellAppearance(cell, planMap.get(day + "_" + slot), true);
        Toast.makeText(getContext(), "Selecciona otra comida para intercambiar", Toast.LENGTH_SHORT).show();
    }

    private void swapCells(String dayA, String slotA, String dayB, String slotB) {
        MealPlanEntry a = planMap.get(dayA + "_" + slotA);
        MealPlanEntry b = planMap.get(dayB + "_" + slotB);

        Runnable applyToB = () -> {
            if (a != null) {
                nutritionRepository.setMealPlan(dayB, slotB, a.getRecipeId(), a.getRecipeName(),
                        () -> requireActivity().runOnUiThread(this::loadPlanAndBuildGrid));
            } else {
                nutritionRepository.clearMealPlanSlot(dayB, slotB,
                        () -> requireActivity().runOnUiThread(this::loadPlanAndBuildGrid));
            }
        };

        if (b != null) {
            nutritionRepository.setMealPlan(dayA, slotA, b.getRecipeId(), b.getRecipeName(), applyToB::run);
        } else {
            nutritionRepository.clearMealPlanSlot(dayA, slotA, applyToB::run);
        }
    }

    private void showRecipePicker(String day, String slot) {
        String[] names = new String[allRecipes.size()];
        for (int i = 0; i < allRecipes.size(); i++) names[i] = allRecipes.get(i).getName();

        new AlertDialog.Builder(requireContext())
                .setTitle("Asignar " + slotLabel(slot) + " — " + day)
                .setItems(names, (d, index) -> {
                    Recipe selected = allRecipes.get(index);
                    applyRecipeToDay(day, slot, selected);
                    offerApplyToOtherDays(day, slot, selected);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String slotLabel(String slot) {
        for (int i = 0; i < SLOTS.length; i++) if (SLOTS[i].equals(slot)) return SLOT_LABELS[i];
        return slot;
    }

    private void applyRecipeToDay(String day, String slot, Recipe recipe) {
        nutritionRepository.setMealPlan(day, slot, recipe.getId(), recipe.getName(), () ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    planMap.put(day + "_" + slot, new MealPlanEntry(day, slot, recipe.getId(), recipe.getName()));
                    buildGrid();
                }));
    }

    private void offerApplyToOtherDays(String currentDay, String slot, Recipe recipe) {
        List<String> otherDays = new ArrayList<>();
        for (String d : DAYS) if (!d.equals(currentDay)) otherDays.add(d);
        boolean[] checked = new boolean[otherDays.size()];

        new AlertDialog.Builder(requireContext())
                .setTitle("¿Aplicar \"" + recipe.getName() + "\" también a otros días?")
                .setMultiChoiceItems(otherDays.toArray(new String[0]), checked,
                        (d, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton("Aplicar", (d, w) -> {
                    for (int i = 0; i < otherDays.size(); i++) {
                        if (checked[i]) applyRecipeToDay(otherDays.get(i), slot, recipe);
                    }
                })
                .setNegativeButton("No, gracias", null)
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
            Map<Long, Recipe> uniqueRecipes = new java.util.LinkedHashMap<>();
            for (MealPlanEntry e : plan) {
                if (uniqueRecipes.containsKey(e.getRecipeId())) continue;
                Recipe r = findRecipeById(e.getRecipeId());
                if (r != null) uniqueRecipes.put(e.getRecipeId(), r);
            }
            List<Recipe> ordered = new ArrayList<>(uniqueRecipes.values());
            ordered.sort((a, b) -> Integer.compare(cookPriority(a.getName()), cookPriority(b.getName())));

            int step = 1;
            for (Recipe r : ordered) {
                TextView header = new TextView(requireContext());
                header.setText(step + ". " + r.getName());
                header.setTextSize(14f);
                header.setTypeface(null, android.graphics.Typeface.BOLD);
                header.setTextColor(Color.parseColor("#1B3A6B"));
                header.setPadding(0, dpToPx(10), 0, dpToPx(2));
                binding.layoutPrepOrder.addView(header);

                String steps = r.getPrepSteps();
                TextView tvSteps = new TextView(requireContext());
                tvSteps.setText(steps != null && !steps.trim().isEmpty()
                        ? steps : "Cocina los ingredientes de la receta y porciona al terminar.");
                tvSteps.setTextSize(13f);
                tvSteps.setTextColor(Color.parseColor("#1C1C1E"));
                tvSteps.setLineSpacing(dpToPx(2), 1f);
                binding.layoutPrepOrder.addView(tvSteps);
                step++;
            }
        }
        binding.cardPrepOrder.setVisibility(View.VISIBLE);
    }

    private Recipe findRecipeById(long id) {
        for (Recipe r : allRecipes) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    /** Lower number = start cooking sooner (longer/slower-cooking dishes go first so they can simmer
     *  while quicker dishes and assembly happen in parallel, following the PDF's prep-order guidance). */
    private int cookPriority(String name) {
        String n = name.toLowerCase();
        if (n.contains("lenteja")) return 1;
        if (n.contains("picadillo") || n.contains("tinga")) return 2;
        if (n.contains("muffin")) return 2;
        if (n.contains("carne") || n.contains("res") || n.contains("salteado") || n.contains("pollo")) return 3;
        if (n.contains("chilaquiles") || n.contains("huevos")) return 4;
        return 5;
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
