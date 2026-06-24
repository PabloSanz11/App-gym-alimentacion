package com.pablosanz.gymapp.ui.nutrition;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.FoodEntry;

import java.util.List;

public class FoodEntryAdapter extends RecyclerView.Adapter<FoodEntryAdapter.ViewHolder> {

    public interface OnDeleteListener { void onDelete(FoodEntry entry); }
    public interface OnEditListener { void onEdit(FoodEntry entry, float newQty); }
    public interface OnEditIngredientsListener { void onEditIngredients(FoodEntry entry); }

    private final List<FoodEntry> items;
    private final OnDeleteListener onDelete;
    private final OnEditListener onEdit;
    private OnEditIngredientsListener onEditIngredients;

    public FoodEntryAdapter(List<FoodEntry> items, OnDeleteListener onDelete, OnEditListener onEdit) {
        this.items = items;
        this.onDelete = onDelete;
        this.onEdit = onEdit;
    }

    /** Para entradas que vienen de una receta, el ícono de lápiz abre el editor de
     *  ingredientes (agregar/quitar) en vez del simple cambio de cantidad. */
    public void setOnEditIngredientsListener(OnEditIngredientsListener listener) {
        this.onEditIngredients = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_entry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        FoodEntry entry = items.get(position);
        h.tvName.setText(entry.getFoodName() != null ? entry.getFoodName() : "Alimento");
        h.tvMacros.setText(String.format("%.0fg  ·  %.0f kcal  ·  P:%.0fg  C:%.0fg  G:%.0fg",
                entry.getQuantityG(), entry.getCaloriesKcal(),
                entry.getProteinG(), entry.getCarbsG(), entry.getFatG()));

        h.btnEdit.setOnClickListener(v -> {
            if (entry.getRecipeId() > 0 && onEditIngredients != null) {
                onEditIngredients.onEditIngredients(entry);
            } else {
                showEditDialog(v, entry);
            }
        });
        h.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar alimento")
                    .setMessage("¿Eliminar \"" + entry.getFoodName() + "\"?")
                    .setPositiveButton("Eliminar", (d, w) -> onDelete.onDelete(entry))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void showEditDialog(View anchor, FoodEntry entry) {
        EditText et = new EditText(anchor.getContext());
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et.setText(String.format("%.0f", entry.getQuantityG()));
        et.setHint("Cantidad en gramos");
        int pad = (int) (16 * anchor.getContext().getResources().getDisplayMetrics().density);
        et.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(anchor.getContext())
                .setTitle("Editar cantidad")
                .setMessage(entry.getFoodName())
                .setView(et)
                .setPositiveButton("Guardar", (d, w) -> {
                    String s = et.getText().toString().trim();
                    if (!s.isEmpty()) {
                        try {
                            float qty = Float.parseFloat(s);
                            if (qty > 0) onEdit.onEdit(entry, qty);
                        } catch (NumberFormatException ignored) {}
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void setItems(List<FoodEntry> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMacros;
        ImageButton btnEdit, btnDelete;

        ViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_entry_name);
            tvMacros = v.findViewById(R.id.tv_entry_macros);
            btnEdit = v.findViewById(R.id.btn_edit_entry);
            btnDelete = v.findViewById(R.id.btn_delete_entry);
        }
    }
}
