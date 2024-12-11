package com.example.soundboard


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SoundViewModel : ViewModel() {
    private val _selectedSounds = MutableLiveData<List<String>>(List(5) { "" })
    val selectedSounds: LiveData<List<String>> = _selectedSounds

    fun updateSelectedSound(buttonIndex: Int, soundName: String) {
        val currentSounds = _selectedSounds.value?.toMutableList() ?: return
        currentSounds[buttonIndex] = soundName
        _selectedSounds.value = currentSounds
    }
}