package com.example.tadhkira.data.adhan

data class Timings(
    val listOfPrayerTimes: MutableList<PrayerTime> = mutableListOf(),
    val gregorianResponse: GregorianResponse? = null
)