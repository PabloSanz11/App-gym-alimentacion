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
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.model.WorkoutSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        WorkoutSession.class,
        ExerciseLog.class,
        BodyMeasurement.class,
        MealLog.class,
        FoodEntry.class,
        FavoriteFood.class,
        Recipe.class,
        RecipeIngredient.class
}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutSessionDao workoutSessionDao();
    public abstract ExerciseLogDao exerciseLogDao();
    public abstract BodyMeasurementDao bodyMeasurementDao();
    public abstract MealLogDao mealLogDao();
    public abstract FoodEntryDao foodEntryDao();
    public abstract FavoriteFoodDao favoriteFoodDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeIngredientDao recipeIngredientDao();

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

            // Favorite foods
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

            // Mexican recipes
            // 1. Chilaquiles verdes con pollo
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Chilaquiles verdes con pollo', 'Desayuno', 38, 50, 520, 18)");
            long r1 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Totopos (tortilla frita)', 60, 5, 40, 280, 12)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Salsa verde', 80, 1, 4, 25, 1)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Pechuga de pollo cocida', 100, 30, 0, 165, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Crema ácida (1 cda)', 15, 0, 1, 30, 3)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Queso fresco (30g)', 30, 6, 2, 75, 6)");

            // 2. Bistec con papas
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Bistec con papas', 'Comida', 45, 40, 480, 14)");
            long r2 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Bistec de res', 150, 36, 0, 250, 10)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Papa mediana', 200, 5, 38, 180, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Aceite de oliva (1 cda)', 14, 0, 0, 120, 14)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Limón y especias', 10, 0, 1, 4, 0)");

            // 3. Licuado post-entreno
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Licuado post-entreno', 'Snack', 42, 60, 490, 8)");
            long r3 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Proteína whey (1 scoop)', 30, 24, 3, 120, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Leche entera (300ml)', 300, 10, 14, 185, 10)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Avena (50g)', 50, 7, 35, 190, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Plátano mediano', 90, 1, 23, 90, 0)");

            // 4. Pollo con arroz y verduras
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Pollo con arroz y verduras', 'Comida', 50, 60, 530, 8)");
            long r4 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Pechuga de pollo (150g)', 150, 45, 0, 248, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Arroz cocido (200g)', 200, 4, 44, 196, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Brócoli y zanahoria (100g)', 100, 3, 10, 50, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Aceite de oliva', 10, 0, 0, 88, 10)");

            // 5. Huevos a la mexicana
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Huevos a la mexicana', 'Desayuno', 22, 8, 290, 20)");
            long r5 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Huevos (3 pzas)', 150, 18, 2, 225, 15)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Jitomate (60g)', 60, 1, 4, 22, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Cebolla y chile (30g)', 30, 1, 6, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Aceite (1 cda)', 10, 0, 0, 88, 10)");

            // 6. Tacos de atún
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Tacos de atún', 'Cena', 36, 30, 380, 10)");
            long r6 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Atún en agua (1 lata)', 140, 30, 0, 140, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Tortillas de maíz (3 pzas)', 75, 5, 45, 220, 3)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Aguacate (40g)', 40, 1, 2, 65, 6)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Limón y cilantro', 10, 0, 1, 5, 0)");

            // 7. Carne molida con verduras
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Carne molida con verduras', 'Comida', 46, 15, 420, 22)");
            long r7 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Carne molida de res (180g)', 180, 40, 0, 340, 20)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Jitomate y cebolla', 80, 2, 8, 40, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Chayote (100g)', 100, 1, 5, 23, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Chile y especias', 15, 0, 2, 10, 0)");

            // 8. Ensalada proteica
            db.execSQL("INSERT INTO recipes (name, category, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Ensalada proteica', 'Cena', 40, 12, 350, 15)");
            long r8 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Pechuga de pollo (120g)', 120, 36, 0, 198, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Lechuga y espinacas (100g)', 100, 3, 5, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Queso panela (40g)', 40, 8, 2, 72, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Aceite de oliva + limón', 12, 0, 1, 100, 12)");
        }

        private long getLastInsertId(SupportSQLiteDatabase db) {
            try (android.database.Cursor cursor = db.query("SELECT last_insert_rowid()")) {
                if (cursor.moveToFirst()) return cursor.getLong(0);
            }
            return 0;
        }
    };
}
