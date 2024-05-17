package com.example.weather_kemelova.models;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

public class WeatherModel {
    @SerializedName("icon")
    String icon;
    @SerializedName("cod")
    Integer cod;
}
