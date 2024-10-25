package com.example.weather.models

data class CoastlineResponse(
    val shape: Int?,
    val segment: Int?,
    val latitude: Double?,
    val longitude: Double?
)

data class CoastlineUi(
    val shape: Int,
    val segment: Int,
    val latitude: Double,
    val longitude: Double
)

fun CoastlineResponse.toUi(): CoastlineUi {
    val data = this
    return CoastlineUi(
        shape = data.shape?: 0,
        segment = data.segment?: 0,
        latitude = data.latitude?: 0.0,
        longitude = data.longitude?: 0.0
    )
}