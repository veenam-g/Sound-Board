package com.example.soundboard

import android.util.Log

data class Sound(
    val id: Int, // The sound ID
    val url: String, // The URI of the sound
    val name: String, // The name user gave to the sound.
    val previews: Map<String, String>, // Dictionary containing the URIs for mp3 and ogg previews
    val images: Map<String, String> // Map of spectrogram waveform images
) {
    companion object {
        private const val TAG_DEBUG = "SoundDebug" // Tag for debug logs
        private const val TAG_INFO = "SoundInfo" // Tag for info logs
        private const val TAG_WARN = "SoundWarn" // Tag for warning logs
        private const val TAG_ERROR = "SoundError" // Tag for error logs
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
