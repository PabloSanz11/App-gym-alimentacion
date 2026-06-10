package com.pablosanz.gymapp.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pablosanz.gymapp.data.model.BodyMeasurement;
import com.pablosanz.gymapp.data.model.ExerciseLog;
import com.pablosanz.gymapp.data.model.FavoriteFood;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.MealLog;
import com.pablosanz.gymapp.data.model.WorkoutSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        WorkoutSession.class,
        ExerciseLog.class,
        BodyMeasurement.class,
        MealLog.class,
        FoodEntry.class,
        FavoriteFood.class
}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutSessionDao workoutSessionDao();
    public abstract ExerciseLogDao exerciseLogDao();
    public abstract BodyMeasurementDao bodyMeasurementDao();
    public abstract MealLogDao mealLogDao();
    public abstract FoodEntryDao foodEntryDao();
    public abstract FavoriteFoodDao favoriteFoodDao();

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gymapp_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // The database is pre-populated with the workout plan via ExerciseData static list.
            // No need to insert into DB as exercises are a static in-memory list.

            // Pre-populate favorite foods (Mexican common foods)
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Chilaquiles con queso', 18, 45, 420, 15, 300)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Bistec a la plancha', 35, 0, 220, 8, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Huevo estrellado (2 pzas)', 12, 1, 180, 14, 100)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Licuado proteína + leche + avena', 40, 55, 450, 8, 400)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Pollo a la plancha (130g)', 42, 0, 215, 5, 130)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Arroz cocido (250g)', 5, 55, 245, 1, 250)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Atún en lata + tostadas', 30, 20, 280, 4, 200)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Carne molida (150g)', 41, 0, 290, 18, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Yogur griego (200g)', 20, 8, 130, 0, 200)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Frijoles negros (150g)', 9, 27, 170, 1, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Queso panela (50g)', 9, 2, 90, 6, 50)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Papa/camote horneado (300g)', 5, 65, 285, 0, 300)");
        }
    };
}
