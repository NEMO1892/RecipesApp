package com.example.recipesapp.network

import android.util.Log
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = " https://api.edamam.com"

const val APP_ID = "8951fa90"

const val APP_KEY = "e8920682539c859febea0f45d822b02a"

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun getEdamamApi(): EdamamApi {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor { message ->
                    Log.d("OK HTTP", message)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .connectTimeout(10000L, TimeUnit.SECONDS)
            .readTimeout(10000L, TimeUnit.SECONDS)
            .writeTimeout(10000L, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(EdamamApi::class.java)
    }
}