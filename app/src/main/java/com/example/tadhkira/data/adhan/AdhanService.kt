package com.example.tadhkira.data.adhan

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface AdhanService {

    @GET("timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String = "Dublin",
        @Query("country") country: String = "US",
        @Query("iso8601") useIso8601: Boolean = true): Response<TimingsByCityResponse>

    @GET("timings")
    suspend fun getPrayerTimings(
        @Query("latitude") latitude: Float = 0f,
        @Query("longitude") longitude: Float = 0f,
        @Query("iso8601") useIso8601: Boolean = true
    ): Response<TimingsByCityResponse>
}