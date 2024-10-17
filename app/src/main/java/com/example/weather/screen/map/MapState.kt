package com.example.weather.screen.map

data class MapState(
    val s: String
) {
    companion object {
        val InitState = MapState(
            ""
        )
    }
}