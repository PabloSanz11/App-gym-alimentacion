package com.pablosanz.gymapp.data.model;

import java.util.ArrayList;
import java.util.List;

public class ExerciseData {

    public static List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();

        // Day 1 - Tren superior fuerza
        exercises.add(new Exercise("Press banca", 1, 4, "6-8"));
        exercises.add(new Exercise("Remo con barra", 1, 4, "6-8"));
        exercises.add(new Exercise("Press militar", 1, 3, "8-10"));
        exercises.add(new Exercise("Jalón al pecho", 1, 3, "10-12"));
        exercises.add(new Exercise("Curl inclinado mancuerna", 1, 3, "10-12"));
        exercises.add(new Exercise("Extensión tríceps polea", 1, 3, "10-12"));
        exercises.add(new Exercise("Curl muñeca+invertido", 1, 3, "12-15"));

        // Day 2 - Tren inferior core
        exercises.add(new Exercise("Sentadilla", 2, 4, "6-8"));
        exercises.add(new Exercise("Peso muerto rumano", 2, 3, "8-10"));
        exercises.add(new Exercise("Prensa/zancadas", 2, 3, "10-12"));
        exercises.add(new Exercise("Curl femoral", 2, 3, "10-12"));
        exercises.add(new Exercise("Pantorrilla", 2, 3, "12-15"));
        exercises.add(new Exercise("Elevación piernas colgado", 2, 3, "12-15"));

        // Day 3 - Tren superior hipertrofia
        exercises.add(new Exercise("Press inclinado mancuerna", 3, 3, "8-12"));
        exercises.add(new Exercise("Remo sentado polea", 3, 3, "10-12"));
        exercises.add(new Exercise("Jalón/dominada", 3, 3, "8-12"));
        exercises.add(new Exercise("Elevaciones laterales", 3, 3, "12-15"));
        exercises.add(new Exercise("Face pull", 3, 3, "15"));
        exercises.add(new Exercise("Curl martillo", 3, 3, "10-12"));
        exercises.add(new Exercise("Curl invertido+agarre disco", 3, 3, "12-15"));

        // Day 4 - Tren inferior core antebrazo
        exercises.add(new Exercise("Peso muerto", 4, 3, "5"));
        exercises.add(new Exercise("Sentadilla frontal goblet", 4, 3, "8-10"));
        exercises.add(new Exercise("Búlgaras", 4, 3, "10 c/pierna"));
        exercises.add(new Exercise("Extensión cuádriceps", 4, 3, "12-15"));
        exercises.add(new Exercise("Farmer carry", 4, 3, "30m"));
        exercises.add(new Exercise("Plancha/rueda abdominal", 4, 3, "serie"));

        return exercises;
    }

    public static List<Exercise> getExercisesForDay(int dayType) {
        List<Exercise> all = getAllExercises();
        List<Exercise> result = new ArrayList<>();
        for (Exercise e : all) {
            if (e.getDayType() == dayType) {
                result.add(e);
            }
        }
        return result;
    }

    public static String getDayName(int dayType) {
        switch (dayType) {
            case 1: return "Tren Superior Fuerza";
            case 2: return "Tren Inferior Core";
            case 3: return "Tren Superior Hipertrofia";
            case 4: return "Tren Inferior Core + Antebrazo";
            default: return "Día " + dayType;
        }
    }
}
