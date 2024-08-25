package com.example.tadhkira.data.adhan

import com.google.gson.annotations.SerializedName

data class TimingsByCityResponse(
    @SerializedName("data")
    val data: DataResponse
)

data class DataResponse(
    @SerializedName("timings")
    val timings: TimingsResponse,
    @SerializedName("date")
    val date: DateResponse
)

data class TimingsResponse(
    @SerializedName("Fajr")
    val fajr: String,
    @SerializedName("Sunrise")
    val sunrise: String,
    @SerializedName("Dhuhr")
    val dhuhr: String,
    @SerializedName("Asr")
    val asr: String,
    @SerializedName("Maghrib")
    val maghrib: String,
    @SerializedName("Isha")
    val isha: String
)

data class DateResponse(
    val gregorian: GregorianResponse
)

data class GregorianResponse(
    @SerializedName("weekday")
    val weekday: WeekdayResponse,
    @SerializedName("day")
    val day: String,
    @SerializedName("month")
    val month: MonthResponse,
    @SerializedName("year")
    val year: String
)

data class WeekdayResponse(
    @SerializedName("en")
    val day: String
)

data class MonthResponse(
    @SerializedName("en")
    val month: String
    )