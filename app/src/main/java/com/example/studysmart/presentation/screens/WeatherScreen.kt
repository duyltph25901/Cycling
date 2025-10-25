package com.example.studysmart.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel<WeatherViewModel>()
) {
    val weatherState by viewModel.weatherState
    val locationPermissionGranted by viewModel.locationPermissionGranted
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.checkLocationPermission()
        }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.checkLocationPermission()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E293B),
                                Color(0xFF334155),
                            )
                        )
                    )
            ) {
                when {
                    weatherState.isLoading && weatherState.weatherData == null -> {
                        LoadingScreen()
                    }

                    weatherState.error != null -> {
                        ErrorScreen(
                            error = weatherState.error!!,
                            onRetry = {
                                viewModel.checkLocationPermission()
                            }
                        )
                    }

                    weatherState.weatherData != null -> {

                    }
                }
            }
        }
    }
}