package com.example.soundboard

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://freesound.org/apiv2/"
    private const val TAG_DEBUG = "RetrofitInstanceDebug"
    private const val TAG_INFO = "RetrofitInstanceInfo"
    private const val TAG_WARN = "RetrofitInstanceWarn"
    private const val TAG_ERROR = "RetrofitInstanceError"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: FreesoundApi by lazy {
        Log.d(TAG_DEBUG, "Creating Retrofit instance")
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            Log.i(TAG_INFO, "Retrofit instance created successfully")
            retrofit.create(FreesoundApi::class.java).also {
                Log.d(TAG_DEBUG, "FreesoundApi created")
            }
        } catch (e: Exception) {
            Log.e(TAG_ERROR, "Error creating Retrofit instance: ${e.message}", e)
            throw e
        }
    }
}