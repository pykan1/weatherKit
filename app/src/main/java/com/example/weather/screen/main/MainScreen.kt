package com.example.weather.screen.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(city: Int, cityName: String) {
    val viewModel: MainViewModel = viewModel()
    val state by viewModel.stateFlow.collectAsState()
    var sliderValue by remember { mutableFloatStateOf(100f) }
    var openStartDialog by remember {
        mutableStateOf(false)
    }
    var selectedScheme by rememberSaveable() { mutableStateOf(Pair(Color.Blue, Color.Red)) }
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
            .animateContentSize()
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
                var sliderEndValue by remember { mutableFloatStateOf((state.daily.size - 1).toFloat()) }
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
                            valueRange = 1f..state.daily.size.toFloat(),
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
                        startDatePickerState.selectedDateMillis = it.ts.time
                        viewModel.changeDate(startDate = it.ts)
                    }
                }

                LaunchedEffect(sliderEndState.value) {
                    sliderEndValue = sliderEndState.value
                    state.daily.getOrNull(sliderEndState.value.roundToInt())?.let {
                        endDatePickerState.selectedDateMillis = it.ts.time
                        viewModel.changeDate(endDate = it.ts)
                    }
                }
                val selectedStep = sliderValue.roundToInt()
                Spacer(modifier = Modifier.size(20.dp))

                if (!state.loading) {
                    TemperatureGraph(datesByPoints = state.datesByPoints, selectedScheme) {
                        selectedScheme = it
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

                Spacer(modifier = Modifier.size(40.dp))

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
                                    viewModel.changeDate(
                                        startDate = Date(
                                            startDatePickerState.selectedDateMillis.orEmpty()
                                                .toStartOfDay()
                                        )
                                    )
                                    val date = Date(
                                        startDatePickerState.selectedDateMillis.orEmpty()
                                            .toStartOfDay()
                                    )
                                    println("date - $date")
                                    println(
                                        "state.daily.firstOrNull { it.ts.time == startDatePickerState.selectedDateMillis } - ${
                                            state.daily.firstOrNull {
                                                it.ts.time == startDatePickerState.selectedDateMillis.orEmpty()
                                                    .toStartOfDay()
                                            }
                                        }"
                                    )
                                    state.daily.firstOrNull {
                                        it.ts.time == startDatePickerState.selectedDateMillis.orEmpty()
                                            .toStartOfDay()
                                    }
                                        ?.let { day ->
                                            sliderStartState.value =
                                                state.daily.indexOf(day).toFloat()
                                        }
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
                                    viewModel.changeDate(
                                        endDate = Date(
                                            endDatePickerState.selectedDateMillis.orEmpty()
                                                .toStartOfDay()
                                        )
                                    )
                                    state.daily.firstOrNull {
                                        it.ts.time == endDatePickerState.selectedDateMillis.orEmpty()
                                            .toStartOfDay()
                                    }
                                        ?.let { day ->
                                            sliderEndState.value =
                                                state.daily.indexOf(day).toFloat()
                                        }
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
                Spacer(modifier = Modifier.size(40.dp))
                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(80.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureGraph(
    datesByPoints: List<DailyUI>,
    selectedScheme: Pair<Color, Color>,
    changeSelectedScheme: (Pair<Color, Color>) -> Unit
) {
    val minTemp = datesByPoints.minOfOrNull { it.temperature }?.let {
        it - (it % 5) - 5
    } ?: 0.0

    val maxTemp = datesByPoints.maxOfOrNull { it.temperature }?.let {
        it + (5 - it % 5)
    } ?: 0.0

    val startDate = datesByPoints.first().ts
    val endDate = datesByPoints.last().ts
    val yearsDiff = endDate.year - startDate.year

    // Определяем visibleLabelsCount в зависимости от временного диапазона
    val (visibleLabelsCount, interval) = when {
        yearsDiff >= 8 -> yearsDiff to Calendar.YEAR
        yearsDiff >= 5 -> yearsDiff * 2 to Calendar.MONTH // Полугодия
        yearsDiff >= 1.5 -> yearsDiff * 4 to Calendar.MONTH // Кварталы
        else -> yearsDiff * 12 to Calendar.MONTH // Месяцы
    }

    val dateFormat = when (interval) {
        Calendar.YEAR -> SimpleDateFormat("yyyy", Locale.getDefault())
        Calendar.MONTH -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
        else -> SimpleDateFormat("yyyy", Locale.getDefault())
    }

    // Остальная часть функции остается прежней
    val steps = ceil(((maxTemp - minTemp) / 5)).toInt()
    val canvasHeight = 300.dp
    var isOpen by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Color scheme")
            IconButton(modifier = Modifier.size(40.dp), onClick = {
                isOpen = !isOpen
            }) {
                Icon(active = isOpen, activeContent = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }, inactiveContent = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                })
            }
        }
        Row(modifier = Modifier.animateContentSize()) {
            val colors = listOf<Pair<Color, Color>>(
                Pair(Color.Blue, Color.Red),
                Pair(Color(0xFFebe4f4), Color(0xFF4c0a28)),
                Pair(Color(0xFFebe4f4), Color(0xFF520565)),
                Pair(Color(0xFFe2efef), Color(0xFF680661)),
                Pair(Color(0xFFeaf5e6), Color(0xFF0a4264)),
                Pair(Color(0xFFe2e0eb), Color(0xFF064258)),
                Pair(Color(0xFFe9f7b9), Color(0xFF12276c)),
                Pair(Color(0xFFe7e6ec), Color(0xFF014e3b)),
                Pair(Color(0xFFddefeb), Color(0xFF0c4624)),
                Pair(Color(0xFFf3fcc5), Color(0xFF06492f)),
            )
            val splitList = splitArray(colors)
            if (isOpen) {
                ColorOption(
                    modifier = Modifier.weight(1f),
                    selectedScheme,
                    colors = splitList.first,
                    onColorChange = changeSelectedScheme
                )
                Spacer(Modifier.width(16.dp))
                ColorOption(
                    modifier = Modifier.weight(1f),
                    selectedScheme,
                    colors = splitList.second,
                    onColorChange = changeSelectedScheme
                )
            }
        }
        Row {
            Spacer(Modifier.size(40.dp))
            Canvas(
                modifier = Modifier.weight(1f)
            ) {
                val xStep = size.width / datesByPoints.size
                val temperatureRange = maxTemp - minTemp

                datesByPoints.forEachIndexed { index, point ->
                    val tempRatio = (point.temperature - minTemp) / temperatureRange.toFloat()
                    val color =
                        lerp(selectedScheme.first, selectedScheme.second, tempRatio.toFloat())

                    drawRect(
                        color = color,
                        topLeft = Offset(index * xStep, 0f),
                        size = Size(xStep, 40f)
                    )
                }
            }
        }
        Row {
            Canvas(
                modifier = Modifier
                    .height(canvasHeight)
                    .width(40.dp)
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

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(canvasHeight)
            ) {
                val temperatureRange = maxTemp - minTemp
                val xStep = size.width / datesByPoints.size
                val path = Path()

                val startY =
                    (size.height * (1 - (datesByPoints[0].temperature - minTemp) / temperatureRange)).toFloat()
                path.moveTo(0f, startY)

                for (index in 1 until datesByPoints.size) {
                    val currentPoint = datesByPoints[index]
                    val prevPoint = datesByPoints[index - 1]
                    val prevX = (index - 1) * xStep
                    val currentX = index * xStep

                    val prevY =
                        size.height * (1 - (prevPoint.temperature - minTemp) / temperatureRange.toFloat())
                    val currentY =
                        size.height * (1 - (currentPoint.temperature - minTemp) / temperatureRange.toFloat())

                    val controlX = (prevX + currentX) / 2
                    val controlY = (prevY + currentY) / 2
                    path.quadraticBezierTo(
                        controlX,
                        controlY.toFloat(),
                        currentX,
                        currentY.toFloat()
                    )
                }

                val labelStep = datesByPoints.size / visibleLabelsCount
                for (i in 0 until visibleLabelsCount) {
                    val index = i * labelStep
                    if (index < datesByPoints.size) {
                        val point = datesByPoints[index]
                        val xPosition = index * xStep
                        drawContext.canvas.nativeCanvas.apply {
                            val text = dateFormat.format(point.ts)
                            save()
                            rotate(90f, xPosition, size.height + (text.length*7.5).toFloat())
                            drawText(
                                text,
                                xPosition,
                                size.height + 30f,
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

                val yStep = size.height / steps
                for (i in 0..steps) {
                    val yOffset = size.height - i * yStep
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, yOffset),
                        end = Offset(size.width, yOffset),
                        strokeWidth = 1f
                    )
                }

                for (i in 0 until visibleLabelsCount) {
                    val index = i * labelStep
                    if (index < datesByPoints.size) {
                        val xPosition = index * xStep
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(xPosition, 0f),
                            end = Offset(xPosition, size.height),
                            strokeWidth = 1f
                        )
                    }
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}


// Helper Composable for color selection
@Composable
fun ColorOption(
    modifier: Modifier = Modifier,
    selectedColor: Pair<Color, Color>,
    colors: List<Pair<Color, Color>>,
    onColorChange: (Pair<Color, Color>) -> Unit
) {
    Column(modifier) {
        colors.forEach { color ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (selectedColor == color),
                        onClick = { onColorChange(color) }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedColor == color),
                    onClick = { onColorChange(color) }
                )
                Spacer(modifier = Modifier.size(8.dp))
                Box(
                    modifier = Modifier
                        .size(height = 24.dp, width = 80.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    color.first,
                                    color.second
                                )
                            )
                        )
                )
            }
        }
    }
}


fun Date.toUI(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return dateFormat.format(this)
}

fun Long.toStartOfDay(): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toStartOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

fun <T> splitArray(array: List<T>): Pair<List<T>, List<T>> {
    val middleIndex = array.size / 2
    val firstPart = array.take(middleIndex)
    val secondPart = array.drop(middleIndex)
    return Pair(firstPart, secondPart)
}

