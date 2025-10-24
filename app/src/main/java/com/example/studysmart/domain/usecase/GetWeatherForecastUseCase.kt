package com.example.studysmart.domain.usecase

import com.example.studysmart.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        lat: Double,
        long: Double
    ) = repository.getWeatherForecast(lat, long)
}