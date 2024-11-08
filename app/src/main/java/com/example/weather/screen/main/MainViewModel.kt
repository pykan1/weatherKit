package com.example.weather.screen.main

import androidx.lifecycle.viewModelScope
import com.example.weather.data.api.WeatherService
import com.example.weather.data.api.launchOperation
import com.example.weather.di.ApiModule
import com.example.weather.ip
import com.example.weather.models.MeasurementTimeRangeUI
import com.example.weather.models.formatDateToISOString
import com.example.weather.models.generateEqualDates
import com.example.weather.models.toUI
import com.example.weather.screen.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel() :
    BaseViewModel<MainState>(MainState.InitState) {
    private val weatherApi: WeatherService = ApiModule.provideApi(ip)
    var jobChangePoint: Job? = null
    fun changePoints(points: Int) {
        jobChangePoint?.cancel()
        jobChangePoint = CoroutineScope(Dispatchers.IO).launch {
            delay(300L)
            reduce {
                state.copy(
                    points = points
                )
            }
            changeDateRange(points)
        }
    }

    var jobChangeDate: Job? = null
    fun changeDate(
        startDate: Date = state.dateStart,
        endDate: Date = state.dateEnd,
        after: () -> Unit = {}
    ) {
        println("endDate - $endDate")
        jobChangeDate?.cancel()
        jobChangeDate = CoroutineScope(Dispatchers.IO).launch {
            delay(200L)
            reduce {
                state.copy(
                    dateStart = startDate,
                    dateEnd = endDate
                )
            }
            after()
            changeDateRange(startDate = startDate, endDate = endDate)
        }
    }

    var jobChangeRange: Job? = null
    fun changeDateRange(
        points: Int = state.points,
        endDate: Date = state.dateEnd,
        startDate: Date = state.dateStart
    ) {
        println("changeDateRange")
        jobChangeRange?.cancel()
        jobChangeRange = CoroutineScope(Dispatchers.IO).launch {
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
        CoroutineScope(Dispatchers.IO).launch {
            reduce { state.copy(loading = true) }
            launchOperation(call = {
                weatherApi.getMeasurementTimeRange(city)
            }) {
                val measurementTimeRange = it.firstOrNull()?.toUI()
                    ?: MeasurementTimeRangeUI.Default
                reduce {
                    state.copy(
                        measurementTimeRange = measurementTimeRange,
                        dateStart = measurementTimeRange.tsMin,
                        dateEnd = measurementTimeRange.tsMax
                    )
                }
                viewModelScope.launch {
                    launchOperation(call = {
                        weatherApi.getDailyTemperatures(
                            city,
                            measurementTimeRange.tsMin.formatDateToISOString(),
                            measurementTimeRange.tsMax.formatDateToISOString()
                        )
                    }) {
                        reduce {
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

}