package com.example.soundboard

import android.util.Log
import retrofit2.Call

class FreesoundApiWrapper(private val api: FreesoundApi) {
    companion object {
        private const val TAG_DEBUG = "FreesoundApiDebug"
        private const val TAG_INFO = "FreesoundApiInfo"
        private const val TAG_WARN = "FreesoundApiWarn"
        private const val TAG_ERROR = "FreesoundApiError"
    }

    fun getSoundById(id: Int): Call<Sound> {
        Log.d(TAG_DEBUG, "Calling getSoundById with ID: $id")
        return try {
            val call = api.getSoundById(id)
            Log.d(TAG_DEBUG, "getSoundById call created successfully for ID: $id")
            call
        } catch (e: Exception) {
            Log.e(TAG_ERROR, "Error creating getSoundById call for ID: $id", e)
            throw e
        }
    }
}