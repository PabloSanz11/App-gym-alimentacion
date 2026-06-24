package com.pablosanz.gymapp.ui.nutrition;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.api.FoodProduct;
import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.model.FavoriteFood;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.FoodEntryIngredient;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.model.MealPlanEntry;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentMealLogBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MealLogFragment extends Fragment {

    private FragmentMealLogBinding binding;
    private NutritionRepository nutritionRepository;
    private FoodSearchAdapter adapter;
    private FoodProduct selectedProduct;
    private String mealSlot;
    private String date;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMealLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mealSlot = getArguments().getString("mealSlot", "desayuno");
            date = getArguments().getString("date");
        }

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        binding.toolbarMealLog.setTitle(capitalize(mealSlot));
        binding.toolbarMealLog.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        adapter = new FoodSearchAdapter(new ArrayList<>(), product -> {
            selectedProduct = product;
            binding.etQuantity.setEnabled(true);
            binding.btnSaveFoodEntry.setEnabled(true);
            String name = product.getProduct_name();
            binding.tvSelectedFood.setText("Seleccionado: " + (name != null ? name : "Producto"));
            binding.tvSelectedFood.setVisibility(View.VISIBLE);
        });

        binding.rvFoodResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFoodResults.setAdapter(adapter);

        // Recipes — grid 2 columnas
        RecipeGridAdapter recipeGridAdapter = new RecipeGridAdapter(recipe -> {
            Bundle args = new Bundle();
            args.putLong("recipeId", recipe.getId());
            args.putString("recipeName", recipe.getName());
            args.putString("mealSlot", mealSlot);
            args.putString("date", date);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_mealLogFragment_to_recipeDetailFragment, args);
        });
        binding.rvRecipes.setLayoutManager(
                new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
        binding.rvRecipes.setAdapter(recipeGridAdapter);
        nutritionRepository.getRecipesByMealSlot(mealSlot, recipes -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            recipeGridAdapter.setItems(recipes);
        }));

        binding.btnOtherRecipes.setOnClickListener(v -> showAllRecipesDialog());

        // Frecuentes
        binding.rvFavorites.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        nutritionRepository.getFavoriteFoodsByMealSlot(mealSlot, favorites -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            FavoriteFoodAdapter favAdapter = new FavoriteFoodAdapter(favorites, this::quickAddFavorite);
            binding.rvFavorites.setAdapter(favAdapter);
        }));

        binding.etFoodSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                final String query = s.toString().trim();
                if (query.length() < 2) return;

                searchRunnable = () -> searchFood(query);
                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnSaveFoodEntry.setEnabled(false);
        binding.etQuantity.setEnabled(false);
        binding.btnSaveFoodEntry.setOnClickListener(v -> saveFoodEntry());

        loadWeeklyPreselection();
    }

    private void showAllRecipesDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_all_recipes, null);
        androidx.recyclerview.widget.RecyclerView rvAllRecipes = dialogView.findViewById(R.id.rv_all_recipes);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Otras recetas")
                .setView(dialogView)
                .setNegativeButton("Cerrar", null)
                .create();

        AllRecipesAdapter adapter = new AllRecipesAdapter(recipe -> {
            dialog.dismiss();
            Bundle args = new Bundle();
            args.putLong("recipeId", recipe.getId());
            args.putString("recipeName", recipe.getName());
            args.putString("mealSlot", mealSlot);
            args.putString("date", date);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_mealLogFragment_to_recipeDetailFragment, args);
        });
        rvAllRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllRecipes.setAdapter(adapter);

        nutritionRepository.getAllRecipes(recipes -> requireActivity().runOnUiThread(() -> adapter.setItems(recipes)));

        dialog.show();
    }

    private void loadWeeklyPreselection() {
        String day = DateUtils.dayOfWeekName(date);
        if (day == null) return;
        nutritionRepository.getMealPlanForDaySlot(day, mealSlot, entry -> requireActivity().runOnUiThread(() -> {
            if (binding == null || entry == null) return;
            binding.tvPreselectionName.setText(entry.getRecipeName());
            nutritionRepository.getRecipeById(entry.getRecipeId(), recipe -> {
                int servings = recipe != null ? Math.max(1, recipe.getServings()) : 1;
                nutritionRepository.getRecipeIngredients(entry.getRecipeId(), ingredients -> requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    float p = 0, c = 0, cal = 0, f = 0;
                    for (RecipeIngredient ing : ingredients) {
                        p += ing.getProteinG();
                        c += ing.getCarbsG();
                        cal += ing.getCaloriesKcal();
                        f += ing.getFatG();
                    }
                    p /= servings; c /= servings; cal /= servings; f /= servings;
                    binding.tvPreselectionMacros.setText(
                            Math.round(cal) + " kcal · " + Math.round(p) + "g prot · " + Math.round(c) + "g carbs");
                }));
            });
            binding.cardWeeklyPreselection.setVisibility(View.VISIBLE);
            binding.btnAddPreselection.setOnClickListener(v -> quickAddPreselection(entry));
        }));
    }

    private void quickAddPreselection(MealPlanEntry entry) {
        nutritionRepository.getRecipeById(entry.getRecipeId(), recipe -> {
            int servings = recipe != null ? Math.max(1, recipe.getServings()) : 1;
            nutritionRepository.getRecipeIngredients(entry.getRecipeId(), ingredients -> {
            float p = 0, c = 0, cal = 0, f = 0;
            for (RecipeIngredient ing : ingredients) {
                p += ing.getProteinG();
                c += ing.getCarbsG();
                cal += ing.getCaloriesKcal();
                f += ing.getFatG();
            }
            final float protein = p / servings, carbs = c / servings, calories = cal / servings, fat = f / servings;
            nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
                FoodEntry foodEntry = new FoodEntry(
                        mealLog.getId(), entry.getRecipeName(), "",
                        protein, carbs, calories, fat, 0);
                foodEntry.setRecipeId(entry.getRecipeId());
                nutritionRepository.insertFoodEntry(foodEntry, entryId -> {
                    List<FoodEntryIngredient> snapshot = new ArrayList<>();
                    for (RecipeIngredient ing : ingredients) {
                        snapshot.add(new FoodEntryIngredient(entryId, ing.getIngredientName(),
                                ing.getQuantityG() / servings, ing.getProteinG() / servings,
                                ing.getCarbsG() / servings, ing.getCaloriesKcal() / servings,
                                ing.getFatG() / servings));
                    }
                    nutritionRepository.insertFoodEntryIngredients(snapshot, null);
                });
                mealLog.setTotalProteinG(mealLog.getTotalProteinG() + protein);
                mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + carbs);
                mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + calories);
                mealLog.setTotalFatG(mealLog.getTotalFatG() + fat);
                nutritionRepository.updateMealLog(mealLog);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), entry.getRecipeName() + " agregado", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                });
            });
            });
        });
    }

    private void searchFood(String query) {
        binding.progressSearch.setVisibility(View.VISIBLE);
        nutritionRepository.searchFood(query, new NutritionRepository.OnFoodSearchCallback() {
            @Override
            public void onSuccess(FoodSearchResponse response) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressSearch.setVisibility(View.GONE);
                    if (response.getProducts() != null) {
                        adapter.updateData(filterUsableProducts(response.getProducts()));
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressSearch.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al buscar: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private java.util.List<FoodProduct> filterUsableProducts(java.util.List<FoodProduct> products) {
        java.util.List<FoodProduct> result = new ArrayList<>();
        for (FoodProduct p : products) {
            if (p.getProduct_name() == null || p.getProduct_name().trim().isEmpty()) continue;
            if (p.getNutriments() == null) continue;
            result.add(p);
        }
        return result;
    }

    private void saveFoodEntry() {
        if (selectedProduct == null) return;

        String quantityStr = binding.etQuantity.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa la cantidad en gramos", Toast.LENGTH_SHORT).show();
            return;
        }

        float quantityG;
        try {
            quantityG = Float.parseFloat(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        float proteins100 = selectedProduct.getNutriments() != null ?
                selectedProduct.getNutriments().getProteins_100g() : 0;
        float carbs100 = selectedProduct.getNutriments() != null ?
                selectedProduct.getNutriments().getCarbohydrates_100g() : 0;
        float cals100 = selectedProduct.getNutriments() != null ?
                selectedProduct.getNutriments().getEnergy_100g() : 0;
        float fat100 = selectedProduct.getNutriments() != null ?
                selectedProduct.getNutriments().getFat_100g() : 0;

        float protein = (proteins100 * quantityG) / 100f;
        float carbs = (carbs100 * quantityG) / 100f;
        float calories = (cals100 * quantityG) / 100f;
        float fat = (fat100 * quantityG) / 100f;

        nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
            FoodEntry entry = new FoodEntry(
                    mealLog.getId(),
                    selectedProduct.getProduct_name(),
                    selectedProduct.getCode(),
                    protein, carbs, calories, fat, quantityG);
            nutritionRepository.insertFoodEntry(entry);

            // Update meal log totals
            mealLog.setTotalProteinG(mealLog.getTotalProteinG() + protein);
            mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + carbs);
            mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + calories);
            mealLog.setTotalFatG(mealLog.getTotalFatG() + fat);
            nutritionRepository.updateMealLog(mealLog);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Alimento agregado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            });
        });
    }

    private void quickAddFavorite(FavoriteFood fav) {
        nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
            FoodEntry entry = new FoodEntry(
                    mealLog.getId(), fav.getName(), "",
                    fav.getProteinG(), fav.getCarbsG(), fav.getCaloriesKcal(),
                    fav.getFatG(), fav.getDefaultQuantityG());
            nutritionRepository.insertFoodEntry(entry);
            mealLog.setTotalProteinG(mealLog.getTotalProteinG() + fav.getProteinG());
            mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + fav.getCarbsG());
            mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + fav.getCaloriesKcal());
            mealLog.setTotalFatG(mealLog.getTotalFatG() + fav.getFatG());
            nutritionRepository.updateMealLog(mealLog);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), fav.getName() + " agregado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            });
        });
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
        binding = null;
    }
}
