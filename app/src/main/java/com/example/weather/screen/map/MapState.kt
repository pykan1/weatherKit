package com.example.weather.screen.map

import com.example.weather.models.CoastlineUI

data class MapState(
    val coastline: List<CoastlineUI>
) {
    companion object {
        val InitState = MapState(
            emptyList()
        )
    }
}