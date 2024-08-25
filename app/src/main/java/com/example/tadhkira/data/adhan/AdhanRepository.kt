package com.example.tadhkira.data.adhan

import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class AdhanRepository @Inject constructor(private val service: AdhanService) {
    private val isoTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    suspend fun getTimings(latitude: Float, longitude: Float): Timings {
        val prayerTimesResponse =
            service.getPrayerTimings(latitude = latitude, longitude = longitude)
        return mapResponseToTimingsModel(response = prayerTimesResponse)
    }

    private fun mapResponseToTimingsModel(response: Response<TimingsByCityResponse>): Timings {
        val listOfPrayerTimes = mutableListOf<PrayerTime>()
        val data = response.body()?.data
        val timings = data?.timings

        val fajrIsoTime = timings?.fajr.toString()
        val dhuhrIsoTime = timings?.dhuhr.toString()
        val asrIsoTime = timings?.asr.toString()
        val maghribIsoTime = timings?.maghrib.toString()
        val ishaIsoTime = timings?.isha.toString()

        listOfPrayerTimes.apply {
            add(PrayerTime(
                prayerName = PrayerName.Fajr,
                isoTime = fajrIsoTime,
                asDate = Date.from(Instant.from(isoTimeFormatter.parse(fajrIsoTime))))
            )
            add(PrayerTime(
                prayerName = PrayerName.Dhuhr,
                isoTime = dhuhrIsoTime,
                asDate = Date.from(Instant.from(isoTimeFormatter.parse(dhuhrIsoTime))))
            )
            add(
                PrayerTime(
                    prayerName = PrayerName.Asr,
                    isoTime = asrIsoTime,
                    asDate = Date.from(Instant.from(isoTimeFormatter.parse(asrIsoTime)))
                )
            )
            add(
                PrayerTime(
                    prayerName = PrayerName.Maghrib,
                    isoTime = maghribIsoTime,
                    asDate = Date.from(Instant.from(isoTimeFormatter.parse(maghribIsoTime)))
                )
            )
            add(
                PrayerTime(
                    prayerName = PrayerName.Isha,
                    isoTime = ishaIsoTime,
                    asDate = Date.from(Instant.from(isoTimeFormatter.parse(ishaIsoTime)))
                )
            )
        }

        return Timings(
            listOfPrayerTimes = listOfPrayerTimes,
            gregorianResponse = data?.date?.gregorian
        )
    }
}