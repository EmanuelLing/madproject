package com.example.madproject;

import com.example.madproject.ui.home.FoodMenuResponse;


import retrofit2.Call;
import retrofit2.http.GET;

public interface FoodMenuApi {
    @GET("menu/food")
    Call<FoodMenuResponse> getFoodMenu();

    @GET("menu/drinks")
    Call<FoodMenuResponse> getDrinkMenu();

}