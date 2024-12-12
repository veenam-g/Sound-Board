package com.example.soundboard

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoundViewModel : ViewModel() {

    private val _currentSoundBank = MutableLiveData<List<Int>>()
    val currentSoundBank: LiveData<List<Int>> = _currentSoundBank

    private val _soundPlayers = MutableLiveData<Map<Int, MediaPlayer>>(mutableMapOf())
    val soundPlayers: LiveData<Map<Int, MediaPlayer>> = _soundPlayers

    fun updateCurrentSoundBank(newSoundBank: List<Int>) {
        _currentSoundBank.value = newSoundBank
        fetchSoundsByIds(newSoundBank)
    }

    fun updateSoundId(index: Int, soundId: Int) {
        val updatedList = _currentSoundBank.value.orEmpty().toMutableList()
        if (index in 0 until updatedList.size) {
            updatedList[index] = soundId
            _currentSoundBank.value = updatedList
            fetchSoundsByIds(listOf(soundId))
        }
    }

    fun fetchSoundsByIds(ids: List<Int>) {
        ids.forEach { id ->
            // If the sound is already loaded, no need to fetch it again
            if (_soundPlayers.value?.containsKey(id) == true) {
                return@forEach
            }

            val call = RetrofitInstance.api.getSoundById(id)
            call.enqueue(object : Callback<Sound> {
                override fun onResponse(call: Call<Sound>, response: Response<Sound>) {
                    if (response.isSuccessful()) {
                        val sound = response.body()
                        sound?.let {
                            val previewUrl = it.previews["preview-hq-mp3"] ?: it.previews["preview-lq-mp3"]
                            val mediaPlayer = MediaPlayer().apply {
                                setDataSource(previewUrl)
                                setOnPreparedListener {
                                    // Update LiveData after MediaPlayer is prepared
                                    val updatedPlayers = _soundPlayers.value.orEmpty().toMutableMap()
                                    updatedPlayers[id] = this
                                    _soundPlayers.value = updatedPlayers
                                }
                                setOnErrorListener { _, what, extra ->
                                    Log.e("MediaPlayer", "Error on sound $id: what=$what, extra=$extra")
                                    false
                                }
                                prepareAsync()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Sound>, t: Throwable) {
                    Log.e("API", "Error fetching sound for ID: $id, Message: ${t.message}")
                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        _soundPlayers.value?.forEach { (_, player) ->
            player.release()
        }
        _soundPlayers.value = emptyMap()
    }
}