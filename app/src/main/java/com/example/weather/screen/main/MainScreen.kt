package com.example.weather.screen.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.example.weather.models.orEmpty
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(city: Int, cityName: String) {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.stateFlow.collectAsState()
    var sliderValue by remember { mutableFloatStateOf(20f) }
    var openStartDialog by remember {
        mutableStateOf(false)
    }
    var openEndDialog by remember {
        mutableStateOf(false)
    }
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
                var sliderStartValue by remember { mutableFloatStateOf(0f) }
                var sliderEndValue by remember { mutableFloatStateOf((state.daily.size-1).toFloat()) }
                val startDatePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = state.dateStart.time
                )

                val endDatePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = state.dateEnd.time
                )
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

                var sliderStartState by remember {
                    mutableStateOf(
                        SliderState(
                            value = 0f,
                            valueRange = 0f..state.daily.size.toFloat(),
                            onValueChangeFinished = {
                            },
                            steps = state.daily.size - 1
                        )
                    )
                }

                var sliderEndState by remember {
                    mutableStateOf(
                        SliderState(
                            value = state.daily.size.toFloat(),
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

                LaunchedEffect(sliderStartState.value) {
                    sliderStartValue = sliderStartState.value
                    state.daily.getOrNull(sliderStartState.value.roundToInt())?.let {
                        viewModel.changeDate(startDate = it.ts)
                    }
                }

                LaunchedEffect(sliderEndState.value) {
                    sliderEndValue = sliderEndState.value
                    state.daily.getOrNull(sliderEndState.value.roundToInt())?.let {
                        viewModel.changeDate(endDate = it.ts)
                    }
                }
                val selectedStep = sliderValue.roundToInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Точек($selectedStep)",
                        fontSize = 20.sp
                    )
                    Slider(
                        state = sliderState,
                        modifier = Modifier.weight(1f)

                    )
                }
                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Старт",
                        fontSize = 20.sp
                    )
                    Slider(
                        state = sliderStartState,
                        modifier = Modifier
                            .weight(1f)

                    )
                }
                Spacer(modifier = Modifier.size(20.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Конец",
                        fontSize = 20.sp
                    )
                    Slider(
                        state = sliderEndState,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    val startMutableInteractionSource = remember {
                        MutableInteractionSource()
                    }
                    val endMutableInteractionSource = remember {
                        MutableInteractionSource()
                    }
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f),
                        value = state.dateStart.toUI(),
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 1,
                        singleLine = true,
                        leadingIcon = {
                            IconButton(onClick = { openStartDialog = true }) {
                                androidx.compose.material3.Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                        interactionSource = startMutableInteractionSource
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            openStartDialog = true
                                        }
                                    }
                                }
                            },
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                openEndDialog = true
                            },
                        value = state.dateEnd.toUI(),
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 1,
                        singleLine = true,
                        leadingIcon = {
                            IconButton(onClick = { openEndDialog = true }) {
                                androidx.compose.material3.Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                        interactionSource = endMutableInteractionSource
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            openEndDialog = true
                                        }
                                    }
                                }
                            },
                    )
                }
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
                if (openStartDialog) {
                    val confirmEnabled = remember {
                        derivedStateOf { startDatePickerState.selectedDateMillis != null }
                    }
                    DatePickerDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when the user clicks outside the dialog or on the back
                            // button. If you want to disable that functionality, simply use an empty
                            // onDismissRequest.
                            openStartDialog = false
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.changeDate(startDate = Date(startDatePickerState.selectedDateMillis.orEmpty()))
                                    openStartDialog = false
                                },
                                enabled = confirmEnabled.value
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openStartDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = startDatePickerState)
                    }
                }

                if (openEndDialog) {
                    val confirmEnabled = remember {
                        derivedStateOf { endDatePickerState.selectedDateMillis != null }
                    }
                    DatePickerDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when the user clicks outside the dialog or on the back
                            // button. If you want to disable that functionality, simply use an empty
                            // onDismissRequest.
                            openEndDialog = false
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.changeDate(endDate = Date(endDatePickerState.selectedDateMillis.orEmpty()))
                                    openEndDialog = false

                                },
                                enabled = confirmEnabled.value
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openEndDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = endDatePickerState)
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
    val minTemp = datesByPoints.minOfOrNull { it.temperature }?.let {
        it - (it % 5) - 5 // Округление вниз до ближайшего кратного 5
    } ?: 0.0

    val maxTemp = datesByPoints.maxOfOrNull { it.temperature }?.let {
        it + (5 - it % 5) // Округление вверх до ближайшего кратного 5
    } ?: 0.0

    val state = rememberLazyListState()
    val pointWidth = 30.dp

    // Формат для отображения даты на оси X
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    LaunchedEffect(datesByPoints) {
        if (datesByPoints.isNotEmpty()) {
            state.animateScrollToItem(datesByPoints.size - 1)
        }
    }

    // Определяем количество шагов по Y с шагом 5
    val steps = ceil(((maxTemp - minTemp) / 5)).toInt()

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
                .height(pointWidth * steps)
        ) {
            val yStep = size.height / steps

            for (i in 0..steps) {
                val temp = minTemp + i * 5
                val yOffset = size.height - i * yStep

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${temp}°C",
                        5f,
                        yOffset,
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
                .height(pointWidth * steps + 50.dp) // Высота увеличена для осей
        ) {
            items(datesByPoints.size) { index ->
                val point = datesByPoints[index]
                val nextPoint = datesByPoints.getOrNull(index + 1)

                Box(modifier = Modifier.height(pointWidth * steps + 50.dp)) {
                    Canvas(
                        modifier = Modifier.size(pointWidth, pointWidth * steps)
                    ) {
                        // Правильное позиционирование точки на графике
                        val temperatureRange = maxTemp - minTemp
                        val yPosition =
                            size.height * (1 - (point.temperature - minTemp) / temperatureRange.toFloat())

                        // Рисуем сетку
                        val gridLineCount = steps
                        val step = size.height / gridLineCount

                        for (i in 0 until gridLineCount) {
                            drawLine(
                                color = Color.Gray,
                                start = Offset(0f, i * step),
                                end = Offset(size.width, i * step),
                                strokeWidth = 1f
                            )
                        }

                        drawLine(
                            color = Color.Gray,
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 1f
                        )

                        // Рисуем точку
                        drawCircle(
                            color = Color.Red,
                            radius = 6f,
                            center = Offset(size.width / 2, yPosition.toFloat())
                        )

                        nextPoint?.let {
                            val nextYPosition =
                                size.height * (1 - (it.temperature - minTemp) / temperatureRange.toFloat())
                            drawLine(
                                color = Color.Blue,
                                start = Offset(size.width / 2, yPosition.toFloat()),
                                end = Offset(
                                    size.width + pointWidth.toPx() / 2,
                                    nextYPosition.toFloat()
                                ),
                                strokeWidth = 4f
                            )
                        }

                        drawContext.canvas.nativeCanvas.apply {
                            save()
                            rotate(90f, size.width / 2, size.height + 70f)
                            drawText(
                                dateFormat.format(point.ts),
                                size.width / 2,
                                size.height + 70f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 24f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                            restore()
                        }
                    }
                }
            }
        }
    }
}

fun Date.toUI(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return dateFormat.format(this)
}


