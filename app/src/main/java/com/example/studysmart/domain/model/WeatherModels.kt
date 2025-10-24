package com.example.studysmart.domain.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val city: City,
    val list: List<WeatherItem>,
    val daily: List<DailyForecast>,
)

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val coor: Coordinates,
)

data class Coordinates(
    val lat: Double,
    val long: Double,
)

data class WeatherItem(
    @SerializedName("dt")
    val date: Long,
    val main: MainWeather,
    val weather: List<Weather>,
    val wind: Wind,
    @SerializedName("pop")
    val precipitationProbability: Double = 0.0
)

data class MainWeather(
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class DailyForecast(
    val date: Long,
    val temperature: Temperature,
    val weather: List<Weather>,
    val humidity: Int,
    val windSpeed: Double,
    val precipitationProbability: Double
)

data class Temperature(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double
)