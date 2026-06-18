package com.pablosanz.gymapp.ui.nutrition;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.api.FoodProduct;
import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentRecipeDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private NutritionRepository nutritionRepository;
    private long recipeId;
    private String mealSlot;
    private String date;
    private Recipe recipe;
    private List<RecipeIngredient> editableIngredients = new ArrayList<>();
    private EditableIngredientAdapter ingredientAdapter;
    private boolean viewOnly;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            recipeId = args.getLong("recipeId", 0L);
            mealSlot = args.getString("mealSlot", "desayuno");
            date = args.getString("date", "");
            viewOnly = args.getBoolean("viewOnly", false);
        }

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        binding.toolbarRecipeDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        ingredientAdapter = new EditableIngredientAdapter(editableIngredients, this::onIngredientChanged);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvIngredients.setAdapter(ingredientAdapter);

        binding.btnAddIngredient.setOnClickListener(v -> showSearchIngredientDialog());

        if (viewOnly) {
            binding.btnAddIngredient.setVisibility(View.GONE);
            binding.btnAddRecipe.setVisibility(View.GONE);
        }

        loadRecipeDetail();
    }

    private void loadRecipeDetail() {
        nutritionRepository.getRecipeById(recipeId, r -> {
            if (r == null || getActivity() == null) return;
            recipe = r;
            nutritionRepository.getRecipeIngredients(recipeId, ingredients -> {
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    editableIngredients.clear();
                    editableIngredients.addAll(ingredients);
                    binding.toolbarRecipeDetail.setTitle(recipe.getName());
                    ingredientAdapter.notifyDataSetChanged();
                    updateMacroSummary();
                });
            });
        });
    }

    private void onIngredientChanged() {
        requireActivity().runOnUiThread(this::updateMacroSummary);
    }

    private void showSearchIngredientDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_search_ingredient, null);
        EditText etSearch = dialogView.findViewById(R.id.et_search_ingredient);
        ProgressBar progress = dialogView.findViewById(R.id.progress_search_ingredient);
        RecyclerView rvResults = dialogView.findViewById(R.id.rv_search_ingredient_results);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Buscar ingrediente")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null)
                .create();

        FoodSearchAdapter adapter = new FoodSearchAdapter(new ArrayList<>(), product -> {
            dialog.dismiss();
            promptQuantityAndAdd(product);
        });
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvResults.setAdapter(adapter);

        Handler searchHandler = new Handler(Looper.getMainLooper());
        final Runnable[] searchRunnable = new Runnable[1];
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (searchRunnable[0] != null) searchHandler.removeCallbacks(searchRunnable[0]);
                final String query = s.toString().trim();
                if (query.length() < 2) return;
                searchRunnable[0] = () -> {
                    progress.setVisibility(View.VISIBLE);
                    nutritionRepository.searchFood(query, new NutritionRepository.OnFoodSearchCallback() {
                        @Override
                        public void onSuccess(FoodSearchResponse response) {
                            requireActivity().runOnUiThread(() -> {
                                progress.setVisibility(View.GONE);
                                if (response.getProducts() != null) adapter.updateData(filterUsableProducts(response.getProducts()));
                            });
                        }
                        @Override
                        public void onError(String error) {
                            requireActivity().runOnUiThread(() -> progress.setVisibility(View.GONE));
                        }
                    });
                };
                searchHandler.postDelayed(searchRunnable[0], 500);
            }
        });

        dialog.show();
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

    private void promptQuantityAndAdd(FoodProduct product) {
        EditText etQty = new EditText(requireContext());
        etQty.setHint("Cantidad en gramos");
        etQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etQty.setText("100");

        String name = product.getProduct_name() != null ? product.getProduct_name() : "Ingrediente";

        new AlertDialog.Builder(requireContext())
                .setTitle(name)
                .setMessage("¿Cuántos gramos vas a usar?")
                .setView(etQty)
                .setPositiveButton("Agregar", (d, w) -> {
                    float qty;
                    try {
                        qty = Float.parseFloat(etQty.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        qty = 100f;
                    }
                    float proteins100 = product.getNutriments() != null ? product.getNutriments().getProteins_100g() : 0;
                    float carbs100 = product.getNutriments() != null ? product.getNutriments().getCarbohydrates_100g() : 0;
                    float cals100 = product.getNutriments() != null ? product.getNutriments().getEnergy_100g() : 0;
                    float fat100 = product.getNutriments() != null ? product.getNutriments().getFat_100g() : 0;

                    RecipeIngredient ingredient = new RecipeIngredient(recipeId, name, qty,
                            (proteins100 * qty) / 100f, (carbs100 * qty) / 100f,
                            (cals100 * qty) / 100f, (fat100 * qty) / 100f);
                    editableIngredients.add(ingredient);
                    ingredientAdapter.notifyItemInserted(editableIngredients.size() - 1);
                    updateMacroSummary();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateMacroSummary() {
        float protein = 0, carbs = 0, cals = 0, fat = 0;
        for (RecipeIngredient ing : editableIngredients) {
            protein += ing.getProteinG();
            carbs += ing.getCarbsG();
            cals += ing.getCaloriesKcal();
            fat += ing.getFatG();
        }
        binding.tvRecipeCalories.setText(String.format("%.0f kcal", cals));
        binding.tvRecipeProtein.setText(String.format("P: %.0fg", protein));
        binding.tvRecipeCarbs.setText(String.format("C: %.0fg", carbs));
        binding.tvRecipeFat.setText(String.format("G: %.0fg", fat));

        final float fProtein = protein, fCarbs = carbs, fCals = cals, fFat = fat;
        binding.btnAddRecipe.setOnClickListener(v -> addToMeal(fProtein, fCarbs, fCals, fFat));
    }

    private void addToMeal(float protein, float carbs, float cals, float fat) {
        String name = recipe != null ? recipe.getName() : "Platillo";
        float totalQtyG = 0;
        for (RecipeIngredient ing : editableIngredients) totalQtyG += ing.getQuantityG();
        final float qtyG = totalQtyG;
        nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
            FoodEntry entry = new FoodEntry(
                    mealLog.getId(), name, "",
                    protein, carbs, cals, fat, qtyG);
            entry.setRecipeId(recipeId);
            nutritionRepository.insertFoodEntry(entry);
            mealLog.setTotalProteinG(mealLog.getTotalProteinG() + protein);
            mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + carbs);
            mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + cals);
            mealLog.setTotalFatG(mealLog.getTotalFatG() + fat);
            nutritionRepository.updateMealLog(mealLog);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), name + " agregado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
