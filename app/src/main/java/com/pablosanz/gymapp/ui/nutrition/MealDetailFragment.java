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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.api.FoodProduct;
import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.FoodEntryIngredient;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentMealDetailBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MealDetailFragment extends Fragment {

    private FragmentMealDetailBinding binding;
    private NutritionRepository repo;
    private FoodEntryAdapter adapter;
    private final List<FoodEntry> entries = new ArrayList<>();

    private String mealSlot;
    private String date;
    private long mealLogId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMealDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mealSlot = getArguments().getString("mealSlot", "desayuno");
            date = getArguments().getString("date", DateUtils.today());
            mealLogId = getArguments().getInt("mealLogId", -1);
        }

        repo = new NutritionRepository(requireActivity().getApplication());

        String title = capitalize(mealSlot) + "  ·  " + DateUtils.formatForDisplay(date);
        binding.toolbarMealDetail.setTitle(title);
        binding.toolbarMealDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        adapter = new FoodEntryAdapter(entries,
                entry -> deleteEntry(entry),
                (entry, newQty) -> editEntry(entry, newQty));
        adapter.setOnEditIngredientsListener(this::showIngredientEditorDialog);

        binding.rvFoodEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFoodEntries.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.rvFoodEntries.setAdapter(adapter);

        binding.fabAddFood.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("mealSlot", mealSlot);
            args.putString("date", date);
            Navigation.findNavController(v)
                    .navigate(R.id.action_mealDetailFragment_to_mealLogFragment, args);
        });

        loadEntries();
    }

    private void loadEntries() {
        if (mealLogId < 0) {
            // No log yet — empty state
            showEmpty(true);
            return;
        }
        repo.getFoodEntriesByMealLog(mealLogId, result ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    adapter.setItems(result);
                    showEmpty(result.isEmpty());
                    updateSummary(result);
                }));
    }

    private void deleteEntry(FoodEntry entry) {
        repo.deleteFoodEntry(entry, () ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                    loadEntries();
                }));
    }

    private void editEntry(FoodEntry entry, float newQty) {
        repo.updateFoodEntryQuantity(entry, newQty, () ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    loadEntries();
                }));
    }

    /** Editor de ingredientes para una entrada de comida puntual de este día — permite
     *  agregar/editar/quitar ingredientes sin afectar la receta original ni otros días. */
    private void showIngredientEditorDialog(FoodEntry entry) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_entry_ingredients, null);
        RecyclerView rv = dialogView.findViewById(R.id.rv_entry_ingredients);
        android.widget.Button btnAdd = dialogView.findViewById(R.id.btn_add_entry_ingredient);

        List<FoodEntryIngredient> ingredients = new ArrayList<>();
        FoodEntryIngredientAdapter ingAdapter = new FoodEntryIngredientAdapter(ingredients,
                () -> updateEntryMacrosPreview(entry, ingredients));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(ingAdapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(entry.getFoodName())
                .setView(dialogView)
                .setPositiveButton("Listo", (d, w) -> loadEntries())
                .create();

        ingAdapter.setOnPersistListener(new FoodEntryIngredientAdapter.OnPersistListener() {
            @Override
            public void onIngredientEdited(FoodEntryIngredient ingredient) {
                repo.updateFoodEntryIngredient(ingredient, null);
            }

            @Override
            public void onIngredientDeleted(FoodEntryIngredient ingredient) {
                repo.deleteFoodEntryIngredient(ingredient, null);
            }
        });

        repo.getFoodEntryIngredients(entry.getId(), result ->
                requireActivity().runOnUiThread(() -> {
                    ingredients.clear();
                    ingredients.addAll(result);
                    ingAdapter.notifyDataSetChanged();
                }));

        btnAdd.setOnClickListener(v -> showSearchIngredientDialog(entry, ingredients, ingAdapter));

        dialog.show();
    }

    private void updateEntryMacrosPreview(FoodEntry entry, List<FoodEntryIngredient> ingredients) {
        // Los totales reales se recalculan y persisten en el repositorio tras cada
        // edición/borrado/alta; aquí solo se refresca la lista visible en pantalla.
    }

    private void showSearchIngredientDialog(FoodEntry entry, List<FoodEntryIngredient> ingredients,
                                            FoodEntryIngredientAdapter ingAdapter) {
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
            promptQuantityAndAddIngredient(entry, product, ingredients, ingAdapter);
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
                    repo.searchFood(query, new NutritionRepository.OnFoodSearchCallback() {
                        @Override
                        public void onSuccess(FoodSearchResponse response) {
                            requireActivity().runOnUiThread(() -> {
                                progress.setVisibility(View.GONE);
                                if (response.getProducts() != null) adapter.updateData(filterUsableProducts(response.getProducts()));
                            });
                        }
                        @Override
                        public void onError(String error) {
                            requireActivity().runOnUiThread(() -> {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Error al buscar: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                };
                searchHandler.postDelayed(searchRunnable[0], 500);
            }
        });

        dialog.show();
        etSearch.requestFocus();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private List<FoodProduct> filterUsableProducts(List<FoodProduct> products) {
        List<FoodProduct> result = new ArrayList<>();
        for (FoodProduct p : products) {
            if (p.getProduct_name() == null || p.getProduct_name().trim().isEmpty()) continue;
            if (p.getNutriments() == null) continue;
            result.add(p);
        }
        return result;
    }

    private void promptQuantityAndAddIngredient(FoodEntry entry, FoodProduct product,
                                                List<FoodEntryIngredient> ingredients,
                                                FoodEntryIngredientAdapter ingAdapter) {
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

                    FoodEntryIngredient ingredient = new FoodEntryIngredient(entry.getId(), name, qty,
                            (proteins100 * qty) / 100f, (carbs100 * qty) / 100f,
                            (cals100 * qty) / 100f, (fat100 * qty) / 100f);
                    ingredients.add(ingredient);
                    ingAdapter.notifyItemInserted(ingredients.size() - 1);
                    repo.insertFoodEntryIngredient(ingredient, null);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateSummary(List<FoodEntry> list) {
        float cal = 0, p = 0, c = 0, f = 0;
        for (FoodEntry e : list) {
            cal += e.getCaloriesKcal();
            p += e.getProteinG();
            c += e.getCarbsG();
            f += e.getFatG();
        }
        binding.tvDetailCalories.setText(String.format("%.0f", cal));
        binding.tvDetailProtein.setText(String.format("%.0f", p));
        binding.tvDetailCarbs.setText(String.format("%.0f", c));
        binding.tvDetailFat.setText(String.format("%.0f", f));
    }

    private void showEmpty(boolean empty) {
        binding.tvEmptyEntries.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvFoodEntries.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
