package com.example.weather.screen.map

import com.example.weather.data.api.WeatherService
import com.example.weather.di.ApiModule
import com.example.weather.screen.base.BaseViewModel

class MapViewModel: BaseViewModel<MapState>(MapState.InitState) {
    val weatherApi: WeatherService = ApiModule.provideApi()

}