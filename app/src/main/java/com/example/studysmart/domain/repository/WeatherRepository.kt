package com.example.studysmart.domain.repository

import com.example.studysmart.domain.model.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherForecast(
        lat: Double,
        long: Double
    ): Result<WeatherResponse>
}