package com.example.soundboard

import android.util.Log

data class Sound(
    val id: Int,
    val url: String,
    val name: String,
    val previews: Map<String, String>
) {
    companion object {
        private const val TAG_DEBUG = "SoundDebug"
        private const val TAG_INFO = "SoundInfo"
        private const val TAG_WARN = "SoundWarn"
        private const val TAG_ERROR = "SoundError"
    }

    init {
        Log.d(TAG_DEBUG, "Created a sound object with ID: $id")
        Log.i(TAG_INFO, "ID: $id, URL: $url, Name: $name, Previews: $previews")

        if (url.isBlank()) {
            Log.e(TAG_ERROR, "URL is missing or empty for sound with ID: $id")
        } else {
            Log.w(TAG_WARN, "Make sure the URL $url is accessible for sound with ID: $id")
        }
    }
}
