package com.example.weather.screen.map

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.weather.models.CityLocationUi
import com.example.weather.models.CityUi
import com.example.weather.models.CoastlineUi
import com.example.weather.models.CountryUi
import com.example.weather.models.RegionUi
import com.example.weather.screen.nav.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navHostController: NavHostController){

    val viewModel: MapViewModel = viewModel()
    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(Unit){
        viewModel.loadData()
    }

    if (state.coastline.isNotEmpty()){
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Column {
                Spacer(modifier = Modifier.size(14.dp))
                Text(text = "Карта мира", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 22.sp, fontWeight = Bold)
                Spacer(modifier = Modifier.size(14.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f), thickness = 2.dp)
                Spacer(modifier = Modifier.size(14.dp))
                WorldMap(
                    state.coastline,
                    selectedCity = state.cityLocations.find { it.identifier == state.selectedCity.identifier }?: CityLocationUi.Default,
                    cityList = state.cityLocations.filter { cityLocation ->
                        state.cities.any { city -> city.identifier == cityLocation.identifier }},
                    onSelectItem = { id ->
                        viewModel.changeSelectedCity(id)
                    }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Регион:", fontSize = 18.sp)
                        ExposedDropdownMenuBox(
                            modifier = Modifier.padding(start = 12.dp),
                            expanded = state.showRegionMenu,
                            onExpandedChange = {
                                viewModel.changeRegionMenuState(it)
                            }
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor(),
                                value = state.selectedRegion.region,
                                onValueChange = {
                                },
                                label = { Text("Выберите регион") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = state.showRegionMenu
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = state.showRegionMenu,
                                onDismissRequest = {
                                    viewModel.changeRegionMenuState(false)
                                }
                            ) {
                                state.regions.forEach { region ->
                                    DropdownMenuItem(
                                        text = { Text(region.region) },
                                        onClick = {
                                            viewModel.changeSelectedRegion(region.identifier)
                                            viewModel.changeRegionMenuState(false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Страна:", fontSize = 18.sp)
                        ExposedDropdownMenuBox(
                            modifier = Modifier.padding(start = 12.dp),
                            expanded = state.showCountryMenu,
                            onExpandedChange = {
                                viewModel.changeCountryMenuState(it)
                            }
                        ) {
                            TextField(
                                enabled = state.selectedRegion != RegionUi.Default,
                                modifier = Modifier
                                    .menuAnchor(),
                                value = state.selectedCountry.description,
                                onValueChange = {
                                },
                                label = { Text("Выберите страну") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = state.showCountryMenu
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = state.showCountryMenu,
                                onDismissRequest = {
                                    viewModel.changeCountryMenuState(false)
                                }
                            ) {
                                state.countries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text(country.description) },
                                        onClick = {
                                            viewModel.changeSelectedCountry(country.identifier)
                                            viewModel.changeCountryMenuState(false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Город:", fontSize = 18.sp)
                        ExposedDropdownMenuBox(
                            modifier = Modifier.padding(start = 12.dp),
                            expanded = state.showCityMenu,
                            onExpandedChange = {
                                viewModel.changeCityMenuState(it)
                            }
                        ) {
                            TextField(
                                enabled = state.selectedCountry != CountryUi.Default,
                                modifier = Modifier
                                    .menuAnchor(),
                                value = state.selectedCity.description,
                                onValueChange = {
                                },
                                label = { Text("Выберите город") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = state.showCityMenu
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = state.showCityMenu,
                                onDismissRequest = {
                                    viewModel.changeCityMenuState(false)
                                }
                            ) {
                                state.cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city.description) },
                                        onClick = {
                                            viewModel.changeSelectedCity(city.identifier)
                                            viewModel.changeCityMenuState(false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        if (state.selectedCity != CityUi.Default){
                            navHostController.navigate(Screen.MainScreen.setCity(city_id = state.selectedCity.identifier, city_name = state.selectedCity.description))
                        }
                    }
                ){
                    Text(text = "Открыть")
                }
            }
        }
    }else{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }
}

@Composable
fun WorldMap(
    points: List<CoastlineUi>,
    selectedCity: CityLocationUi,
    cityList: List<CityLocationUi>,
    onSelectItem: (Int) -> Unit
) {
    Box(
        modifier = Modifier.padding(6.dp)
    ) {
        Canvas(
            modifier = Modifier
                .aspectRatio(8f / 5)
                .pointerInput(cityList) {
                    detectTapGestures { offset ->
                        val firstPoint = points.first()
                        val minLatitude = points.minOf { it.latitude }
                        val maxLatitude = points.maxOf { it.latitude }
                        val minLongitude = points.minOf { it.longitude }
                        val maxLongitude = points.maxOf { it.longitude }

                        // Преобразование координат нажатия в широту и долготу
                        val latitude =
                            xToLatitude(offset.x, minLatitude, maxLatitude, size.width.toFloat())
                        val longitude = yToLongitude(
                            offset.y,
                            minLongitude,
                            maxLongitude,
                            size.height.toFloat()
                        )

                        val currentCity = cityList.find {
                            it.longitude in latitude - 4..latitude + 4 && it.latitude in longitude - 4..longitude + 4
                        }
                        currentCity?.let {
                            onSelectItem(it.identifier)
                        }
//                        Log.d("Tapped", "Latitude: ${cityList.getOrNull(0)?.latitude.toString()?: "0.0"}, Longitude: ${cityList.getOrNull(0)?.longitude.toString()?: "0.0"}")
//                        Log.d("Tapped", "Latitude: $latitude, Longitude: $longitude")
//                        Log.d("Tapped", "$currentCity")
                    }
                }

        ) {
            if (points.isNotEmpty()) {
                // Определяем границы карты
                val minLatitude = points.minOf { it.latitude }
                val maxLatitude = points.maxOf { it.latitude }
                val minLongitude = points.minOf { it.longitude }
                val maxLongitude = points.maxOf { it.longitude }

                val path = Path().apply {
                    val firstPoint = points.first()
                    var lastX = latitudeToX(firstPoint.latitude, minLatitude, maxLatitude, size.width)
                    var lastY = longitudeToY(firstPoint.longitude, minLongitude, maxLongitude, size.height)

                    moveTo(lastX, lastY)

                    val threshold = 30f // Пороговое значение для расстояния между точками

                    for (i in 1 until points.size) {
                        val point = points[i]
                        val x = latitudeToX(point.latitude, minLatitude, maxLatitude, size.width)
                        val y = longitudeToY(point.longitude, minLongitude, maxLongitude, size.height)

                        // Рассчитываем расстояние между текущей и предыдущей точкой
                        val distance = Math.hypot((x - lastX).toDouble(), (y - lastY).toDouble())

                        if (distance > threshold) {
                            // Если расстояние слишком большое, делаем moveTo
                            moveTo(x, y)
                        } else {
                            // Иначе рисуем линию
                            lineTo(x, y)
                        }

                        // Обновляем последнюю точку
                        lastX = x
                        lastY = y
                    }
                }

                // Проверяем, корректно ли отрисовывается Path
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 1.dp.toPx())
                )

                // Отображаем крестик для выбранного города
                cityList.forEach { item ->
                    val cityX = latitudeToX(item.longitude, minLatitude, maxLatitude, size.width)
                    val cityY = longitudeToY(item.latitude, minLongitude, maxLongitude, size.height)

                    Log.d("BIgCity", "${cityX}")
                    Log.d("BIgCity", "${cityY}")

                    // Рисуем крестик
                    val crossSize = 6.dp.toPx()
                    drawLine(
                        color = if (item == selectedCity) {
                            Color.Red} else{
                            Color.Red.copy(0.2f)} ,
                        start = Offset(cityX - crossSize, cityY - crossSize),
                        end = Offset(cityX + crossSize, cityY + crossSize),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = if (item == selectedCity) {
                            Color.Red} else{
                            Color.Red.copy(0.2f)} ,
                        start = Offset(cityX - crossSize, cityY + crossSize),
                        end = Offset(cityX + crossSize, cityY - crossSize),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            } else {
                Log.d("WorldMap", "No points to draw")
            }
        }
    }
}

private fun latitudeToX(latitude: Double, minLatitude: Double, maxLatitude: Double, width: Float): Float {
    val normalizedLatitude = (latitude - minLatitude) / (maxLatitude - minLatitude)
    return (normalizedLatitude * width).toFloat()
}

private fun longitudeToY(longitude: Double, minLongitude: Double, maxLongitude: Double, height: Float): Float {
    val normalizedLongitude = (longitude - minLongitude) / (maxLongitude - minLongitude)
    return ((1 - normalizedLongitude) * height).toFloat() // Инвертируем Y
}

private fun xToLatitude(x: Float, minLatitude: Double, maxLatitude: Double, width: Float): Double {
    val normalizedX = x / width
    return normalizedX * (maxLatitude - minLatitude) + minLatitude
}

private fun yToLongitude(y: Float, minLongitude: Double, maxLongitude: Double, height: Float): Double {
    val normalizedY = 1 - (y / height)  // Инвертируем Y обратно
    return normalizedY * (maxLongitude - minLongitude) + minLongitude
}