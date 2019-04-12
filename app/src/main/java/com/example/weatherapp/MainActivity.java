package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.TextView;

import com.example.weatherapp.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private String lat;
    private String lon;
    private TextView weatherData;

    static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    static final String API_ID = "e7635207e7a83be8fa9925c5fc57402a";

    ActivityMainBinding binding;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Retrieve user location
        this.getUserLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        lon = String.valueOf(location.getLongitude());
        lat = String.valueOf(location.getLatitude());

        // Get weather data
        this.getCurrentData();

        Log.d("User location",location.getLongitude() + " " + location.getLatitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        return;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        return;
    }

    @Override
    public void onProviderEnabled(String provider) {
        return;
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
    }

    void getCurrentData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherClient service = retrofit.create(WeatherClient.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, API_ID);
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
                            weatherResponse.main.temp +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main.temp_min +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main.temp_max +
                            "\n" +
                            "Humidité: " +
                            weatherResponse.main.humidity +
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