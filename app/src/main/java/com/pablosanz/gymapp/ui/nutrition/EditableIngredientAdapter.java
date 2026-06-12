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
import com.pablosanz.gymapp.data.model.RecipeIngredient;

import java.util.List;

public class EditableIngredientAdapter extends RecyclerView.Adapter<EditableIngredientAdapter.ViewHolder> {

    public interface OnChangeListener { void onChange(); }

    private final List<RecipeIngredient> items;
    private final OnChangeListener listener;

    public EditableIngredientAdapter(List<RecipeIngredient> items, OnChangeListener listener) {
        this.items = items;
        this.listener = listener;
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
        RecipeIngredient ing = items.get(position);

        // Clear previous listeners before setting values
        holder.etName.removeTextChangedListener(holder.nameWatcher);
        holder.etQty.removeTextChangedListener(holder.qtyWatcher);

        holder.etName.setText(ing.getIngredientName());
        holder.etQty.setText(String.valueOf((int) ing.getQuantityG()));

        // Original macros per gram (stored at original quantity)
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
                if (pos != RecyclerView.NO_ID) items.get(pos).setIngredientName(s.toString());
            }
        };

        holder.qtyWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_ID) return;
                try {
                    float newQty = Float.parseFloat(s.toString());
                    RecipeIngredient i = items.get(pos);
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

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                items.remove(pos);
                notifyItemRemoved(pos);
                listener.onChange();
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
