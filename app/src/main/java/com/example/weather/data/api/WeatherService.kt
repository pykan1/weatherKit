package com.example.weather.data.api

import com.example.weather.models.CoastlineResponse
import com.example.weather.models.DailyResponse
import com.example.weather.models.MeasurementTimeRangeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("rpc/get_measurement_time_range")
    suspend fun getMeasurementTimeRange(
        @Query("city") city: Int
    ): Response<List<MeasurementTimeRangeResponse>>

    @GET("coastline")
    suspend fun getCoastline(): Response<List<CoastlineResponse>>

    @GET("rpc/get_daily_temperatures")
    suspend fun getDailyTemperatures(
        @Query("city") city: Int,
        @Query("ts_from") tsFrom: String,
        @Query("ts_to") tsTo: String,
    ): Response<List<DailyResponse>>
}