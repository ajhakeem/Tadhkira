package com.example.tadhkira.ui

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.tadhkira.R
import com.example.tadhkira.data.adhan.*
import com.example.tadhkira.util.PrayerUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adhanRepository: AdhanRepository): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val uiState = _uiState.asStateFlow()
    private val sharedPreferences =
        context.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)
    private val prayerUtils = PrayerUtils(context = context)

    suspend fun getTimingsByCity(latitude: Float, longitude: Float) {
        withContext(Dispatchers.Default) {
            _uiState.emit(HomeUiState.LoadingState)

            val response = adhanRepository.getTimings(
                latitude = latitude,
                longitude = longitude
            )

            // If current time is after isha, then get next days fajr time
            if (Date().after(response.listOfPrayerTimes[4].asDate)) {
                val nextDayFajrTime = prayerUtils.createNextDayFajrTime(response.listOfPrayerTimes[0])
                response.listOfPrayerTimes[0] = nextDayFajrTime
            }

            _uiState.value = toUiState(
                listOfPrayerTimes = response.listOfPrayerTimes,
                gregorianResponse = response.gregorianResponse
            )
        }
    }

    private fun toUiState(listOfPrayerTimes: List<PrayerTime>, gregorianResponse: GregorianResponse?): HomeUiState {
        val prayerTimes = prayerUtils.toPrayerTimes(listOfPrayerTimes = listOfPrayerTimes)
        val nextPrayerTime = prayerUtils.findNextPrayer(listOfPrayerTimes = listOfPrayerTimes)
        val gregorianDate = gregorianResponse?.let { prayerUtils.createGregorianDate(gregorianResponse = it) }
        saveToSharedPreferences(prayerTimes = prayerTimes)

        return HomeUiState.SuccessState(
            startsInLabel = prayerUtils.createStartsInString(nextPrayerTime.prayerName.name),
            hourMinLabel = prayerUtils.createHourLabel(prayerTime = nextPrayerTime),
            prayerTimes = prayerTimes,
            dayLabel = gregorianDate?.dayOfWeek.toString(),
            dateLabel = gregorianDate?.displayDate.toString(),
            atTimeLabel = prayerUtils.createAtTimeString(prayerUtils.to12HrReadableTime(nextPrayerTime.asDate)),
            nextPrayerTime = nextPrayerTime
        )
    }

    private fun saveToSharedPreferences(prayerTimes: PrayerTimes) {
        sharedPreferences.edit {
            putString(FAJR_KEY, prayerTimes.fajrTime)
            putString(DHUHR_KEY, prayerTimes.dhuhrTime)
            putString(ASR_KEY, prayerTimes.asrTime)
            putString(MAGHRIB_KEY, prayerTimes.maghribTime)
            putString(ISHA_KEY, prayerTimes.ishaTime)
            apply()
        }
    }

    companion object {
        const val FAJR_KEY = "Fajr"
        const val DHUHR_KEY = "Dhuhr"
        const val ASR_KEY = "Asr"
        const val MAGHRIB_KEY = "Maghrib"
        const val ISHA_KEY = "Isha"
    }
}