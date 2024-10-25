package com.example.weather.models

import java.util.Date

data class DailyResponse(
    val ts: String?,
    val temperature: Double?
)

data class DailyUI(
    val ts: Date,
    val temperature: Double
) {
    companion object {
        val Default = DailyUI(
            ts = Date(),
            temperature = 0.0
        )
    }
}

fun DailyResponse.toUI(): DailyUI {
    return DailyUI(
        ts = ts.orEmpty().parseDate(),
        temperature = temperature.orEmpty()
    )
}

fun Double?.orEmpty(): Double = this ?: 0.0

fun Int?.orEmpty(): Int = this ?: 0

fun Long?.orEmpty(): Long = this ?: 0