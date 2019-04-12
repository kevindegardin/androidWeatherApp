package com.example.weatherapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.databinding.ActivitySearchedCityWeatherBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchedCityWeather extends AppCompatActivity {

    private String city = "";
    static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    static final String API_ID = "e7635207e7a83be8fa9925c5fc57402a";

    ActivitySearchedCityWeatherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_city_weather);

        Intent i = getIntent();
        city = i.getStringExtra("TypedCity");

        Log.d("onCreate: ", city);

        // Setting binding to view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searched_city_weather);

        // Get weather data
        this.getWeatherData();
    }

    void getWeatherData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherClient service = retrofit.create(WeatherClient.class);
        Call<WeatherResponse> call = service.getCurrentWeatherDataFromCity(city, API_ID);
        call.enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String city = weatherResponse.name;

                    Integer temperature = Math.round(weatherResponse.main.temp) - 273;

                    String detailledInformations =
                            "Temperature: " +
                            Math.round(weatherResponse.main.temp - 273) + "°" +
                            "\n" +
                            "Temperature(Min): " +
                            Math.round(weatherResponse.main.temp_min - 273) + "°" +
                            "\n" +
                            "Temperature(Max): " +
                            Math.round(weatherResponse.main.temp_max - 273) + "°" +
                            "\n" +
                            "Humidité: " +
                            weatherResponse.main.humidity + "%" +
                            "\n" +
                            "Pression: " +
                            weatherResponse.main.pressure;

                    binding.city.setText(city);
                    binding.temperature.setText(temperature + "°");
                    binding.detailledInformations.setText(detailledInformations);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                binding.detailledInformations.setText("Échec de la récupération des informations.");
            }

        });
    }
}
