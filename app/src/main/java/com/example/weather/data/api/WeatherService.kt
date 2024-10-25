package com.example.weather.data.api

import com.example.weather.models.CityLocationResponse
import com.example.weather.models.CityResponse
import com.example.weather.models.CoastlineResponse
import com.example.weather.models.CountryResponse
import com.example.weather.models.DailyResponse
import com.example.weather.models.MeasurementTimeRangeResponse
import com.example.weather.models.RegionResponse
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

    @GET("region_countries")
    suspend fun getRegionCountries(): Response<List<RegionResponse>>

    @GET("rpc/get_countries")
    suspend fun getCountriesFromRegion(@Query("region") region: Int): Response<List<CountryResponse>>

    @GET("rpc/get_cities")
    suspend fun getCitiesFromCountry(@Query("country") country: Int): Response<List<CityResponse>>

    @GET("rpc/get_city_locations")
    suspend fun getCityLocations(): Response<List<CityLocationResponse>>

    @GET("rpc/get_daily_temperatures")
    suspend fun getDailyTemperatures(
        @Query("city") city: Int,
        @Query("ts_from") tsFrom: String,
        @Query("ts_to") tsTo: String,
    ): Response<List<DailyResponse>>
}