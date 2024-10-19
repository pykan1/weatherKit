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
import java.util.Date

class MainViewModel() :
    BaseViewModel<MainState>(MainState.InitState) {
    private val weatherApi: WeatherService = ApiModule.provideApi()
    var jobChangePoint: Job? = null
    fun changePoints(points: Int) {
        jobChangePoint?.cancel()
        jobChangePoint = viewModelScope.launch {
            delay(300L)
            reduce {
                state.copy(
                    points = points
                )
            }
            changeDateRange(points)
        }
    }

    fun changeDate(startDate: Date = state.dateStart, endDate: Date = state.dateEnd) {
        println("endDate - $endDate")
        viewModelScope.launch {
            reduce {
                state.copy(
                    dateStart = startDate,
                    dateEnd = endDate
                )
            }
            changeDateRange(startDate = startDate, endDate = endDate)
        }
    }

    var jobChangeRange: Job? = null
    fun changeDateRange(points: Int = state.points, endDate: Date = state.dateEnd, startDate: Date = state.dateStart) {
        println("changeDateRange")
        jobChangeRange?.cancel()
        jobChangeRange = viewModelScope.launch {
            reduce { state.copy(loading = true) }
            delay(300L)
            val dates = generateEqualDates(
                allDays = state.daily,
                dateFrom = startDate,
                dateTo = endDate,
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