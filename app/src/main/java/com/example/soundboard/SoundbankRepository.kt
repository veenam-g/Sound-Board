package com.example.soundboard

import kotlinx.coroutines.flow.Flow

class SoundbankRepository(private val soundbankDao: SoundbankDao) {

    val allSoundbanks: Flow<List<Soundbank>> = soundbankDao.getAllSoundbanks()

    suspend fun insert(soundbank: Soundbank) {
        soundbankDao.insertSoundbank(soundbank)
    }

    suspend fun delete(soundbank: Soundbank) {
        soundbankDao.deleteSoundbank(soundbank)
    }
}