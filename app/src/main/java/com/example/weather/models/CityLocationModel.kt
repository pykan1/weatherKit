package com.example.weather.models

data class CityLocationResponse(
    val identifier: Int?,
    val latitude: Double?,
    val longitude: Double?
)

data class CityLocationUi(
    val identifier: Int,
    val latitude: Double,
    val longitude: Double
){
    companion object {
        val Default = CityLocationUi(
            identifier = 0,
            latitude = 0.0,
            longitude = 0.0
        )
    }
}

fun CityLocationResponse.toUi(): CityLocationUi {
    val data = this
    return CityLocationUi(
        identifier = data.identifier?: 0,
        latitude = data.latitude?: 0.0,
        longitude = data.longitude?: 0.0
    )
}