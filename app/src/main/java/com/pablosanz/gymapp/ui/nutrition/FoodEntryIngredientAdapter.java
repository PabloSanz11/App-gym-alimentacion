package com.pablosanz.gymapp.ui.nutrition;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.FoodEntryIngredient;

import java.util.List;

/** Editor de ingredientes para una entrada de comida puntual de un día (snapshot
 *  independiente de la receta original). Las cantidades aquí ya son las del día,
 *  por lo que no hay división por porciones como en EditableIngredientAdapter. */
public class FoodEntryIngredientAdapter extends RecyclerView.Adapter<FoodEntryIngredientAdapter.ViewHolder> {

    public interface OnChangeListener { void onChange(); }

    public interface OnPersistListener {
        void onIngredientEdited(FoodEntryIngredient ingredient);
        void onIngredientDeleted(FoodEntryIngredient ingredient);
    }

    private final List<FoodEntryIngredient> items;
    private final OnChangeListener listener;
    private OnPersistListener persistListener;

    public FoodEntryIngredientAdapter(List<FoodEntryIngredient> items, OnChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setOnPersistListener(OnPersistListener persistListener) {
        this.persistListener = persistListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_editable_ingredient, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodEntryIngredient ing = items.get(position);

        holder.etName.removeTextChangedListener(holder.nameWatcher);
        holder.etQty.removeTextChangedListener(holder.qtyWatcher);

        holder.etName.setText(ing.getIngredientName());
        holder.etQty.setText(String.valueOf((int) ing.getQuantityG()));

        float origQty = ing.getQuantityG() > 0 ? ing.getQuantityG() : 1;
        float proteinPer = ing.getProteinG() / origQty;
        float carbsPer = ing.getCarbsG() / origQty;
        float calsPer = ing.getCaloriesKcal() / origQty;
        float fatPer = ing.getFatG() / origQty;

        holder.tvMacros.setText(String.format("P:%.0f C:%.0f %.0fkcal",
                ing.getProteinG(), ing.getCarbsG(), ing.getCaloriesKcal()));

        holder.nameWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) items.get(pos).setIngredientName(s.toString());
            }
        };

        holder.qtyWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                try {
                    float newQty = Float.parseFloat(s.toString());
                    FoodEntryIngredient i = items.get(pos);
                    i.setQuantityG(newQty);
                    i.setProteinG(proteinPer * newQty);
                    i.setCarbsG(carbsPer * newQty);
                    i.setCaloriesKcal(calsPer * newQty);
                    i.setFatG(fatPer * newQty);
                    holder.tvMacros.setText(String.format("P:%.0f C:%.0f %.0fkcal",
                            i.getProteinG(), i.getCarbsG(), i.getCaloriesKcal()));
                    listener.onChange();
                } catch (NumberFormatException ignored) {}
            }
        };

        holder.etName.addTextChangedListener(holder.nameWatcher);
        holder.etQty.addTextChangedListener(holder.qtyWatcher);

        View.OnFocusChangeListener persistOnBlur = (v, hasFocus) -> {
            if (hasFocus) return;
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            if (persistListener != null) persistListener.onIngredientEdited(items.get(pos));
        };
        holder.etName.setOnFocusChangeListener(persistOnBlur);
        holder.etQty.setOnFocusChangeListener(persistOnBlur);

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                FoodEntryIngredient removed = items.remove(pos);
                notifyItemRemoved(pos);
                listener.onChange();
                if (persistListener != null) persistListener.onIngredientDeleted(removed);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etName, etQty;
        TextView tvMacros;
        ImageButton btnDelete;
        TextWatcher nameWatcher, qtyWatcher;

        ViewHolder(@NonNull View v) {
            super(v);
            etName = v.findViewById(R.id.et_ingredient_name);
            etQty = v.findViewById(R.id.et_ingredient_qty);
            tvMacros = v.findViewById(R.id.tv_ingredient_macros);
            btnDelete = v.findViewById(R.id.btn_delete_ingredient);
        }
    }
}
