package com.example.weather.models

class RegionResponse(
    val identifier: Int?,
    val region: String?,
    val countries: Int?
)

class RegionUi(
    val identifier: Int,
    val region: String,
    val countries: Int
){
    companion object{
        val Default = RegionUi(
            identifier = 0,
            region = "",
            countries = 0
        )
    }
}

fun RegionResponse.toUi(): RegionUi {
    val data = this
    return RegionUi(
        identifier = data.identifier?: 0,
        region = data.region.orEmpty(),
        countries = data.countries?: 0
    )
}