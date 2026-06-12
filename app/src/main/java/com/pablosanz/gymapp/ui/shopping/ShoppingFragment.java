package com.pablosanz.gymapp.ui.shopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.databinding.FragmentShoppingBinding;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {

    private static final String PREFS_NAME = "shopping_checks";
    private FragmentShoppingBinding binding;
    private List<ShoppingItem> items;
    private ShoppingItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShoppingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        items = buildShoppingList();
        restoreChecks();

        adapter = new ShoppingItemAdapter(items, this::updateSummary);
        binding.rvShopping.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvShopping.setAdapter(adapter);

        binding.btnResetShopping.setOnClickListener(v -> resetAll());
        updateSummary();
    }

    private List<ShoppingItem> buildShoppingList() {
        List<ShoppingItem> list = new ArrayList<>();
        String cat;

        // Proteínas
        cat = "Proteínas";
        list.add(new ShoppingItem("Pechuga de pollo", "1 kg", 90, cat));
        list.add(new ShoppingItem("Carne molida de res", "700 g", 85, cat));
        list.add(new ShoppingItem("Atún en agua", "3 latas (140g c/u)", 45, cat));
        list.add(new ShoppingItem("Huevos", "12 piezas", 42, cat));
        list.add(new ShoppingItem("Proteína en polvo (whey)", "1 kg", 420, cat));
        list.add(new ShoppingItem("Jamón de pavo", "200 g", 35, cat));

        // Lácteos
        cat = "Lácteos";
        list.add(new ShoppingItem("Leche deslactosada", "2 litros", 40, cat));
        list.add(new ShoppingItem("Yogurt griego natural", "500 g", 50, cat));
        list.add(new ShoppingItem("Queso cottage", "400 g", 55, cat));

        // Carbohidratos
        cat = "Carbohidratos";
        list.add(new ShoppingItem("Arroz integral", "1 kg", 28, cat));
        list.add(new ShoppingItem("Avena integral", "500 g", 22, cat));
        list.add(new ShoppingItem("Papa blanca", "1 kg", 18, cat));
        list.add(new ShoppingItem("Tortillas de maíz", "1 paquete (30 pzas)", 25, cat));
        list.add(new ShoppingItem("Pan integral (Bimbo)", "1 bolsa", 40, cat));
        list.add(new ShoppingItem("Camote", "500 g", 15, cat));

        // Verduras y frutas
        cat = "Verduras y Frutas";
        list.add(new ShoppingItem("Brócoli", "500 g", 18, cat));
        list.add(new ShoppingItem("Espinacas", "200 g", 18, cat));
        list.add(new ShoppingItem("Jitomate", "4 piezas", 15, cat));
        list.add(new ShoppingItem("Cebolla blanca", "3 piezas", 12, cat));
        list.add(new ShoppingItem("Ajo", "1 cabeza", 8, cat));
        list.add(new ShoppingItem("Pimiento morrón", "2 piezas", 20, cat));
        list.add(new ShoppingItem("Limones", "6 piezas", 10, cat));
        list.add(new ShoppingItem("Plátano", "4 piezas", 12, cat));

        // Grasas saludables
        cat = "Grasas Saludables";
        list.add(new ShoppingItem("Aceite de oliva", "250 ml", 60, cat));
        list.add(new ShoppingItem("Aguacate", "3 piezas", 36, cat));
        list.add(new ShoppingItem("Mantequilla de maní natural", "400 g", 75, cat));

        // Extras y condimentos
        cat = "Extras y Condimentos";
        list.add(new ShoppingItem("Sal y pimienta", "1 paquete c/u", 15, cat));
        list.add(new ShoppingItem("Sazonador Maggi / Knorr", "1 frasco", 22, cat));
        list.add(new ShoppingItem("Salsa Valentina", "1 botella", 18, cat));
        list.add(new ShoppingItem("Agua purificada", "Garrafón 20L", 28, cat));

        return list;
    }

    private void updateSummary() {
        if (binding == null) return;
        int checked = 0;
        float total = 0;
        float totalAll = 0;
        for (ShoppingItem item : items) {
            totalAll += item.priceEstimated;
            if (item.checked) {
                checked++;
                total += item.priceEstimated;
            }
        }
        binding.tvShoppingTotal.setText(String.format("Total estimado: $%.0f MXN", totalAll));
        binding.tvShoppingProgress.setText(checked + " / " + items.size() + " productos");
        int pct = items.isEmpty() ? 0 : (checked * 100 / items.size());
        binding.progressShopping.setProgress(pct);
        saveChecks();
    }

    private void resetAll() {
        for (ShoppingItem item : items) item.checked = false;
        adapter.notifyDataSetChanged();
        saveChecks();
        updateSummary();
    }

    private void saveChecks() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < items.size(); i++) {
            editor.putBoolean("item_" + i, items.get(i).checked);
        }
        editor.apply();
    }

    private void restoreChecks() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).checked = prefs.getBoolean("item_" + i, false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
