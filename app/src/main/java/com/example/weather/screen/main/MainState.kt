package com.example.weather.screen.main

data class MainState(
    val s: String
) {
    companion object {
        val InitState = MainState("")
    }
}