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
import com.pablosanz.gymapp.data.model.MealPlanEntry;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.model.ShoppingListItem;
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
        RecipeIngredient.class,
        MealPlanEntry.class,
        ShoppingListItem.class
}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutSessionDao workoutSessionDao();
    public abstract ExerciseLogDao exerciseLogDao();
    public abstract BodyMeasurementDao bodyMeasurementDao();
    public abstract MealLogDao mealLogDao();
    public abstract FoodEntryDao foodEntryDao();
    public abstract FavoriteFoodDao favoriteFoodDao();
    public abstract RecipeDao recipeDao();
    public abstract RecipeIngredientDao recipeIngredientDao();
    public abstract MealPlanDao mealPlanDao();
    public abstract ShoppingListDao shoppingListDao();

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

            // ── Frecuentes del meal prep semanal (PDF) ─────────────────────────
            // Proteínas cocinadas el domingo
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Pollo deshebrado (120g)', 36, 0, 198, 4, 120)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Pollo en cubos (150g)', 45, 0, 248, 5, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Carne molida 80/20 (150g)', 30, 0, 290, 18, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Frijol cocido (120g)', 9, 22, 135, 1, 120)");
            // A la mano — sin cocinar
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Huevo entero (1 pza)', 6, 0, 70, 5, 50)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Claras San Juan (100ml)', 11, 0, 52, 0, 100)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Queso cottage (150g)', 18, 5, 120, 5, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Queso panela (80g)', 14, 4, 180, 12, 80)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Atún en agua (1 lata / 140g)', 30, 0, 140, 2, 140)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Totopos horneados HEB (38g)', 3, 28, 180, 6, 38)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Salsa verde (80g)', 1, 4, 25, 0, 80)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Aguacate ¼ (40g)', 1, 4, 64, 6, 40)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Kéfir o leche (240ml)', 8, 12, 150, 8, 240)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Proteína whey (1 scoop / 30g)', 24, 3, 120, 2, 30)");
            // Guarniciones cocinadas el domingo
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Arroz cocido (250g)', 5, 55, 248, 0, 250)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Camote o papa horneado (300g)', 5, 65, 285, 0, 300)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Pasta integral cocida (200g)', 10, 56, 280, 2, 200)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Avena (¾ taza / 60g)', 8, 40, 228, 4, 60)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Plátano mediano (100g)', 1, 23, 89, 0, 100)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Verduras asadas mix (100g)', 2, 8, 40, 0, 100)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Ensalada de hoja (50g)', 1, 3, 15, 0, 50)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Nueces (30g)', 4, 4, 196, 19, 30)");

            // ── Recetas del Plan Maestro (PDF) ──────────────────────────────────

            // 1. Licuado post-entreno — 9am (fija)
            // 1 scoop whey + 1 taza kéfir/leche + ¾ taza avena + plátano → ~41g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Licuado post-entreno', '9am — Fija', '1 scoop proteína + kéfir o leche + avena + plátano · ~41g proteína', '🥛', 41, 78, 587, 12)");
            long r1 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Proteína whey (1 scoop)', 30, 24, 3, 120, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Kéfir o leche (240ml)', 240, 8, 12, 150, 8)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Avena (¾ taza / 60g)', 60, 8, 40, 228, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Plátano mediano (100g)', 100, 1, 23, 89, 0)");

            // 2. Chilaquiles verdes proteicos — 12pm (rotativa Lun/Jue)
            // 38g totopos horneados + salsa verde + 150g cottage + 1 huevo + 100ml claras + ¼ aguacate → ~40g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Chilaquiles verdes proteicos', '12pm — Rotativa', 'Totopos horneados + salsa verde + cottage + claras + aguacate · ~40g proteína', '🫔', 40, 41, 511, 22)");
            long r2 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Totopos horneados HEB (38g)', 38, 3, 28, 180, 6)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Salsa verde (80g)', 80, 1, 4, 25, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Queso cottage (150g)', 150, 18, 5, 120, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Huevo entero (1 pza)', 50, 6, 0, 70, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Claras San Juan (100ml)', 100, 11, 0, 52, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Aguacate (¼ / 40g)', 40, 1, 4, 64, 6)");

            // 3. Bowl burrito — 12pm (rotativa Mar/Vie)
            // 250g arroz + 120g frijol + 120g pollo deshebrado + salsa + ¼ aguacate → ~51g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Bowl burrito', '12pm — Rotativa', 'Arroz + frijol + pollo deshebrado + salsa + aguacate · ~51g proteína', '🌯', 51, 84, 660, 11)");
            long r3 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Arroz cocido (250g)', 250, 5, 55, 248, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Frijol cocido (120g)', 120, 9, 22, 135, 1)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Pollo deshebrado (120g)', 120, 36, 0, 198, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Salsa (30g)', 30, 0, 3, 15, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Aguacate (¼ / 40g)', 40, 1, 4, 64, 6)");

            // 4. Huevos a la mexicana + frijoles — 12pm (rotativa Mié)
            // 2 huevos + 150ml claras + jitomate/cebolla/chile + frijoles + 2 tortillas + 60g panela → ~50g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Huevos a la mexicana + frijoles', '12pm — Rotativa', '2 huevos + claras + frijoles + 2 tortillas + queso panela · ~50g proteína', '🍳', 50, 53, 618, 22)");
            long r4 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Huevos (2 pzas)', 100, 12, 0, 140, 10)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Claras San Juan (150ml)', 150, 16, 0, 78, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Jitomate + cebolla + chile (80g)', 80, 1, 7, 35, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Frijoles cocidos (80g)', 80, 6, 15, 90, 1)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Tortillas de maíz (2 pzas / 60g)', 60, 4, 28, 140, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Queso panela (60g)', 60, 11, 3, 135, 9)");

            // 5. Bowl de res con camote — 4pm (rotativa Lun/Jue)
            // 150g carne molida + 300g camote/papa + verduras + ensalada → ~38g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Bowl de res con camote', '4pm — Rotativa', 'Carne molida o en cubos + camote/papa horneado + verduras · ~38g proteína', '🥩', 38, 76, 630, 18)");
            long r5 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Carne molida 80/20 (150g)', 150, 30, 0, 290, 18)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Camote o papa (300g)', 300, 5, 65, 285, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Verduras asadas (100g)', 100, 2, 8, 40, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Ensalada de hoja (50g)', 50, 1, 3, 15, 0)");

            // 6. Pasta integral con carne molida — 4pm (rotativa Mar)
            // 90g pasta seca + 150g carne molida + calabacita y jitomate → ~44g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Pasta integral con carne molida', '4pm — Rotativa', '~90g pasta integral + 150g carne molida + calabacita y jitomate · ~44g proteína', '🍝', 44, 70, 660, 20)");
            long r6 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Pasta integral seca (90g → ~200g cocida)', 90, 13, 64, 340, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Carne molida 80/20 (150g)', 150, 30, 0, 290, 18)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Calabacita y jitomate (100g)', 100, 1, 6, 30, 0)");

            // 7. Salteado de pollo con verduras + arroz — 4pm (rotativa Mié/Vie)
            // 150g pollo en cubos + pimiento/brócoli/cebolla + 200g arroz → ~52g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Salteado de pollo con verduras', '4pm — Rotativa', '150g pollo en cubos + pimiento/brócoli/cebolla + arroz cocido · ~52g proteína', '🍗', 52, 56, 504, 5)");
            long r7 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Pollo en cubos (150g)', 150, 45, 0, 248, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Pimiento + brócoli + cebolla (150g)', 150, 3, 12, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Arroz cocido (200g)', 200, 4, 44, 196, 0)");

            // 8. Cena: Atún + queso panela — 8pm (opción A)
            // 1 lata atún + 80g panela → ~44g P, cero cocina
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Cena: Atún + queso panela', '8pm — Cena (cero cocina)', '1 lata atún en agua + queso panela · ~44g proteína · sin cocinar', '🐟', 44, 4, 320, 14)");
            long r8 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Atún en agua (1 lata / 140g)', 140, 30, 0, 140, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Queso panela (80g)', 80, 14, 4, 180, 12)");

            // 9. Cena: Bowl kéfir + fruta + nuez — 8pm (opción B)
            // 200ml kéfir + fruta + 30g nueces → ~12g P, ligero
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Cena: Bowl kéfir + fruta + nuez', '8pm — Cena (cero cocina)', 'Kéfir + fruta de temporada + nueces · cena ligera sin cocinar', '🥣', 12, 29, 391, 26)");
            long r9 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r9 + ", 'Kéfir (200ml)', 200, 7, 10, 130, 7)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r9 + ", 'Fruta de temporada (100g)', 100, 1, 15, 65, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r9 + ", 'Nueces (30g)', 30, 4, 4, 196, 19)");
        }

        private long getLastInsertId(SupportSQLiteDatabase db) {
            try (android.database.Cursor cursor = db.query("SELECT last_insert_rowid()")) {
                if (cursor.moveToFirst()) return cursor.getLong(0);
            }
            return 0;
        }
    };
}
