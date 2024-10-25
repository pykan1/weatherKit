package com.example.weather.di

import com.example.weather.data.api.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {

    fun provideApi(): WeatherService {
        return Retrofit.Builder().baseUrl("http://192.168.118.25:3000").addConverterFactory(
            GsonConverterFactory.create())
            .build().create(WeatherService::class.java)
    }

}