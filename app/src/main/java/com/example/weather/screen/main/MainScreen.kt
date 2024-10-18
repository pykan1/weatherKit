package com.example.weather.screen.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.models.DailyUI
import java.text.SimpleDateFormat
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(city: Int, cityName: String) {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.stateFlow.collectAsState()
    var sliderValue by remember { mutableFloatStateOf(5f) }
    LaunchedEffect(viewModel) {
        viewModel.loadData(city, cityName)
    }
    Column(
        modifier = Modifier
            .verticalScroll(
                rememberScrollState()
            )
            .fillMaxSize()
    ) {

        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = cityName,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp)
        )

        if (state.daily.isNotEmpty()) {
            if (state.points != 0) {
                var sliderState by remember {
                    mutableStateOf(
                        SliderState(
                            value = state.points.toFloat(),
                            valueRange = 0f..state.daily.size.toFloat(),
                            onValueChangeFinished = {
                            },
                            steps = state.daily.size - 1
                        )
                    )
                }


                LaunchedEffect(sliderState.value) {
                    sliderValue = sliderState.value
                    viewModel.changePoints(sliderValue.roundToInt())
                }

                Slider(
                    state = sliderState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                val selectedStep = sliderValue.roundToInt()
                Text(text = sliderState.value.toString())
                Text(text = selectedStep.toString())
                Text(text = sliderState.steps.toString())
                if (!state.loading) {
                    Spacer(modifier = Modifier.size(30.dp))
                    TemperatureGraph(datesByPoints = state.datesByPoints)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(80.dp)
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TemperatureGraph(datesByPoints: List<DailyUI>) {
    val minTemp = (datesByPoints.minOfOrNull { it.temperature }?.toInt()?.let {
        ((it - 5) / 5) * 5 // Округление минимальной температуры до ближайшего кратного 5 с запасом
    } ?: 0)
    val maxTemp = (datesByPoints.maxOfOrNull { it.temperature }?.toInt()?.let {
        ceil((it + 5).toDouble() / 5).toInt() * 5 // Округление максимальной температуры до ближайшего кратного 5 с запасом
    } ?: 0)

    println("minTemp - $minTemp")
    println("maxTemp - $maxTemp")

    val state = rememberLazyListState()
    val pointWidth = 60.dp

    // Формат для отображения даты на оси X
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    LaunchedEffect(datesByPoints) {
        if (datesByPoints.isNotEmpty()) {
            state.animateScrollToItem(datesByPoints.size - 1)
        }
    }

    // Определяем количество шагов по Y с шагом 5
    val steps = ((maxTemp - minTemp) / 5) + 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Ось Y с подписями температур
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .height(pointWidth * (steps / 2))
        ) {
            val yStep = size.height / steps // Расчет высоты шага

            for (i in 0 until steps) {
                val temp = minTemp + i * 5 // Температуры кратны 5
                val yOffset = size.height - (i * yStep)

                // Подпись температуры
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${temp}°C",
                        5f,
                        yOffset, // Небольшой сдвиг для текста
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 30f
                        }
                    )
                }
            }
        }

        // Основной график
        LazyRow(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(pointWidth * (steps / 2) + 50.dp) // Высота увеличена для осей
        ) {
            items(datesByPoints.size) { index ->
                val point = datesByPoints[index]
                val nextPoint =
                    if (index + 1 < datesByPoints.size) datesByPoints[index + 1] else null



                Box(modifier = Modifier.height(pointWidth * (steps / 2) + 50.dp)) {
                    Canvas(
                        modifier = Modifier.size(pointWidth, pointWidth * (steps / 2))
                    ) {
                        // Исправляем нормализацию температуры
                        val temperatureRange = maxTemp - minTemp
                        val normalizedTemp = (point.temperature - minTemp) / temperatureRange.toFloat()
                        // Рисуем сетку
                        val gridLineCount = steps
                        val step = size.height / gridLineCount

                        // Горизонтальные линии сетки
                        for (i in 0..gridLineCount) {
                            drawLine(
                                color = Color.Gray,
                                start = Offset(0f, i * step),
                                end = Offset(size.width, i * step),
                                strokeWidth = 1f
                            )
                        }

                        // Вертикальная линия на каждую точку X
                        drawLine(
                            color = Color.Gray,
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 1f
                        )

                        // Рисуем точку
                        val yPosition = size.height * (1 - normalizedTemp)
                        drawCircle(
                            color = Color.Red,
                            radius = 4f,
                            center = Offset(size.width / 2, yPosition.toFloat())
                        )

                        // Если есть следующая точка, рисуем линию к ней
                        nextPoint?.let {
                            val nextNormalizedTemp =
                                (it.temperature - minTemp) / (maxTemp - minTemp).toFloat()
                            val nextYPosition = size.height * (1 - nextNormalizedTemp)
                            drawLine(
                                color = Color.Blue,
                                start = Offset(size.width / 2, yPosition.toFloat()),
                                end = Offset(size.width + pointWidth.toPx() / 2, nextYPosition.toFloat()),
                                strokeWidth = 4f
                            )
                        }

                        // Рисуем текст даты
                        drawContext.canvas.nativeCanvas.apply {
                            save() // Сохраняем текущее состояние Canvas
                            // Перемещаем точку начала текста в нужное место и вращаем Canvas
                            rotate(90f, size.width / 2, size.height + 70f)
                            drawText(
                                dateFormat.format(point.ts),
                                size.width / 2,
                                size.height + 70f, // Позиционируем дату ниже графика
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 24f // Размер текста
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                            restore() // Восстанавливаем состояние Canvas
                        }
                    }
                }
            }
        }
    }
}



