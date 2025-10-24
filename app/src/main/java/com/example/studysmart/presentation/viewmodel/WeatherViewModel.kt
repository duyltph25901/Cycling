package com.example.studysmart.presentation.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.network.Config
import com.example.studysmart.domain.model.BikeRidingScore
import com.example.studysmart.domain.model.DailyForecast
import com.example.studysmart.domain.model.Temperature
import com.example.studysmart.domain.model.WeatherResponse
import com.example.studysmart.domain.usecase.CalculateBikeRidingScoreUseCase
import com.example.studysmart.domain.usecase.GetWeatherForecastUseCase
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val calculateBikeRidingScoreUseCase: CalculateBikeRidingScoreUseCase
): ViewModel() {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val _locationPermissionGranted = mutableStateOf(false)
    val locationPermissionGranted: State<Boolean> = _locationPermissionGranted

    private val _weatherState = mutableStateOf(WeatherState())
    val weatherState: State<WeatherState> = _weatherState

    private val _dailyScores = mutableStateOf<List<Pair<DailyForecast, BikeRidingScore>>>(emptyList())
    val dailyScores: State<List<Pair<DailyForecast, BikeRidingScore>>> = _dailyScores

    fun checkLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        _locationPermissionGranted.value = hasPermission

        if (hasPermission) {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        fetchWeatherData(it.latitude, it.longitude)
                    }
                }.addOnFailureListener { exception ->
                    _weatherState.value =
                        _weatherState.value.copy(
                            isLoading = false,
                            error = "Fail to get location: ${exception.message}"
                        )
                }
        }
    }

    private fun fetchWeatherData(lat: Double, long: Double) {
        _weatherState.value =
            _weatherState.value.copy(
                isLoading = true,
                error = null
            )

        viewModelScope.launch {
            getWeatherForecastUseCase.invoke(lat, long)
                .onSuccess { response ->
                    val dailyForecast = processForecastIntoDaily(response)
                    val score = dailyForecast.map { forecast ->
                        forecast to calculateBikeRidingScoreUseCase.invoke(forecast)
                    }
                    _dailyScores.value = score
                    _weatherState.value =
                        _weatherState.value.copy(
                            isLoading = false,
                            weatherData = response.copy(
                                daily = dailyForecast
                            ),
                            error = null
                        )
                }.onFailure { exception ->
                    _weatherState.value =
                        _weatherState.value.copy(
                            isLoading = false,
                            error = "Fail to get location: ${exception.message}"
                        )
                }
        }
    }

    private fun processForecastIntoDaily(response: WeatherResponse): List<DailyForecast> {
        val allDailyForecasts = mutableListOf<DailyForecast>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dailyGroups = response.list.groupBy { weather ->
            dateFormat.format(weather.date * 1000)
        }

        dailyGroups.values.forEach { singleDayForecast ->
            if (singleDayForecast.isNotEmpty()) {
                val firstForecast = singleDayForecast.first()
                val maxTemp = singleDayForecast.maxOf { it.main.tempMax }
                val minTemp = singleDayForecast.minOf { it.main.tempMin }
                val avgHumidity = singleDayForecast.map { it.main.humidity }.average().toInt()
                val avgWindSpeed = singleDayForecast. map { it.wind.speed }.average()
                val avgPrecipitation = singleDayForecast.map { it.precipitationProbability }.average()

                val mostCommonWeather = singleDayForecast.flatMap { it.weather }
                    .groupBy { it.main }
                    .maxByOrNull { it.value.size }
                    ?.value?.first() ?: firstForecast.weather.first()
                
                val dailyForecast = DailyForecast(
                    date = firstForecast.date,
                    temperature = Temperature(
                        day = firstForecast.main.temp,
                        min = minTemp,
                        max = maxTemp,
                        night = firstForecast.main.temp
                    ),
                    weather = listOf(mostCommonWeather),
                    humidity = avgHumidity,
                    windSpeed = avgWindSpeed,
                    precipitationProbability = avgPrecipitation
                )

                allDailyForecasts.add(dailyForecast)
            }
        }

        return allDailyForecasts.take(6)
    }

    fun getIconWeatherUrl(iconCode: String) =
        "${Config.WEATHER_ICON_BASE_URL}$iconCode@2x.png"

    fun formatDate(timeStamp: Long): String {
        val date = Date(timeStamp * 1000)
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return dateFormat.format(date)
    }
}

data class WeatherState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val error: String? = null
) {

}
