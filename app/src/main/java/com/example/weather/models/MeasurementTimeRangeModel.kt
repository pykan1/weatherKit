package com.example.weather.models

import com.google.gson.annotations.SerializedName
import java.io.Serial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class MeasurementTimeRangeResponse(
    @SerializedName("ts_min")
    val tsMin: String?,
    @SerializedName("ts_max")
    val tsMax: String?
)

data class MeasurementTimeRangeUI(
    val tsMin: Date,
    val tsMax: Date
) {
    companion object {
        val Default = MeasurementTimeRangeUI(
            Date(), Date()
        )
    }
}

fun MeasurementTimeRangeResponse.toUI(): MeasurementTimeRangeUI {
    return MeasurementTimeRangeUI(
        tsMin = tsMin.orEmpty().parseDate(),
        tsMax = tsMax.orEmpty().parseDate()
    )
}

fun Date.formatDateToISOString(): String {
    // Формат для преобразования даты
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return format.format(this)
}

fun String.parseDate(): Date {
    return try {
        // Определяем формат даты, соответствующий переданной строке
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.parse(this).let {
            it ?: Date()
        }

    } catch (e: Exception) {
        Date()
    }
}