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

    public interface OnPersistListener {
        void onIngredientEdited(RecipeIngredient ingredient);
        void onIngredientDeleted(RecipeIngredient ingredient);
    }

    private final List<RecipeIngredient> items;
    private final OnChangeListener listener;
    private OnPersistListener persistListener;
    private int servings = 1;

    public EditableIngredientAdapter(List<RecipeIngredient> items, OnChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setOnPersistListener(OnPersistListener persistListener) {
        this.persistListener = persistListener;
    }

    /** Las cantidades de los ingredientes se guardan por el batch completo; se dividen
     * entre este valor para mostrar y editar la porción de un día. */
    public void setServings(int servings) {
        this.servings = Math.max(1, servings);
        notifyDataSetChanged();
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
        // Las cantidades/macros del ingrediente son del batch completo; se muestran
        // divididas entre las porciones para reflejar la cantidad de un día.
        float displayQty = ing.getQuantityG() / servings;
        float displayProtein = ing.getProteinG() / servings;
        float displayCarbs = ing.getCarbsG() / servings;
        float displayCals = ing.getCaloriesKcal() / servings;
        float displayFat = ing.getFatG() / servings;

        holder.etQty.setText(String.valueOf((int) displayQty));

        // Macros por gramo (en base a la cantidad del batch, no de la porción mostrada)
        float origQty = ing.getQuantityG() > 0 ? ing.getQuantityG() : 1;
        float proteinPer = ing.getProteinG() / origQty;
        float carbsPer = ing.getCarbsG() / origQty;
        float calsPer = ing.getCaloriesKcal() / origQty;
        float fatPer = ing.getFatG() / origQty;

        holder.tvMacros.setText(String.format("P:%.0f C:%.0f %.0fkcal",
                displayProtein, displayCarbs, displayCals));

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
                    float newDisplayQty = Float.parseFloat(s.toString());
                    float newBatchQty = newDisplayQty * servings;
                    RecipeIngredient i = items.get(pos);
                    i.setQuantityG(newBatchQty);
                    i.setProteinG(proteinPer * newBatchQty);
                    i.setCarbsG(carbsPer * newBatchQty);
                    i.setCaloriesKcal(calsPer * newBatchQty);
                    i.setFatG(fatPer * newBatchQty);
                    holder.tvMacros.setText(String.format("P:%.0f C:%.0f %.0fkcal",
                            i.getProteinG() / servings, i.getCarbsG() / servings, i.getCaloriesKcal() / servings));
                    listener.onChange();
                } catch (NumberFormatException ignored) {}
            }
        };

        holder.etName.addTextChangedListener(holder.nameWatcher);
        holder.etQty.addTextChangedListener(holder.qtyWatcher);

        View.OnFocusChangeListener persistOnBlur = (v, hasFocus) -> {
            if (hasFocus) return;
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            if (persistListener != null) persistListener.onIngredientEdited(items.get(pos));
        };
        holder.etName.setOnFocusChangeListener(persistOnBlur);
        holder.etQty.setOnFocusChangeListener(persistOnBlur);

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                RecipeIngredient removed = items.remove(pos);
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
