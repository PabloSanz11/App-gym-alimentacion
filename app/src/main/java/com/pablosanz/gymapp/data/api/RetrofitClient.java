package com.pablosanz.gymapp.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "https://world.openfoodfacts.org/";
    private static RetrofitClient instance;
    private final OpenFoodFactsService service;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(OpenFoodFactsService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }

    public OpenFoodFactsService getService() { return service; }
}
