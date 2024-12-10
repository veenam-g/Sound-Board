// File: app/src/main/java/com/example/soundboard/Sound.kt
package com.example.soundboard

data class Sound(
    val id: Int,
    val url: String,
    val name: String,
    val previews: Map<String, String>
)