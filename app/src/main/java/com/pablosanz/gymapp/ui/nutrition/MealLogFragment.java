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

import com.pablosanz.gymapp.data.api.FoodProduct;
import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.model.FavoriteFood;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentMealLogBinding;

import java.util.ArrayList;

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

        // Favorite / frequent foods
        binding.rvFavorites.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        nutritionRepository.getFavoriteFoods(favorites -> requireActivity().runOnUiThread(() -> {
            FavoriteFoodAdapter favAdapter = new FavoriteFoodAdapter(favorites, this::quickAddFavorite);
            binding.rvFavorites.setAdapter(favAdapter);
        }));

        // Mexican recipes
        binding.rvRecipes.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        nutritionRepository.getAllRecipes(recipes -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            RecipeCardAdapter recipeAdapter = new RecipeCardAdapter(recipes, recipe -> {
                Bundle args = new Bundle();
                args.putLong("recipeId", recipe.getId());
                args.putString("recipeName", recipe.getName());
                args.putString("mealSlot", mealSlot);
                args.putString("date", date);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_mealLogFragment_to_recipeDetailFragment, args);
            });
            binding.rvRecipes.setAdapter(recipeAdapter);
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
    }

    private void searchFood(String query) {
        binding.progressSearch.setVisibility(View.VISIBLE);
        nutritionRepository.searchFood(query, new NutritionRepository.OnFoodSearchCallback() {
            @Override
            public void onSuccess(FoodSearchResponse response) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressSearch.setVisibility(View.GONE);
                    if (response.getProducts() != null) {
                        adapter.updateData(response.getProducts());
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
