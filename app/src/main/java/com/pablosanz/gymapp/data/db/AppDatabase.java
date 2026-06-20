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
import com.pablosanz.gymapp.data.model.IngredientPriceInfo;
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
        ShoppingListItem.class,
        IngredientPriceInfo.class
}, version = 11, exportSchema = false)
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
    public abstract IngredientPriceInfoDao ingredientPriceInfoDao();

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
            db.beginTransaction();
            try {
                seedData(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        // Total recipes inserted by seedData(); used to detect a partial/incomplete seed
        // (e.g. a previous run that crashed mid-insert) and not just a fully empty table.
        private static final int EXPECTED_RECIPE_COUNT = 14;

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                int count = 0;
                try (android.database.Cursor cursor = db.query("SELECT COUNT(*) FROM recipes")) {
                    if (cursor.moveToFirst()) count = cursor.getInt(0);
                }
                if (count < EXPECTED_RECIPE_COUNT) {
                    // Run inside a single transaction so every statement (including the
                    // last_insert_rowid() lookups in seedData) is pinned to the same
                    // connection — Room's WAL read-connection pool can otherwise route
                    // queries to a connection that never saw the INSERT, returning a
                    // stale/zero rowid and triggering FK violations on the next insert.
                    db.beginTransaction();
                    try {
                        db.execSQL("DELETE FROM recipe_ingredients");
                        db.execSQL("DELETE FROM recipes");
                        db.execSQL("DELETE FROM favorite_foods");
                        seedData(db);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }
            });
        }

        private void seedData(@NonNull SupportSQLiteDatabase db) {
            // ── Frecuentes del meal prep semanal (PDF) ─────────────────────────
            // Cada fila lleva mealSlot para que al elegir un horario (desayuno/almuerzo/
            // merienda/cena) en el calendario y en "Tu plan de comidas" solo se muestren
            // los widgets relevantes para ese horario.
            // Proteínas cocinadas el domingo
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Pollo deshebrado (120g)', 36, 0, 198, 4, 120, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Pollo en cubos (150g)', 45, 0, 248, 5, 150, 'merienda')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Carne molida 80/20 (150g)', 30, 0, 290, 18, 150, 'merienda')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Frijol cocido (120g)', 9, 22, 135, 1, 120, 'almuerzo')");
            // A la mano — sin cocinar
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Huevo entero (1 pza)', 6, 0, 70, 5, 50, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Claras San Juan (100ml)', 11, 0, 52, 0, 100, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Queso cottage (150g)', 18, 5, 120, 5, 150, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Queso panela (80g)', 14, 4, 180, 12, 80, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Atún en agua (1 lata / 140g)', 30, 0, 140, 2, 140, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Totopos horneados HEB (38g)', 3, 28, 180, 6, 38, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Salsa verde (80g)', 1, 4, 25, 0, 80, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Aguacate ¼ (40g)', 1, 4, 64, 6, 40, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Kéfir o leche (240ml)', 8, 12, 150, 8, 240, 'desayuno')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Proteína whey (1 scoop / 30g)', 24, 3, 120, 2, 30, 'desayuno')");
            // Guarniciones cocinadas el domingo
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Arroz cocido (250g)', 5, 55, 248, 0, 250, 'almuerzo')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Camote o papa horneado (300g)', 5, 65, 285, 0, 300, 'merienda')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Pasta integral cocida (200g)', 10, 56, 280, 2, 200, 'merienda')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Avena (¾ taza / 60g)', 8, 40, 228, 4, 60, 'desayuno')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Plátano mediano (100g)', 1, 23, 89, 0, 100, 'desayuno')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Verduras asadas mix (100g)', 2, 8, 40, 0, 100, 'merienda')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Ensalada de hoja (50g)', 1, 3, 15, 0, 50, 'merienda')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Nueces (30g)', 4, 4, 196, 19, 30, 'cena')");
            // Nuevo del PDF: para las albóndigas en chipotle (merienda 4pm)
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Chipotles en adobo (lata)', 1, 6, 30, 1, 30, 'merienda')");

            // ── Recetas del Plan Maestro (PDF) ──────────────────────────────────

            // 1. Licuado post-entreno — 9am (fija)
            // 1 scoop whey + 1 taza kéfir/leche + ¾ taza avena + plátano → ~41g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Licuado post-entreno', '9am — Fija', '1 scoop proteína + kéfir o leche + avena + plátano + crema de cacahuate · ~41g proteína', '🥛', 41, 81, 677, 22, 'desayuno')");
            long r1 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Proteína whey (1 scoop)', 30, 24, 3, 120, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Kéfir o leche (240ml)', 240, 8, 12, 150, 8)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Avena (¾ taza / 60g)', 60, 8, 40, 228, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Plátano mediano (100g)', 100, 1, 23, 89, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r1 + ", 'Crema de cacahuate (1 cda / 16g)', 16, 0, 3, 90, 8)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Licúa el kéfir o leche con el plátano y la avena hasta integrar.\n2. Agrega el scoop de proteína y la crema de cacahuate.\n3. Licúa 30-40 segundos más hasta que quede homogéneo y sirve de inmediato.' WHERE id = " + r1);

            // 2. Chilaquiles verdes proteicos — 12pm (rotativa Lun/Jue)
            // 38g totopos horneados + salsa verde + 150g cottage + 1 huevo + 100ml claras + ¼ aguacate → ~40g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Chilaquiles verdes proteicos', '12pm — Rotativa', 'Totopos horneados + salsa verde + cottage + claras + aguacate · ~40g proteína', '🫔', 40, 41, 511, 22, 'almuerzo')");
            long r2 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Totopos horneados HEB (38g)', 38, 3, 28, 180, 6)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Salsa verde (80g)', 80, 1, 4, 25, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Queso cottage (150g)', 150, 18, 5, 120, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Huevo entero (1 pza)', 50, 6, 0, 70, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Claras San Juan (100ml)', 100, 11, 0, 52, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r2 + ", 'Aguacate (¼ / 40g)', 40, 1, 4, 64, 6)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Calienta la salsa verde en un sartén a fuego medio.\n2. Agrega los totopos y mezcla rápido para que se impregnen sin que se ablanden de más (1-2 min).\n3. Aparte, cocina el huevo y las claras revueltas o estrelladas.\n4. Sirve los totopos, corona con el huevo, el queso cottage y el aguacate en rebanadas.' WHERE id = " + r2);

            // 3. Bowl burrito — 12pm (rotativa Mar/Vie)
            // 250g arroz + 120g frijol + 120g pollo deshebrado + salsa + ¼ aguacate → ~51g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Bowl burrito', '12pm — Rotativa', 'Arroz + frijol + pollo deshebrado + salsa + aguacate · ~51g proteína', '🌯', 51, 84, 660, 11, 'almuerzo')");
            long r3 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Arroz cocido (250g)', 250, 5, 55, 248, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Frijol cocido (120g)', 120, 9, 22, 135, 1)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Pollo deshebrado (120g)', 120, 36, 0, 198, 4)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Salsa (30g)', 30, 0, 3, 15, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r3 + ", 'Aguacate (¼ / 40g)', 40, 1, 4, 64, 6)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Calienta el arroz y el frijol cocidos (microondas o sartén).\n2. Calienta o recalienta el pollo deshebrado.\n3. Arma el bowl: arroz como base, frijol, pollo encima.\n4. Corona con la salsa y el aguacate en rebanadas.' WHERE id = " + r3);

            // 4. Huevos a la mexicana + frijoles — 12pm (rotativa Mié)
            // 2 huevos + 150ml claras + jitomate/cebolla/chile + frijoles + 2 tortillas + 60g panela → ~50g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Huevos a la mexicana + frijoles', '12pm — Rotativa', '2 huevos + claras + frijoles + 2 tortillas + queso panela · ~50g proteína', '🍳', 50, 53, 618, 22, 'almuerzo')");
            long r4 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Huevos (2 pzas)', 100, 12, 0, 140, 10)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Claras San Juan (150ml)', 150, 16, 0, 78, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Jitomate + cebolla + chile (80g)', 80, 1, 7, 35, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Frijoles cocidos (80g)', 80, 6, 15, 90, 1)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Tortillas de maíz (2 pzas / 60g)', 60, 4, 28, 140, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r4 + ", 'Queso panela (60g)', 60, 11, 3, 135, 9)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Sofríe el jitomate, la cebolla y el chile picados en un sartén con un poco de aceite.\n2. Agrega los huevos y las claras batidos, revuelve hasta cuajar.\n3. Calienta los frijoles y las tortillas aparte.\n4. Sirve los huevos con los frijoles, las tortillas y el queso panela.' WHERE id = " + r4);

            // 5. Bowl de res con camote — 4pm (rotativa Lun/Jue)
            // 150g carne molida + 300g camote/papa + verduras + ensalada → ~38g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Bowl de res con camote', '4pm — Rotativa', 'Carne molida o en cubos + camote/papa horneado + verduras · ~38g proteína', '🥩', 38, 76, 630, 18, 'merienda')");
            long r5 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Carne molida 80/20 (150g)', 150, 30, 0, 290, 18)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Camote o papa (300g)', 300, 5, 65, 285, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Verduras asadas (100g)', 100, 2, 8, 40, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r5 + ", 'Ensalada de hoja (50g)', 50, 1, 3, 15, 0)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Hornea o cuece el camote/papa en cubos hasta que esté suave.\n2. Dora la carne molida o los cubos de res en un sartén a fuego medio-alto.\n3. Asa las verduras (10 min) mientras la carne termina de cocinarse.\n4. Arma el bowl con la carne, el camote, las verduras asadas y la ensalada.' WHERE id = " + r5);

            // 6. Pasta integral con carne molida — 4pm (rotativa Mar)
            // 90g pasta seca + 150g carne molida + calabacita y jitomate → ~44g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Pasta integral con carne molida', '4pm — Rotativa', '~90g pasta integral + 150g carne molida + calabacita y jitomate · ~44g proteína', '🍝', 44, 70, 660, 20, 'merienda')");
            long r6 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Pasta integral seca (90g → ~200g cocida)', 90, 13, 64, 340, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Carne molida 80/20 (150g)', 150, 30, 0, 290, 18)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r6 + ", 'Calabacita y jitomate (100g)', 100, 1, 6, 30, 0)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Pon a cocer la pasta integral en agua con sal según el tiempo del paquete.\n2. Mientras hierve, dora la carne molida en un sartén.\n3. Agrega la calabacita y el jitomate picados, cocina 5 min más.\n4. Escurre la pasta, mézclala con la carne y las verduras y sirve.' WHERE id = " + r6);

            // 7. Salteado de pollo con verduras + arroz — 4pm (rotativa Mié/Vie)
            // 150g pollo en cubos + pimiento/brócoli/cebolla + 200g arroz → ~52g P
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Salteado de pollo con verduras', '4pm — Rotativa', '150g pollo en cubos + pimiento/brócoli/cebolla + arroz cocido · ~52g proteína', '🍗', 52, 56, 504, 5, 'merienda')");
            long r7 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Pollo en cubos (150g)', 150, 45, 0, 248, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Pimiento + brócoli + cebolla (150g)', 150, 3, 12, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7 + ", 'Arroz cocido (200g)', 200, 4, 44, 196, 0)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Calienta un sartén o wok con un poco de aceite a fuego alto.\n2. Saltea el pollo en cubos hasta dorar (5-6 min).\n3. Agrega el pimiento, brócoli y cebolla, saltea 4-5 min más sin que se sobrecocinen.\n4. Sirve sobre el arroz cocido.' WHERE id = " + r7);

            // 7b. Albóndigas en salsa de chipotle — 4pm (rotativa Viernes, PDF sección 7)
            // 900g carne molida (rinde 5) + huevo + avena molida + salsa de chipotle → ~40g P por porción
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Albóndigas en salsa de chipotle', '4pm — Rotativa', '900g carne molida + huevo + avena molida + salsa de jitomate y chipotle, sirve 5 · ~40g proteína por porción con arroz o tortillas', '🍖', 40, 22, 420, 24, 'merienda')");
            long r7b = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Carne molida (900g)', 900, 180, 0, 1740, 108)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Huevo entero (1 pza)', 50, 6, 0, 70, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Avena molida (40g)', 40, 5, 27, 152, 3)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Cebolla (½ pza)', 75, 0, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Jitomate (4 pzas)', 400, 3, 16, 80, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Chipotles en adobo (3 pzas)', 45, 0, 6, 30, 1)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r7b + ", 'Arroz cocido (½ taza / 125g)', 125, 3, 28, 124, 0)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Mezcla la carne molida con el huevo, la avena molida, cebolla y ajo picados, comino y sal; forma las albóndigas.\n2. Licúa el jitomate, el chipotle, la cebolla y el ajo restantes para la salsa.\n3. Sella las albóndigas en una olla con aceite.\n4. Vierte la salsa y el caldo, hierve y cuece ~20 min hasta que espese.\n5. Sirve con ½ taza de arroz o tortillas. Guárdalas en su salsa para que se mantengan jugosas al recalentar.' WHERE id = " + r7b);

            // 8. Cena: Atún + queso panela — 8pm (opción A)
            // 1 lata atún + 80g panela → ~44g P, cero cocina
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Cena: Atún + queso panela', '8pm — Cena (cero cocina)', '1 lata atún en agua + queso panela · ~44g proteína · sin cocinar', '🐟', 44, 4, 320, 14, 'cena')");
            long r8 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Atún en agua (1 lata / 140g)', 140, 30, 0, 140, 2)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r8 + ", 'Queso panela (80g)', 80, 14, 4, 180, 12)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Escurre el atún.\n2. Corta el queso panela en cubos o rebanadas.\n3. Sirve juntos, sin cocción. Listo en menos de 2 minutos.' WHERE id = " + r8);

            // 9. Cena: Bowl kéfir + fruta + nuez — 8pm (opción B)
            // 200ml kéfir + fruta + 30g nueces → ~12g P, ligero
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Cena: Bowl kéfir + fruta + nuez', '8pm — Cena (cero cocina)', 'Kéfir + fruta de temporada + nueces · cena ligera sin cocinar', '🥣', 12, 29, 391, 26, 'cena')");
            long r9 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r9 + ", 'Kéfir (200ml)', 200, 7, 10, 130, 7)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r9 + ", 'Fruta de temporada (100g)', 100, 1, 15, 65, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r9 + ", 'Nueces (30g)', 30, 4, 4, 196, 19)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Sirve el kéfir en un bowl.\n2. Corta la fruta de temporada y agrégala encima.\n3. Espolvorea las nueces. Sin cocción.' WHERE id = " + r9);

            // 10. Picadillo proteico de res y frijol — 8pm (cena lista para calentar, sirve 5)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Picadillo proteico de res y frijol', '8pm — Cena (meal prep)', '750g carne molida + 600g frijol cocido + verduras · ~40g proteína por porción (sube a ~50g con 80g de cottage)', '🍲', 40, 24, 410, 18, 'cena')");
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
            db.execSQL("UPDATE recipes SET prepSteps = '1. Pica la cebolla, el ajo, el pimiento, la calabacita, la zanahoria y el jitomate.\n2. Sofríe la verdura en el aceite hasta que la cebolla esté transparente.\n3. Agrega la carne molida y dórala bien, deshaciendo los grumos.\n4. Incorpora el comino, paprika, orégano y sal, agrega el jitomate y el caldo.\n5. Reduce a fuego medio-bajo y cocina 15 min.\n6. Incorpora el frijol cocido, mezcla y cocina 5 min más. Porciona en 5 recipientes (sube a ~50g coronando con 80g de cottage al servir).' WHERE id = " + r10);

            // 11. Muffins de huevo proteicos — 8pm (cena lista para calentar, 12 muffins)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Muffins de huevo proteicos', '8pm — Cena (meal prep)', '10 huevos + claras + pollo + panela + verduras, horneados · ~30g proteína por 3 piezas (sube a ~40g con cottage o kéfir)', '🧁', 30, 6, 280, 16, 'cena')");
            long r11 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Huevos (10 pzas)', 500, 60, 0, 700, 50)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Claras San Juan (200ml)', 200, 22, 0, 104, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Pollo deshebrado (150g)', 150, 45, 0, 248, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Queso panela (100g)', 100, 18, 5, 225, 15)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Calabacita rallada (1 pza)', 150, 2, 6, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Pimiento (1 pza)', 120, 1, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Cebolla (½ pza)', 75, 0, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r11 + ", 'Espinaca (50g)', 50, 1, 2, 12, 0)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Precalienta el horno a 180°C y engrasa un molde para 12 muffins.\n2. Bate los huevos con las claras y la sal.\n3. Reparte la calabacita rallada, el pimiento, la cebolla, la espinaca, el pollo deshebrado y el queso panela entre los moldes.\n4. Vierte la mezcla de huevo encima de cada molde hasta cubrir el relleno.\n5. Hornea 20-22 min o hasta que cuajen. Deja enfriar antes de desmoldar (sube a ~40g acompañando con 100g de cottage o 1 taza de kéfir).' WHERE id = " + r11);

            // 12. Tinga de pollo — 8pm (cena lista para calentar, sirve 5)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Tinga de pollo', '8pm — Cena (meal prep)', '750g pechuga deshebrada + salsa de jitomate y chipotle · ~40g proteína por porción · sirve con tortillas y cottage', '🌶️', 40, 8, 280, 8, 'cena')");
            long r12 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Pechuga de pollo (750g)', 750, 225, 0, 1240, 25)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Cebolla (1 pza)', 150, 1, 14, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Jitomate (4 pzas)', 400, 3, 16, 80, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Chipotles en adobo (2 pzas)', 30, 0, 4, 20, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Ajo (2 dientes)', 7, 0, 1, 7, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r12 + ", 'Aceite (1 cda)', 14, 0, 0, 120, 14)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Cuece la pechuga de pollo en agua con sal hasta que esté bien cocida; deshébrala.\n2. Licúa el jitomate, el chipotle y el ajo hasta obtener una salsa.\n3. Acitrona la cebolla en rodajas con el aceite.\n4. Agrega la salsa y el laurel, deja hervir 8-10 min.\n5. Incorpora el pollo deshebrado y cocina 5 min más para que absorba el sabor. Sirve con 2-3 tortillas y queso cottage.' WHERE id = " + r12);

            // 13. Lentejas guisadas con chorizo de pavo — 8pm (cena lista para calentar, sirve 5)
            db.execSQL("INSERT INTO recipes (name, category, description, imageEmoji, totalProteinG, totalCarbsG, totalCaloriesKcal, totalFatG, mealSlot) VALUES ('Lentejas guisadas con chorizo de pavo', '8pm — Cena (meal prep)', '450g lentejas secas + chorizo de pavo + verduras · ~34g proteína por porción (sube a ~40g con huevo cocido o 80g de cottage)', '🍛', 34, 50, 420, 12, 'cena')");
            long r13 = getLastInsertId(db);
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Lentejas secas (450g)', 450, 117, 297, 1530, 5)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Chorizo de pavo (250g)', 250, 50, 5, 400, 25)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Cebolla (1 pza)', 150, 1, 14, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Ajo (3 dientes)', 10, 0, 2, 10, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Jitomate (3 pzas)', 300, 2, 12, 60, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Zanahoria (1 pza)', 70, 1, 7, 30, 0)");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, ingredientName, quantityG, proteinG, carbsG, caloriesKcal, fatG) VALUES (" + r13 + ", 'Caldo de pollo (1.5L)', 1500, 5, 10, 90, 0)");
            db.execSQL("UPDATE recipes SET prepSteps = '1. Dora el chorizo de pavo en una olla grande hasta que suelte su grasa.\n2. Sofríe la cebolla, el ajo y la zanahoria picados en la misma olla.\n3. Licúa el jitomate y agrégalo junto con el comino y la sal.\n4. Incorpora las lentejas secas y el caldo de pollo.\n5. Cocina a fuego medio-bajo ~35 min, hasta que las lentejas estén suaves, moviendo de vez en cuando. Porciona en 5 recipientes (sube a ~40g con un huevo cocido encima o 80g de cottage).' WHERE id = " + r13);

            // ── Frecuentes adicionales del Plan Maestro (PDF, cenas meal prep) ──
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Crema de cacahuate (1 cda / 16g)', 0, 3, 90, 8, 16, 'desayuno')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Chorizo de pavo (100g)', 20, 2, 160, 10, 100, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Lentejas cocidas (150g)', 13, 33, 170, 0, 150, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Pechuga de pollo deshebrada (100g)', 30, 0, 165, 3, 100, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Picadillo de res y frijol (porción / 280g)', 40, 24, 410, 18, 280, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Muffin de huevo proteico (3 pzas / 180g)', 30, 6, 280, 16, 180, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Tinga de pollo (porción / 260g)', 40, 8, 280, 8, 260, 'cena')");
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Lentejas guisadas con chorizo (porción / 350g)', 34, 50, 420, 12, 350, 'cena')");
            // Nuevo del PDF: albóndigas en salsa de chipotle (merienda / cena)
            db.execSQL("INSERT INTO favorite_foods (name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, mealSlot) VALUES ('Albóndigas en chipotle (4 pzas / porción)', 40, 10, 360, 20, 200, 'merienda')");
        }

        private long getLastInsertId(SupportSQLiteDatabase db) {
            try (android.database.Cursor cursor = db.query("SELECT last_insert_rowid()")) {
                if (cursor.moveToFirst()) return cursor.getLong(0);
            }
            return 0;
        }
    };
}
