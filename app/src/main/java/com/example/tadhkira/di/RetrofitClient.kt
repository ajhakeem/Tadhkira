package com.example.tadhkira.di

import com.example.tadhkira.data.adhan.AdhanService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.aladhan.com/v1/")
            .addConverterFactory(gsonConverterFactory)


    @Singleton
    @Provides
    fun provideAdhanTimingsService(retrofitBuilder: Retrofit.Builder): AdhanService =
        retrofitBuilder.build().create(AdhanService::class.java)
}