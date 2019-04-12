package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherClient {

    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("appId") String APP_ID);

    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherDataFromCity(@Query("q") String city, @Query("appId") String APP_ID);

}