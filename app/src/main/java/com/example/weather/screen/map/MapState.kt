package com.example.weather.screen.map

import com.example.weather.models.CityLocationUi
import com.example.weather.models.CityUi
import com.example.weather.models.CoastlineUi
import com.example.weather.models.CountryUi
import com.example.weather.models.RegionUi

data class MapState(
    val coastline: List<CoastlineUi>,
    val regions: List<RegionUi>,
    val countries: List<CountryUi>,
    val cities: List<CityUi>,
    val showRegionMenu: Boolean,
    val showCountryMenu: Boolean,
    val showCityMenu: Boolean,
    val cityLocations: List<CityLocationUi>,

    val selectedRegion: RegionUi,
    val selectedCountry: CountryUi,
    val selectedCity: CityUi,
){
    companion object{
        val InitState = MapState(
            coastline = emptyList(),
            regions = emptyList(),
            countries = emptyList(),
            cities = emptyList(),
            cityLocations = emptyList(),
            showRegionMenu = false,
            showCountryMenu = false,
            showCityMenu = false,
            selectedRegion = RegionUi.Default,
            selectedCountry = CountryUi.Default,
            selectedCity = CityUi.Default
        )
    }
}