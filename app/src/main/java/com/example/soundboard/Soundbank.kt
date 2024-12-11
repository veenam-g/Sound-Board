package com.example.soundboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundbanks")
data class Soundbank(
    @PrimaryKey val name: String,
    val sound1: String,
    val sound2: String,
    val sound3: String,
    val sound4: String,
    val sound5: String
)