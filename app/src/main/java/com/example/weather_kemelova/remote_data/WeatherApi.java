package com.example.weather_kemelova.remote_data;


import com.example.weather_kemelova.models.Model;
import com.example.weather_kemelova.models.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    public static final String URL_KEY = "412e73a22716652aab35a5bf132c13a9";
    public static final String BASE_URL = "https://api.openweathermap.org";

//    String URL  = "412e73a22716652aab35a5bf132c13a9";

    @GET("/data/2.5/weather")
    Call<Model> getCurrentWeather(
            @Query("q") String name,
            @Query("appid") String key);


    @GET("/data/2.5/weather/?q=London&appid=412e73a22716652aab35a5bf132c13a9")
    Call<WeatherModel> getWeather(
            @Query("q") String name,
            @Query("appid") String key);
}
