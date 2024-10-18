package com.example.weather.screen.main

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

@Composable
fun TemperatureGraph(datesByPoints: List<DailyUI>) {
    val minTemp = datesByPoints.minOfOrNull { it.temperature } ?: 0.0
    val maxTemp = datesByPoints.maxOfOrNull { it.temperature } ?: 0.0
    val state = rememberLazyListState()
    val pointWidth = 60.dp

    // Формат для отображения даты на оси X
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    LaunchedEffect(datesByPoints) {
        if (datesByPoints.isNotEmpty()) {
            state.animateScrollToItem(datesByPoints.size - 1)
        }
    }
    val steps = abs(abs(maxTemp) - abs(minTemp)).toInt()/2
    Row(modifier = Modifier.fillMaxWidth()) {
        // Ось Y с подписями температур
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .height(250.dp)
        ) {
            val yStep = size.height / steps // 2 шагов для подписей температуры

            for (i in 0..steps) {
                val temp = minTemp + (i * (maxTemp - minTemp) / steps)
                val yOffset = size.height - (i * yStep)

                // Рисуем линии сетки для оси Y
                drawLine(
                    color = Color.Gray,
                    start = Offset(size.width, yOffset),
                    end = Offset(size.width - 10.dp.toPx(), yOffset), // короткие линии
                    strokeWidth = 2f
                )

                // Подпись температуры
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${temp.roundToInt()}°C",
                        10f,
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
                .height(250.dp) // Высота увеличена для осей
        ) {
            items(datesByPoints.size) { index ->
                val point = datesByPoints[index]
                val nextPoint =
                    if (index + 1 < datesByPoints.size) datesByPoints[index + 1] else null

                // Нормализуем температуру для оси Y
                val normalizedTemp = (point.temperature - minTemp) / (maxTemp - minTemp)

                Box(modifier = Modifier.height(300.dp)) {
                    Canvas(
                        modifier = Modifier.size(pointWidth, 350.dp)
                    ) {
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
                        val yPosition = size.height * (1 - normalizedTemp).toFloat()
                        drawCircle(
                            color = Color.Red,
                            radius = 4f,
                            center = Offset(size.width / 2, yPosition)
                        )

                        // Если есть следующая точка, рисуем линию к ней
                        nextPoint?.let {
                            val nextNormalizedTemp =
                                (it.temperature - minTemp) / (maxTemp - minTemp)
                            val nextYPosition = size.height * (1 - nextNormalizedTemp).toFloat()
                            drawLine(
                                color = Color.Blue,
                                start = Offset(size.width / 2, yPosition),
                                end = Offset(size.width + pointWidth.toPx() / 2, nextYPosition),
                                strokeWidth = 4f
                            )
                        }
                    }

                    // Подписи для оси X (даты), сдвиг ниже последней горизонтальной линии
                    Text(
                        text = dateFormat.format(point.ts),
                        fontSize = 10.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter).rotate(90f) // Смещение ниже графика
                    )
                }
            }
        }
    }
}
