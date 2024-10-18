package com.example.weather.screen.map

import androidx.lifecycle.viewModelScope
import com.example.weather.data.api.WeatherService
import com.example.weather.di.ApiModule
import com.example.weather.models.MeasurementTimeRangeUI
import com.example.weather.models.toUI
import com.example.weather.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class MapViewModel: BaseViewModel<MapState>(MapState.InitState) {
    val weatherApi: WeatherService = ApiModule.provideApi()
    fun loadData() {
        viewModelScope.launch {
            weatherApi.getCoastline().body()?.let {
                reduce {
                    state.copy(
                        coastline = it.map { it.toUI() }
                    )
                }
            }
        }
    }
}