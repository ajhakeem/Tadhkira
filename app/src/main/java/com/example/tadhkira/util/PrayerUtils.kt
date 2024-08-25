package com.example.tadhkira.util

import android.content.Context
import com.example.tadhkira.R
import com.example.tadhkira.data.adhan.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PrayerUtils(private val context: Context) {

    private val isoTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    private val ONE_HOUR_MS = 3600000L

    fun createNextDayFajrTime(fajrTime: PrayerTime): PrayerTime {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val isoTime = sdf.format(calendar.time) + fajrTime.isoTime.substring(10)

        return PrayerTime(
            prayerName = PrayerName.Fajr,
            isoTime = isoTime,
            asDate = Date.from(Instant.from(isoTimeFormatter.parse(isoTime)))
        )
    }

    fun createStartsInString(nextPrayer: String): String =
        String.format(context.getString(R.string.starts_in_template), nextPrayer)

    fun createHourLabel(prayerTime: PrayerTime): String {
        var hourLabelString = ""
        val minLabelString: String

        val prayerTimeAsDate = Date.from(Instant.from(isoTimeFormatter.parse(prayerTime.isoTime)))
        val timeToPrayerEpochSeconds = abs(prayerTimeAsDate.time - System.currentTimeMillis())
        val hoursToPrayer = TimeUnit.MILLISECONDS.toHours(timeToPrayerEpochSeconds)
        val timeModulusHours = timeToPrayerEpochSeconds % ONE_HOUR_MS
        val minutesInHourToPrayer = TimeUnit.MILLISECONDS.toMinutes(timeModulusHours)

        if (hoursToPrayer == 1L) {
            hourLabelString =
                String.format(context.getString(R.string.hour_template, hoursToPrayer.toString()))
        } else if (hoursToPrayer != 0L) {
            hourLabelString =
                String.format(context.getString(R.string.hours_template, hoursToPrayer.toString()))
        }

        minLabelString = if (minutesInHourToPrayer == 1L) {
            String.format(context.getString(R.string.min_template, minutesInHourToPrayer.toString()))
        } else {
            String.format(context.getString(R.string.mins_template, minutesInHourToPrayer.toString()))
        }

        return "$hourLabelString $minLabelString"
    }

    fun toPrayerTimes(listOfPrayerTimes: List<PrayerTime>): PrayerTimes {
        return PrayerTimes(
            fajrTime = to12HrReadableTime(listOfPrayerTimes[0].asDate),
            dhuhrTime = to12HrReadableTime(listOfPrayerTimes[1].asDate),
            asrTime = to12HrReadableTime(listOfPrayerTimes[2].asDate),
            maghribTime = to12HrReadableTime(listOfPrayerTimes[3].asDate),
            ishaTime = to12HrReadableTime(listOfPrayerTimes[4].asDate)
        )
    }

    fun findNextPrayer(listOfPrayerTimes: List<PrayerTime>): PrayerTime {
        var nextPrayerTime = PrayerTime()

        for (i in 0..3) {
            val prayerTime = listOfPrayerTimes[i]
            val followingPrayerTime = listOfPrayerTimes[i+1]
            val currentTimeDate = Date()

            if (currentTimeDate.before(prayerTime.asDate)) {
                nextPrayerTime = prayerTime
                break
            } else if (currentTimeDate.after(prayerTime.asDate)
                && currentTimeDate.before(followingPrayerTime.asDate)) {
                nextPrayerTime = followingPrayerTime
                break
            } else {
                continue
            }
        }

        return nextPrayerTime
    }

    fun createAtTimeString(time: String): String {
        return String.format(context.getString(R.string.at_time_template, time))
    }

    fun to12HrReadableTime(date: Date): String {
        val sdf12Hr = SimpleDateFormat("h:mm a", Locale.US)
        return sdf12Hr.format(date)
    }

    fun createGregorianDate(gregorianResponse: GregorianResponse): GregorianDate {
        return GregorianDate(
            dayOfWeek = gregorianResponse.weekday.day,
            displayDate = String.format(
                context.getString(
                    R.string.display_date_template,
                    gregorianResponse.month.month,
                    gregorianResponse.day,
                    gregorianResponse.year)
            )
        )
    }
}