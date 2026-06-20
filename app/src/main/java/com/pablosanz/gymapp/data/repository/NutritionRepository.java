package com.pablosanz.gymapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.api.RetrofitClient;
import com.pablosanz.gymapp.data.db.AppDatabase;
import com.pablosanz.gymapp.data.db.FavoriteFoodDao;
import com.pablosanz.gymapp.data.db.FoodEntryDao;
import com.pablosanz.gymapp.data.db.IngredientPriceInfoDao;
import com.pablosanz.gymapp.data.db.MealLogDao;
import com.pablosanz.gymapp.data.db.MealPlanDao;
import com.pablosanz.gymapp.data.db.RecipeDao;
import com.pablosanz.gymapp.data.db.RecipeIngredientDao;
import com.pablosanz.gymapp.data.db.ShoppingListDao;
import com.pablosanz.gymapp.data.model.FavoriteFood;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.IngredientPriceInfo;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.model.MealPlanEntry;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.model.ShoppingListItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionRepository {

    private final MealLogDao mealLogDao;
    private final FoodEntryDao foodEntryDao;
    private final FavoriteFoodDao favoriteFoodDao;
    private final RecipeDao recipeDao;
    private final RecipeIngredientDao recipeIngredientDao;
    private final MealPlanDao mealPlanDao;
    private final ShoppingListDao shoppingListDao;
    private final IngredientPriceInfoDao ingredientPriceInfoDao;

    public NutritionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mealLogDao = db.mealLogDao();
        foodEntryDao = db.foodEntryDao();
        favoriteFoodDao = db.favoriteFoodDao();
        recipeDao = db.recipeDao();
        recipeIngredientDao = db.recipeIngredientDao();
        mealPlanDao = db.mealPlanDao();
        shoppingListDao = db.shoppingListDao();
        ingredientPriceInfoDao = db.ingredientPriceInfoDao();
    }

    public LiveData<List<MealLog>> getMealLogsByDate(String date) {
        return mealLogDao.getByDate(date);
    }

    public void insertMealLog(MealLog mealLog, OnInsertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = mealLogDao.insert(mealLog);
            if (callback != null) callback.onInserted(id);
        });
    }

    public void updateMealLog(MealLog mealLog) {
        AppDatabase.databaseWriteExecutor.execute(() -> mealLogDao.update(mealLog));
    }

    public void insertFoodEntry(FoodEntry entry) {
        AppDatabase.databaseWriteExecutor.execute(() -> foodEntryDao.insert(entry));
    }

    public void getOrCreateMealLog(String date, String mealSlot, OnMealLogCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            MealLog existing = mealLogDao.getByDateAndSlot(date, mealSlot);
            if (existing != null) {
                callback.onResult(existing);
            } else {
                MealLog newLog = new MealLog(date, mealSlot);
                long id = mealLogDao.insert(newLog);
                newLog.setId(id);
                callback.onResult(newLog);
            }
        });
    }

    public void searchFood(String query, OnFoodSearchCallback callback) {
        RetrofitClient.getInstance().getService()
                .searchFood("process", query, 1, 25, "product_name,brands,nutriments,code", "es", "unique_scans_n")
                .enqueue(new Callback<FoodSearchResponse>() {
                    @Override
                    public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Sin resultados (código " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
                        callback.onError(t.getMessage() != null ? t.getMessage() : "Sin conexión");
                    }
                });
    }

    public void getWeeklySummary(OnWeeklySummaryCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            java.util.List<String> dates = new java.util.ArrayList<>();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                dates.add(com.pablosanz.gymapp.util.DateUtils.formatDate(cal.getTime()));
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
            }
            java.util.List<MealLog> logs = mealLogDao.getByDateRange(dates);
            if (callback != null) callback.onResult(logs);
        });
    }

    public void getFavoriteFoods(OnFavoriteFoodsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<FavoriteFood> foods = favoriteFoodDao.getAll();
            if (callback != null) callback.onResult(foods);
        });
    }

    public void getAllRecipes(OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDao.getAllRecipes();
            if (callback != null) callback.onResult(recipes);
        });
    }

    public void searchRecipes(String query, OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = (query == null || query.isEmpty())
                    ? recipeDao.getAll()
                    : recipeDao.search(query);
            if (callback != null) callback.onResult(recipes);
        });
    }

    public void getRecipesByCategory(String category, OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDao.getByCategory(category);
            if (callback != null) callback.onResult(recipes);
        });
    }

    public void searchRecipesByMealSlot(String mealSlot, String query, OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = (query == null || query.isEmpty())
                    ? recipeDao.getByMealSlot(mealSlot)
                    : recipeDao.searchByMealSlot(mealSlot, query);
            if (callback != null) callback.onResult(recipes);
        });
    }

    public void getRecipesByMealSlot(String mealSlot, OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDao.getByMealSlot(mealSlot);
            if (callback != null) callback.onResult(recipes);
        });
    }

    public void getFavoriteFoodsByMealSlot(String mealSlot, OnFavoriteFoodsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<FavoriteFood> foods = favoriteFoodDao.getByMealSlot(mealSlot);
            if (callback != null) callback.onResult(foods);
        });
    }

    public void getRecipeById(long id, OnRecipeCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Recipe recipe = recipeDao.getById(id);
            if (callback != null) callback.onResult(recipe);
        });
    }

    public void insertRecipeIngredient(RecipeIngredient ingredient, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            recipeIngredientDao.insert(ingredient);
            if (onDone != null) onDone.run();
        });
    }

    public void updateRecipe(Recipe recipe) {
        AppDatabase.databaseWriteExecutor.execute(() -> recipeDao.update(recipe));
    }

    public void getRecipeIngredients(long recipeId, OnIngredientsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<RecipeIngredient> items = recipeIngredientDao.getByRecipeId(recipeId);
            if (callback != null) callback.onResult(items);
        });
    }

    public void getFoodEntriesByMealLog(long mealLogId, OnEntriesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<FoodEntry> entries = foodEntryDao.getByMealLogId(mealLogId);
            if (callback != null) callback.onResult(entries);
        });
    }

    public void deleteFoodEntry(FoodEntry entry, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodEntryDao.delete(entry);
            recalcMealLogTotals(entry.getMealLogId());
            if (onDone != null) onDone.run();
        });
    }

    public void updateFoodEntryQuantity(FoodEntry entry, float newQty, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            float ratio = (entry.getQuantityG() > 0) ? newQty / entry.getQuantityG() : 0;
            entry.setQuantityG(newQty);
            entry.setProteinG(entry.getProteinG() * ratio);
            entry.setCarbsG(entry.getCarbsG() * ratio);
            entry.setCaloriesKcal(entry.getCaloriesKcal() * ratio);
            entry.setFatG(entry.getFatG() * ratio);
            foodEntryDao.update(entry);
            recalcMealLogTotals(entry.getMealLogId());
            if (onDone != null) onDone.run();
        });
    }

    private void recalcMealLogTotals(long mealLogId) {
        MealLog log = mealLogDao.getById(mealLogId);
        if (log == null) return;
        List<FoodEntry> entries = foodEntryDao.getByMealLogId(mealLogId);
        float p = 0, c = 0, cal = 0, f = 0;
        for (FoodEntry e : entries) {
            p += e.getProteinG();
            c += e.getCarbsG();
            cal += e.getCaloriesKcal();
            f += e.getFatG();
        }
        log.setTotalProteinG(p);
        log.setTotalCarbsG(c);
        log.setTotalCaloriesKcal(cal);
        log.setTotalFatG(f);
        mealLogDao.update(log);
    }

    public void setMealPlan(String day, String slot, long recipeId, String recipeName, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mealPlanDao.insert(new MealPlanEntry(day, slot, recipeId, recipeName));
            if (onDone != null) onDone.run();
        });
    }

    public void setMealPlanFavorite(String day, String slot, long favoriteFoodId, String favoriteFoodName, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mealPlanDao.insert(MealPlanEntry.forFavorite(day, slot, favoriteFoodId, favoriteFoodName));
            if (onDone != null) onDone.run();
        });
    }

    public void clearMealPlanSlot(String day, String slot, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mealPlanDao.clearSlot(day, slot);
            if (onDone != null) onDone.run();
        });
    }

    public void clearWeekPlan(Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mealPlanDao.clearAll();
            shoppingListDao.clearAll();
            if (onDone != null) onDone.run();
        });
    }

    public void getMealPlanForDaySlot(String day, String slot, OnMealPlanEntryCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            MealPlanEntry entry = day != null ? mealPlanDao.getByDayAndSlot(day, slot) : null;
            if (callback != null) callback.onResult(entry);
        });
    }

    public void getMealPlan(OnMealPlanCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MealPlanEntry> entries = mealPlanDao.getAll();
            if (callback != null) callback.onResult(entries);
        });
    }

    public void generateShoppingList(OnShoppingListCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MealPlanEntry> plan = mealPlanDao.getAll();
            Map<String, Float> totals = new LinkedHashMap<>();
            for (MealPlanEntry entry : plan) {
                if (entry.getFavoriteFoodId() > 0) {
                    FavoriteFood fav = favoriteFoodDao.getById(entry.getFavoriteFoodId());
                    if (fav != null) {
                        totals.merge(fav.getName(), fav.getDefaultQuantityG(), Float::sum);
                    }
                } else {
                    Recipe recipe = recipeDao.getById(entry.getRecipeId());
                    int servings = recipe != null ? Math.max(1, recipe.getServings()) : 1;
                    List<RecipeIngredient> ingredients = recipeIngredientDao.getByRecipeId(entry.getRecipeId());
                    for (RecipeIngredient ing : ingredients) {
                        // Las cantidades del ingrediente son del batch completo; se dividen entre
                        // las porciones para sumar solo la cantidad de la porción de este día.
                        totals.merge(ing.getIngredientName(), ing.getQuantityG() / servings, Float::sum);
                    }
                }
            }
            Map<String, IngredientPriceInfo> remembered = new LinkedHashMap<>();
            for (ShoppingListItem existing : shoppingListDao.getAll()) {
                remembered.put(existing.getIngredientName(),
                        new IngredientPriceInfo(existing.getIngredientName(), existing.getEstimatedCostMxn(), existing.getStore()));
            }
            for (IngredientPriceInfo info : ingredientPriceInfoDao.getAll()) {
                remembered.putIfAbsent(info.getIngredientName(), info);
            }
            shoppingListDao.clearAll();
            List<ShoppingListItem> items = new ArrayList<>();
            for (Map.Entry<String, Float> e : totals.entrySet()) {
                IngredientPriceInfo info = remembered.get(e.getKey());
                ShoppingListItem item = new ShoppingListItem(e.getKey(), e.getValue(), false,
                        info != null ? info.getEstimatedCostMxn() : 0f);
                if (info != null) item.setStore(info.getStore());
                items.add(item);
            }
            if (!items.isEmpty()) shoppingListDao.insertAll(items);
            if (callback != null) callback.onResult(items, plan);
        });
    }

    /** Guarda el precio y súper editados para un ingrediente, tanto en la lista actual como en la
     *  memoria persistente usada para prellenar futuras listas. */
    public void saveIngredientPriceInfo(String ingredientName, float estimatedCostMxn, String store, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ingredientPriceInfoDao.upsert(new IngredientPriceInfo(ingredientName, estimatedCostMxn, store));
            ShoppingListItem current = shoppingListDao.getByIngredientName(ingredientName);
            if (current != null) {
                current.setEstimatedCostMxn(estimatedCostMxn);
                current.setStore(store);
                shoppingListDao.update(current);
            }
            if (onDone != null) onDone.run();
        });
    }

    public void updateShoppingItems(List<ShoppingListItem> items, Runnable onDone) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            for (ShoppingListItem item : items) shoppingListDao.update(item);
            if (onDone != null) onDone.run();
        });
    }

    public void getShoppingList(OnShoppingItemsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ShoppingListItem> items = shoppingListDao.getAll();
            if (callback != null) callback.onResult(items);
        });
    }

    public void updateShoppingItem(ShoppingListItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> shoppingListDao.update(item));
    }

    public interface OnMealPlanCallback {
        void onResult(List<MealPlanEntry> entries);
    }

    public interface OnMealPlanEntryCallback {
        void onResult(MealPlanEntry entry);
    }

    public interface OnShoppingListCallback {
        void onResult(List<ShoppingListItem> items, List<MealPlanEntry> plan);
    }

    public interface OnShoppingItemsCallback {
        void onResult(List<ShoppingListItem> items);
    }

    public interface OnInsertCallback {
        void onInserted(long id);
    }

    public interface OnMealLogCallback {
        void onResult(MealLog mealLog);
    }

    public interface OnFoodSearchCallback {
        void onSuccess(FoodSearchResponse response);
        void onError(String error);
    }

    public interface OnWeeklySummaryCallback {
        void onResult(java.util.List<MealLog> logs);
    }

    public interface OnFavoriteFoodsCallback {
        void onResult(List<FavoriteFood> foods);
    }

    public interface OnRecipesCallback {
        void onResult(List<Recipe> recipes);
    }

    public interface OnRecipeCallback {
        void onResult(Recipe recipe);
    }

    public interface OnIngredientsCallback {
        void onResult(List<RecipeIngredient> ingredients);
    }

    public interface OnEntriesCallback {
        void onResult(List<FoodEntry> entries);
    }
}
