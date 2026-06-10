package com.pablosanz.gymapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.pablosanz.gymapp.data.api.FoodSearchResponse;
import com.pablosanz.gymapp.data.api.RetrofitClient;
import com.pablosanz.gymapp.data.db.AppDatabase;
import com.pablosanz.gymapp.data.db.FavoriteFoodDao;
import com.pablosanz.gymapp.data.db.FoodEntryDao;
import com.pablosanz.gymapp.data.db.MealLogDao;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.MealLog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionRepository {

    private final MealLogDao mealLogDao;
    private final FoodEntryDao foodEntryDao;
    private final FavoriteFoodDao favoriteFoodDao;

    public NutritionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mealLogDao = db.mealLogDao();
        foodEntryDao = db.foodEntryDao();
        favoriteFoodDao = db.favoriteFoodDao();
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
                .searchFood(query, 1, 20)
                .enqueue(new Callback<FoodSearchResponse>() {
                    @Override
                    public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Error en la respuesta");
                        }
                    }

                    @Override
                    public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
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

    public void getFavoriteFoods(OnFavoriteFoodsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<com.pablosanz.gymapp.data.model.FavoriteFood> foods = favoriteFoodDao.getAll();
            if (callback != null) callback.onResult(foods);
        });
    }

    public interface OnFavoriteFoodsCallback {
        void onResult(List<com.pablosanz.gymapp.data.model.FavoriteFood> foods);
    }
}
