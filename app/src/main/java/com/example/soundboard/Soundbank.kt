package com.example.soundboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundbanks")
data class Soundbank(
    @PrimaryKey val name: String,
    val soundIds: String // Comma-separated list of sound IDs
)