package com.pablosanz.gymapp.ui.nutrition;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;
import com.pablosanz.gymapp.data.model.ShoppingListItem;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.DialogEditShoppingItemBinding;

/** Modal para editar el precio y el súper de un ingrediente de la lista de compras. */
public class ShoppingItemEditDialog {

    public interface OnSaved {
        void onSaved(ShoppingListItem item);
    }

    public static void show(Context context, LayoutInflater inflater, NutritionRepository repository,
                             ShoppingListItem item, OnSaved onSaved) {
        DialogEditShoppingItemBinding binding = DialogEditShoppingItemBinding.inflate(inflater);

        binding.tvEditItemName.setText(item.getIngredientName());
        if (item.getEstimatedCostMxn() > 0) {
            binding.etEditItemPrice.setText(trimTrailingZero(item.getEstimatedCostMxn()));
        }
        selectChipForStore(binding, item.getStore());

        new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setPositiveButton("Guardar", (d, w) -> {
                    float cost = 0f;
                    String priceStr = binding.etEditItemPrice.getText() != null
                            ? binding.etEditItemPrice.getText().toString().trim() : "";
                    if (!priceStr.isEmpty()) {
                        try {
                            cost = Float.parseFloat(priceStr);
                        } catch (NumberFormatException ignored) {}
                    }
                    String store = selectedStore(binding);

                    item.setEstimatedCostMxn(cost);
                    item.setStore(store);
                    repository.saveIngredientPriceInfo(item.getIngredientName(), cost, store, null);
                    if (onSaved != null) onSaved.onSaved(item);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private static void selectChipForStore(DialogEditShoppingItemBinding binding, String store) {
        if (store == null) return;
        Chip chip = chipForStore(binding, store);
        if (chip != null) chip.setChecked(true);
    }

    private static String selectedStore(DialogEditShoppingItemBinding binding) {
        int checkedId = binding.chipGroupStore.getCheckedChipId();
        if (checkedId == binding.chipStoreHeb.getId()) return "HEB";
        if (checkedId == binding.chipStoreWalmart.getId()) return "Walmart";
        if (checkedId == binding.chipStoreComer.getId()) return "Comer";
        if (checkedId == binding.chipStoreLocal.getId()) return "Local";
        return null;
    }

    private static Chip chipForStore(DialogEditShoppingItemBinding binding, String store) {
        switch (store) {
            case "HEB": return binding.chipStoreHeb;
            case "Walmart": return binding.chipStoreWalmart;
            case "Comer": return binding.chipStoreComer;
            case "Local": return binding.chipStoreLocal;
            default: return null;
        }
    }

    private static String trimTrailingZero(float value) {
        return value == Math.floor(value) ? String.valueOf((int) value) : String.valueOf(value);
    }
}
