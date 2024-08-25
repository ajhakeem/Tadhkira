package com.example.tadhkira.data.adhan

import java.util.Date

data class PrayerTime(
    val prayerName: PrayerName = PrayerName.Fajr,
    val isoTime: String = "",
    val asDate: Date = Date()
)

enum class PrayerName {
    Fajr,
    Dhuhr,
    Asr,
    Maghrib,
    Isha
}