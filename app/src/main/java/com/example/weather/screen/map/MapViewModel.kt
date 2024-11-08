package com.example.weather.screen.map

import androidx.lifecycle.viewModelScope
import com.example.weather.data.api.WeatherService
import com.example.weather.data.api.launchOperation
import com.example.weather.di.ApiModule
import com.example.weather.ip
import com.example.weather.models.CityUi
import com.example.weather.models.CountryUi
import com.example.weather.models.MeasurementTimeRangeUI
import com.example.weather.models.RegionUi
import com.example.weather.models.toUI
import com.example.weather.models.toUi
import com.example.weather.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class MapViewModel(): BaseViewModel<MapState>(MapState.InitState){

    private val api = ApiModule.provideApi(ip)

    fun loadData(){
        viewModelScope.launch {
            getCostline()
            getRegions()
            getCitiesLocation()
        }
    }

    fun changeRegionMenuState(menuState: Boolean){
        reduce {
            state.copy(
                showRegionMenu = menuState
            )
        }
    }

    fun changeCountryMenuState(menuState: Boolean){
        reduce {
            state.copy(
                showCountryMenu = menuState
            )
        }
    }

    fun changeCityMenuState(menuState: Boolean){
        reduce {
            state.copy(
                showCityMenu = menuState
            )
        }
    }

    fun changeSelectedRegion(regionId: Int){
        reduce {
            state.copy(
                selectedRegion = state.regions.find { it.identifier == regionId }?: RegionUi.Default
            )
        }
        getCountries(regionId)
    }

    fun changeSelectedCountry(countryId: Int){
        reduce {
            state.copy(
                selectedCountry = state.countries.find { it.identifier == countryId }?: CountryUi.Default
            )
        }
        getCities(countryId)
    }

    fun changeSelectedCity(cityId: Int){
        reduce {
            state.copy(
                selectedCity = state.cities.find { it.identifier == cityId }?: CityUi.Default
            )
        }
    }



    fun getCostline(){
        viewModelScope.launch {
            launchOperation(
                call = { api.getCoastline() },
                onSuccess = { response ->
                    reduce {
                        state.copy(coastline = response.map { it.toUi() })
                    }
                }
            )
        }
    }

    fun getRegions(){
        viewModelScope.launch {
            launchOperation(
                call = { api.getRegionCountries() },
                onSuccess = { response ->
                    reduce {
                        state.copy(regions = response.map { it.toUi() })
                    }
                }
            )
        }
    }

    private fun getCountries(regionId: Int){
        viewModelScope.launch {
            launchOperation(
                call = { api.getCountriesFromRegion(regionId) },
                onSuccess = { response ->
                    reduce {
                        state.copy(countries = response.map { it.toUi() })
                    }
                }
            )
        }
    }

    fun getCities(countryId: Int){
        viewModelScope.launch {
            launchOperation(
                call = { api.getCitiesFromCountry(countryId) },
                onSuccess = { response ->
                    reduce {
                        state.copy(cities = response.map { it.toUi() })
                    }
                }
            )
        }
    }

    fun getCitiesLocation(){
        viewModelScope.launch {
            launchOperation(
                call = { api.getCityLocations() },
                onSuccess = { response ->
                    reduce {
                        state.copy(cityLocations = response.map { it.toUi() })
                    }
                }
            )
        }
    }
}