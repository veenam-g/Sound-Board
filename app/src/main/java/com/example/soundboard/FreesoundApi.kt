package com.example.soundboard

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface FreesoundApi {
    @Headers("Authorization: Token JqN1s6a8zzghshW402DRj6acgJ6BLZQLJA5y4oJK")
    @GET("sounds/{id}/")
    fun getSoundById(@Path("id") id: Int): Call<Sound>
}