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
}, version = 8, exportSchema = false)
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
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Licuado post-entreno', '9am — Fija', '1 scoop proteína + kéfir o leche + avena + plátano + crema de cacahuate · ~41g proteína', '🥛', 41, 81, 677, 22)");
            long r1 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Proteína whey (1 scoop)', 30, 24, 3, 120, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Kéfir o leche (240ml)', 240, 8, 12, 150, 8)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Avena (¾ taza / 60g)', 60, 8, 40, 228, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Plátano mediano (100g)', 100, 1, 23, 89, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Crema de cacahuate (1 cda / 16g)', 16, 0, 3, 90, 8)");

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

            // 10. Picadillo proteico de res y frijol — 8pm (cena lista para calentar, sirve 5)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Picadillo proteico de res y frijol', '8pm — Cena (meal prep)', '750g carne molida + 600g frijol cocido + verduras · ~40g proteína por porción (sube a ~50g con 80g de cottage)', '🍲', 40, 24, 410, 18)");
            long r10 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Carne molida (750g)', 750, 150, 0, 1450, 90)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Frijol cocido (600g)', 600, 45, 110, 675, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Cebolla (1 pza)', 150, 1, 14, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Ajo (3 dientes)', 10, 0, 2, 10, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Jitomate (3 pzas)', 300, 2, 12, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Pimiento (1 pza)', 120, 1, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Calabacita (1 pza)', 150, 2, 6, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Zanahoria (1 pza)', 70, 1, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Aceite (1 cda)', 14, 0, 0, 120, 14)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r10 + ", 'Caldo de res (1 taza)', 240, 1, 2, 15, 0)");

            // 11. Muffins de huevo proteicos — 8pm (cena lista para calentar, 12 muffins)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Muffins de huevo proteicos', '8pm — Cena (meal prep)', '10 huevos + claras + pollo + panela + verduras, horneados · ~30g proteína por 3 piezas (sube a ~40g con cottage o kéfir)', '🧁', 30, 6, 280, 16)");
            long r11 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Huevos (10 pzas)', 500, 60, 0, 700, 50)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Claras San Juan (200ml)', 200, 22, 0, 104, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Pollo deshebrado (150g)', 150, 45, 0, 248, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Queso panela (100g)', 100, 18, 5, 225, 15)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Calabacita rallada (1 pza)', 150, 2, 6, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Pimiento (1 pza)', 120, 1, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Cebolla (½ pza)', 75, 0, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Espinaca (50g)', 50, 1, 2, 12, 0)");

            // 12. Tinga de pollo — 8pm (cena lista para calentar, sirve 5)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Tinga de pollo', '8pm — Cena (meal prep)', '750g pechuga deshebrada + salsa de jitomate y chipotle · ~40g proteína por porción · sirve con tortillas y cottage', '🌶️', 40, 8, 280, 8)");
            long r12 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Pechuga de pollo (750g)', 750, 225, 0, 1240, 25)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Cebolla (1 pza)', 150, 1, 14, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Jitomate (4 pzas)', 400, 3, 16, 80, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Chipotles en adobo (2 pzas)', 30, 0, 4, 20, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Ajo (2 dientes)', 7, 0, 1, 7, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Aceite (1 cda)', 14, 0, 0, 120, 14)");

            // 13. Lentejas guisadas con chorizo de pavo — 8pm (cena lista para calentar, sirve 5)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG) VALUES ('Lentejas guisadas con chorizo de pavo', '8pm — Cena (meal prep)', '450g lentejas secas + chorizo de pavo + verduras · ~34g proteína por porción (sube a ~40g con huevo cocido o 80g de cottage)', '🍛', 34, 50, 420, 12)");
            long r13 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Lentejas secas (450g)', 450, 117, 297, 1530, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Chorizo de pavo (250g)', 250, 50, 5, 400, 25)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Cebolla (1 pza)', 150, 1, 14, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Ajo (3 dientes)', 10, 0, 2, 10, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Jitomate (3 pzas)', 300, 2, 12, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Zanahoria (1 pza)', 70, 1, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Caldo de pollo (1.5L)', 1500, 5, 10, 90, 0)");

            // ── Frecuentes adicionales del Plan Maestro (PDF, cenas meal prep) ──
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Crema de cacahuate (1 cda / 16g)', 0, 3, 90, 8, 16)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Chorizo de pavo (100g)', 20, 2, 160, 10, 100)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Lentejas cocidas (150g)', 13, 33, 170, 0, 150)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Pechuga de pollo deshebrada (100g)', 30, 0, 165, 3, 100)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Picadillo de res y frijol (porción / 280g)', 40, 24, 410, 18, 280)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Muffin de huevo proteico (3 pzas / 180g)', 30, 6, 280, 16, 180)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Tinga de pollo (porción / 260g)', 40, 8, 280, 8, 260)");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG) VALUES ('Lentejas guisadas con chorizo (porción / 350g)', 34, 50, 420, 12, 350)");
        }

        private long getLastInsertId(SupportSQLiteDatabase db) {
            try (android.database.Cursor cursor = db.query("SELECT last_insert_rowid()")) {
                if (cursor.moveToFirst()) return cursor.getLong(0);
            }
            return 0;
        }
    };
}
