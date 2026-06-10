package com.pablosanz.gymapp.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://world.openfoodfacts.org/";
    private static RetrofitClient instance;
    private final OpenFoodFactsService service;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(OpenFoodFactsService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public OpenFoodFactsService getService() {
        return service;
    }
}
