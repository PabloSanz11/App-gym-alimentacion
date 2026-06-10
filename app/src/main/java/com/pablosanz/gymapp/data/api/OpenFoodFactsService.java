package com.pablosanz.gymapp.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenFoodFactsService {

    @GET("cgi/search.pl")
    Call<FoodSearchResponse> searchFood(
            @Query("action") String action,
            @Query("search_terms") String query,
            @Query("json") int json,
            @Query("page_size") int pageSize,
            @Query("fields") String fields,
            @Query("lc") String languageCode,
            @Query("cc") String countryCode);

    @GET("api/v0/product/{barcode}.json")
    Call<ProductResponse> getProduct(@Path("barcode") String barcode);
}
