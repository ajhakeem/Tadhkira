package com.example.tadhkira

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.example.tadhkira.data.adhan.AdhanService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class TadhkiraWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
private val coroutineScope = CoroutineScope(Dispatchers.Default)

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
//    val adhanService = buildAdhanService()
//    val adhanRepo = AdhanRepository(adhanService)
//    val sharedPreferences =
//        context.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE)
//    val latitude = getLastLatitude(sharedPreferences = sharedPreferences)
//    val longitude = getLastLongitude(sharedPreferences = sharedPreferences)
//    val prayerUtils = PrayerUtils(context = context)
//    var prayerInString = "Waiting for"
//    var hourMinString = "prayer times update"
//
//    if (latitude != 0f && longitude != 0f) {
//        coroutineScope.launch {
//            val timings = adhanRepo.getTimings(latitude = latitude, longitude = longitude)
//            val nextPrayerTime = prayerUtils.findNextPrayer(listOfPrayerTimes = timings.listOfPrayerTimes)
//            prayerInString = prayerUtils.createStartsInString(nextPrayerTime.prayerName.name)
//            hourMinString = prayerUtils.createHourLabel(prayerTime = nextPrayerTime)
//        }
//    }

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.tadhkira_widget)
//    views.setTextViewText(R.id.prayer_in_tv, prayerInString)
//    views.setTextViewText(R.id.time_to_next_prayer_tv, hourMinString)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun buildAdhanService(): AdhanService {
    val okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.aladhan.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())

    return retrofit.build().create(AdhanService::class.java)
}

private fun getLastLatitude(sharedPreferences: SharedPreferences): Float {
    return sharedPreferences.getFloat("latitude", 0f)
}

private fun getLastLongitude(sharedPreferences: SharedPreferences): Float {
    return sharedPreferences.getFloat("longitude", 0f)
}