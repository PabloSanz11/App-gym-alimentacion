package com.pablosanz.gymapp.util;

import java.util.Arrays;
import java.util.List;

/** Groups shopping-list ingredients into food categories for display. */
public class IngredientCategorizer {

    public static final String PROTEINAS = "Proteínas";
    public static final String CARBOHIDRATOS = "Carbohidratos";
    public static final String VERDURAS_FRUTAS = "Verduras y frutas";
    public static final String LACTEOS = "Lácteos";
    public static final String CONDIMENTOS = "Condimentos y salsas";
    public static final String OTROS = "Otros";

    public static final List<String> CATEGORY_ORDER = Arrays.asList(
            PROTEINAS, CARBOHIDRATOS, VERDURAS_FRUTAS, LACTEOS, CONDIMENTOS, OTROS);

    public static String categorize(String ingredientName) {
        if (ingredientName == null) return OTROS;
        String n = ingredientName.toLowerCase();

        if (n.contains("pollo") || n.contains("carne") || n.contains("res") || n.contains("huevo")
                || n.contains("clara") || n.contains("atún") || n.contains("atun") || n.contains("proteína")
                || n.contains("proteina") || n.contains("whey") || n.contains("chorizo") || n.contains("lenteja")
                || n.contains("frijol")) {
            return PROTEINAS;
        }
        if (n.contains("arroz") || n.contains("avena") || n.contains("pasta") || n.contains("camote")
                || n.contains("papa") || n.contains("tortilla") || n.contains("totopo") || n.contains("pan")) {
            return CARBOHIDRATOS;
        }
        if (n.contains("verdura") || n.contains("ensalada") || n.contains("jitomate") || n.contains("cebolla")
                || n.contains("aguacate") || n.contains("plátano") || n.contains("platano") || n.contains("fruta")
                || n.contains("lechuga") || n.contains("chile") || n.contains("nopal")) {
            return VERDURAS_FRUTAS;
        }
        if (n.contains("queso") || n.contains("leche") || n.contains("kéfir") || n.contains("kefir")
                || n.contains("yogur") || n.contains("cottage") || n.contains("panela")) {
            return LACTEOS;
        }
        if (n.contains("salsa") || n.contains("chipotle") || n.contains("cacahuate") || n.contains("nuez")
                || n.contains("nueces") || n.contains("especia") || n.contains("aceite")) {
            return CONDIMENTOS;
        }
        return OTROS;
    }
}
