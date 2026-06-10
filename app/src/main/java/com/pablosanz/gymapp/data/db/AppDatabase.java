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

            // Pre-load Mexican recipes
            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Chilaquiles', 'Tortilla en salsa con queso y crema', 'desayuno', '🫔')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (1, 'Tortilla frita (totopos)', 60, 280, 4, 38, 14, 1, 'Cambia por totopos horneados para menos grasa')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (1, 'Salsa verde o roja', 100, 35, 1, 6, 1, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (1, 'Queso fresco o panela', 40, 80, 7, 2, 5, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (1, 'Crema', 30, 90, 1, 2, 9, 1, 'Omite para menos calorias')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (1, 'Pollo desmenuzado', 80, 130, 25, 0, 3, 0, 'Agrega para mas proteina (+25g)')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Huevos con frijoles', 'Desayuno mexicano clasico', 'desayuno', '🍳')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (2, 'Huevo entero (2 pzas)', 100, 155, 13, 1, 11, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (2, 'Frijoles negros cocidos', 150, 165, 10, 30, 1, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (2, 'Tortilla de maiz (2 pzas)', 60, 125, 3, 26, 1, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (2, 'Aceite para cocinar', 10, 88, 0, 0, 10, 1, 'Usa spray para menos calorias')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Tacos de bistec', 'Con cebolla y cilantro', 'comida', '🌮')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (3, 'Bistec de res', 150, 270, 35, 0, 14, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (3, 'Tortilla de maiz (3 pzas)', 90, 190, 5, 39, 2, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (3, 'Cebolla asada', 40, 20, 0, 5, 0, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (3, 'Salsa verde', 50, 18, 1, 3, 0, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (3, 'Aguacate', 50, 80, 1, 4, 7, 0, 'Guacamole opcional')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Pollo con arroz y verduras', 'Meal prep semanal', 'comida', '🍗')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (4, 'Pechuga de pollo', 130, 215, 42, 0, 5, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (4, 'Arroz cocido', 250, 245, 5, 55, 1, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (4, 'Verdura asada (mix)', 150, 55, 3, 10, 0, 1, '')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Licuado post-entreno', 'Proteina + avena + platano', 'desayuno', '🥤')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (5, 'Proteina en polvo (1 scoop)', 30, 120, 25, 3, 2, 1, 'O sustituye por 1.5 taza yogur griego')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (5, 'Leche entera', 240, 150, 8, 12, 8, 1, 'O leche descremada para menos grasa')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (5, 'Avena cruda', 60, 220, 8, 40, 4, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (5, 'Platano', 120, 105, 1, 27, 0, 1, '')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Atun con tostadas', 'Cena rapida alto en proteina', 'cena', '🥫')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (6, 'Atun en lata', 140, 165, 35, 0, 2, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (6, 'Tostadas (4 pzas)', 60, 230, 5, 44, 4, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (6, 'Queso panela', 50, 90, 9, 2, 6, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (6, 'Aguacate', 75, 120, 1, 6, 11, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (6, 'Ensalada (lechuga, jitomate)', 150, 25, 2, 5, 0, 1, '')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Quesadillas', 'Con queso Oaxaca', 'cualquiera', '🫓')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (7, 'Tortilla de maiz (2 pzas)', 60, 125, 3, 26, 1, 1, 'O tortilla de harina')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (7, 'Queso Oaxaca', 60, 200, 14, 1, 16, 1, '')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (7, 'Pollo desmenuzado', 80, 130, 25, 0, 3, 0, 'Agrega proteina extra')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (7, 'Espinaca', 30, 7, 1, 1, 0, 0, 'Para mas nutrientes')");

            db.execSQL("INSERT INTO recipes (name, description, category, imageEmoji) VALUES ('Carne molida con camote', 'Meal prep PM', 'comida', '🥩')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (8, 'Carne molida 80/20', 150, 290, 21, 0, 23, 1, 'O carne molida 90/10 para menos grasa')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (8, 'Camote horneado', 300, 255, 4, 60, 0, 1, 'O papa blanca')");
            db.execSQL("INSERT INTO recipe_ingredients (recipeId, name, quantityG, caloriesKcal, proteinG, carbsG, fatG, included, alternativeNote) VALUES (8, 'Verdura asada', 150, 55, 3, 10, 0, 1, '')");
        }
    };
}
