package com.example.weather.screen.main

import androidx.lifecycle.viewModelScope
import com.example.weather.data.api.WeatherService
import com.example.weather.di.ApiModule
import com.example.weather.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel() :
    BaseViewModel<MainState>(MainState.InitState) {
    val weatherApi: WeatherService = ApiModule.provideApi()
    fun loadData() {
        viewModelScope.launch {

        }
    }

}