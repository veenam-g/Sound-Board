// File: app/src/main/java/com/example/soundboard/RetrofitInstance.kt
package com.example.soundboard

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://freesound.org/apiv2/"

    val api: FreesoundApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FreesoundApi::class.java)
    }
}