package com.pablosanz.gymapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.api.RetrofitClient;
import com.pablosanz.gymapp.data.db.AppDatabase;
import com.pablosanz.gymapp.data.db.FavoriteFoodDao;
import com.pablosanz.gymapp.data.db.FoodEntryDao;
import com.pablosanz.gymapp.data.db.MealLogDao;
import com.pablosanz.gymapp.data.db.RecipeDao;
import com.pablosanz.gymapp.data.db.RecipeIngredientDao;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionRepository {

    private final MealLogDao mealLogDao;
    private final FoodEntryDao foodEntryDao;
    private final FavoriteFoodDao favoriteFoodDao;
    private final RecipeDao recipeDao;
    private final RecipeIngredientDao recipeIngredientDao;

    public NutritionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mealLogDao = db.mealLogDao();
        foodEntryDao = db.foodEntryDao();
        favoriteFoodDao = db.favoriteFoodDao();
        recipeDao = db.recipeDao();
        recipeIngredientDao = db.recipeIngredientDao();
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
                .searchFood("process", query, 1, 20, "product_name,brands,nutriments,code", "es", "mx")
                .enqueue(new Callback<FoodSearchResponse>() {
                    @Override
                    public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getProducts() != null) {
                            callback.onSuccess(response.body());
                        } else if (response.isSuccessful() && response.body() != null) {
                            // Empty but valid response
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

    public void getWeeklySummary(OnWeeklySummaryCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Get meal logs for last 7 days
            java.util.List<String> dates = new java.util.ArrayList<>();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                dates.add(com.pablosanz.gymapp.util.DateUtils.formatDate(cal.getTime()));
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
            }
            java.util.List<com.pablosanz.gymapp.data.model.MealLog> logs = mealLogDao.getByDateRange(dates);
            if (callback != null) callback.onResult(logs);
        });
    }

    public interface OnWeeklySummaryCallback {
        void onResult(java.util.List<com.pablosanz.gymapp.data.model.MealLog> logs);
    }

    public void searchRecipes(String query, OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = query.isEmpty()
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

    public void getRecipeIngredients(long recipeId, OnIngredientsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<RecipeIngredient> ingredients = recipeIngredientDao.getByRecipeId(recipeId);
            if (callback != null) callback.onResult(ingredients);
        });
    }

    public interface OnRecipesCallback {
        void onResult(List<Recipe> recipes);
    }

    public interface OnIngredientsCallback {
        void onResult(List<RecipeIngredient> ingredients);
    }

    public void getFavoriteFoods(OnFavoriteFoodsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<com.pablosanz.gymapp.data.model.FavoriteFood> foods = favoriteFoodDao.getAll();
            if (callback != null) callback.onResult(foods);
        });
    }

    public interface OnFavoriteFoodsCallback {
        void onResult(List<com.pablosanz.gymapp.data.model.FavoriteFood> foods);
    }

    public void getAllRecipes(OnRecipesCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDao.getAllRecipes();
            if (callback != null) callback.onResult(recipes);
        });
    }

    public void getRecipeById(long id, OnRecipeCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Recipe recipe = recipeDao.getById(id);
            if (callback != null) callback.onResult(recipe);
        });
    }

    public void getRecipeIngredients(long recipeId, OnIngredientsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<RecipeIngredient> items = recipeIngredientDao.getByRecipe(recipeId);
            if (callback != null) callback.onResult(items);
        });
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
}
