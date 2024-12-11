package com.example.soundboard

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundbankDao {
    @Query("SELECT * FROM soundbanks")
    fun getAllSoundbanks(): Flow<List<Soundbank>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoundbank(soundbank: Soundbank)

    @Delete
    suspend fun deleteSoundbank(soundbank: Soundbank)
}