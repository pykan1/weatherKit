package com.example.weather.screen.main

import androidx.lifecycle.viewModelScope
import com.example.weather.data.api.WeatherService
import com.example.weather.di.ApiModule
import com.example.weather.models.MeasurementTimeRangeUI
import com.example.weather.models.formatDateToISOString
import com.example.weather.models.generateEqualDates
import com.example.weather.models.toUI
import com.example.weather.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel() :
    BaseViewModel<MainState>(MainState.InitState) {
    private val weatherApi: WeatherService = ApiModule.provideApi()
    var job: Job? = null
    fun changePoints(points: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            delay(300L)
            reduce {
                state.copy(
                    points = points
                )
            }
            changeDateRange(points)
        }
    }


    fun changeDateRange(points: Int = state.points) {
        println("changeDateRange")
        viewModelScope.launch {
            reduce { state.copy(loading = true) }

            val dates = generateEqualDates(
                allDays = state.daily,
                dateFrom = state.dateStart,
                dateTo = state.dateEnd,
                points = points
            )
            println("dates - $dates")
            reduce {
                state.copy(
                    datesByPoints = dates, loading = false
                )
            }
        }
    }

    fun loadData(city: Int, cityName: String) {
        viewModelScope.launch {
            reduce { state.copy(loading = true) }
            weatherApi.getMeasurementTimeRange(city).body()?.let {
                val measurementTimeRange = it.firstOrNull()?.toUI()
                    ?: MeasurementTimeRangeUI.Default
                reduce {
                    state.copy(
                        measurementTimeRange = measurementTimeRange,
                        dateStart = measurementTimeRange.tsMin,
                        dateEnd = measurementTimeRange.tsMax
                    )
                }
                weatherApi.getDailyTemperatures(
                    city,
                    measurementTimeRange.tsMin.formatDateToISOString(),
                    measurementTimeRange.tsMax.formatDateToISOString()
                ).body()?.let {
                    println("daily tyt nax")
                    reduce {
                        println("reduce кальчик нах")
                        state.copy(
                            daily = it.map { it.toUI() },
                        )
                    }
                    changeDateRange()
                }
            }
        }
    }

}