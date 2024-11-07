package com.example.weather.screen.main

import com.example.weather.models.DailyResponse
import com.example.weather.models.DailyUI
import com.example.weather.models.MeasurementTimeRangeUI
import java.util.Date

data class MainState(
    val measurementTimeRange: MeasurementTimeRangeUI,
    val daily: List<DailyUI>,
    val dateStart: Date,
    val dateEnd: Date,
    val points: Int,
    val datesByPoints: List<DailyUI>,
    val loading: Boolean,
) {
    companion object {
        val InitState = MainState(MeasurementTimeRangeUI.Default, emptyList(), Date(), Date(), 100, emptyList(), true)
    }
}