package com.example.weather.models

import java.util.Calendar
import java.util.Date

fun generateEqualDates(
    allDays: List<DailyUI>,
    dateFrom: Date,
    dateTo: Date,
    points: Int
): List<DailyUI> {

    val dates = mutableListOf<DailyUI>()

    // Создаем экземпляры Calendar для обеих дат, сбрасываем время
    val calendarFrom = Calendar.getInstance().apply {
        time = dateFrom
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val calendarTo = Calendar.getInstance().apply {
        time = dateTo
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // Вычисляем разницу в днях между двумя датами
    val daysDifference =
        ((calendarTo.timeInMillis - calendarFrom.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

    // Вычисляем шаг между датами в днях
    val intervalDays = daysDifference.toDouble() / (points - 1)

    // Генерируем равномерно распределённые даты
    for (i in 0 until points) {
        val newDate = calendarFrom.clone() as Calendar
        newDate.add(Calendar.DAY_OF_YEAR, (i * intervalDays).toInt())
        allDays.find { it.ts == newDate.time }?.let {
            dates.add(it)
        }
    }

    return dates
}