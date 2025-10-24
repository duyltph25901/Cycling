package com.example.studysmart.data.network.repository

import com.example.studysmart.data.network.Config
import com.example.studysmart.data.network.WeatherApiService
import com.example.studysmart.domain.model.WeatherResponse
import com.example.studysmart.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherApiService
): WeatherRepository {
    override suspend fun getWeatherForecast(
        lat: Double,
        long: Double
    ): Result<WeatherResponse> =
        try {
            val response = service.getWeatherForecast(lat, long, Config.OPENWEATHER_API_KEY)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
}