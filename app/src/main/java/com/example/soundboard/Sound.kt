package com.example.soundboard

data class Sound(
    val id: Int,
    val url: String,
    val name: String,
    val previews: Map<String, String>
) {
    init {
        Log.d("Sound", "Created a sound object with ID: $id")
        Log.i("Sound", "ID: $id, URL: $url, Name: $name, Previews: $previews")

        if (url.isBlank()) {
            Log.e("Sound", "URL is missing or empty for sound with ID: $id")
        } else {
            Log.w("Sound", "Make sure the URL $url is accessible for sound with ID: $id")
        }
    }
}