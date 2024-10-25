package com.example.weather.models

class CountryResponse(
    val identifier: Int?,
    val description: String?
)

class CountryUi(
    val identifier: Int,
    val description: String
){
    companion object{
        val Default = CountryUi(
            identifier = 0,
            description = ""
        )
    }
}

fun CountryResponse.toUi(): CountryUi {
    val data = this
    return CountryUi(
        identifier = data.identifier?: 0,
        description = data.description.orEmpty()
    )
}