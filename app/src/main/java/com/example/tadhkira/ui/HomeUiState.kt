package com.example.tadhkira.ui

import com.example.tadhkira.data.adhan.PrayerTime
import com.example.tadhkira.data.adhan.PrayerTimes

sealed class HomeUiState {
    data class SuccessState(
        val startsInLabel: String = "",
        val hourMinLabel: String = "",
        val atTimeLabel: String = "",
        val dayLabel: String = "",
        val dateLabel: String = "",
        val prayerTimes: PrayerTimes? = null,
        val nextPrayerTime: PrayerTime? = null
    ): HomeUiState()

    object LoadingState: HomeUiState()
}