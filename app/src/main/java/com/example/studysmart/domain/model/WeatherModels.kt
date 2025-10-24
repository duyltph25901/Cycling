package com.example.studysmart.domain.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val city: City? = null,
    val list: List<WeatherItem>? = null,
    val daily: List<DailyForecast>? = null,
)

data class City(
    val id: Int? = null,
    val name: String? = null,
    val country: String? = null,
    val coor: Coordinates? = null,
)

data class Coordinates(
    val lat: Double? = null,
    val long: Double? = null,
)

data class WeatherItem(
    @SerializedName("dt")
    val date: Long? = null,
    val main: MainWeather? = null,
    val weather: List<Weather>? = null,
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