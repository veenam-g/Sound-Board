package com.example.soundboard

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class SoundControlsFragment : Fragment() {
    private val viewModel: SoundViewModel by activityViewModels()

    // Updated sound IDs
    private var soundIds = listOf(
        418107, // Dog Bark
        110011, // Cat Meow
        700380, // Cow Moo
        527845, // Elephant Trumpet
        467762, // Lion Growl
        24929,  // Phone ring
        275072, // Doorbell
        219244, // Alarm clock
        52906,  // Siren
        243629, // Rain
        446753, // Thunder
        34338,  // Wind
        14777,  // Waves
        649208,  // Fire Crackling
        182472 // Car Horn
    )

    lateinit var currentSoundBank: MutableList<Int>
    private lateinit var soundAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sound_controls, container, false)

        // Set up sound buttons
        val soundButtons = listOf(
            view.findViewById<Button>(R.id.Sound1_Button),
            view.findViewById<Button>(R.id.Sound2_Button),
            view.findViewById<Button>(R.id.Sound3_Button),
            view.findViewById<Button>(R.id.Sound4_Button),
            view.findViewById<Button>(R.id.Sound5_Button)
        )

        // Set up sound spinners
        val soundSpinners = listOf(
            view.findViewById<Spinner>(R.id.soundSpinner1),
            view.findViewById<Spinner>(R.id.soundSpinner2),
            view.findViewById<Spinner>(R.id.soundSpinner3),
            view.findViewById<Spinner>(R.id.soundSpinner4),
            view.findViewById<Spinner>(R.id.soundSpinner5)
        )

        val soundNames = arrayOf(
            "Dog Bark", "Cat Meow", "Cow Moo", "Elephant Trumpet", "Lion Growl",
            "Phone Ring", "Doorbell", "Alarm Clock", "Siren", "Rain",
            "Thunder", "Wind", "Waves", "Fire Crackling", "Car Horn"
        )

        soundAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soundNames).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        // Initialize currentSoundBank from ViewModel
        currentSoundBank = viewModel.currentSoundBank.value?.toMutableList() ?: soundIds.subList(0, 5).toMutableList()

        for ((index, spinner) in soundSpinners.withIndex()) {
            spinner.adapter = soundAdapter

            // Set initial selection for Spinners
            val soundNameIndex = soundIds.indexOf(currentSoundBank[index])
            if (soundNameIndex != -1) {
                spinner.setSelection(soundNameIndex, false)
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedSoundId = soundIds[position]

                    // Update ViewModel only if the selected sound is different
                    if (selectedSoundId != currentSoundBank[index]) {
                        currentSoundBank[index] = selectedSoundId
                        viewModel.updateSoundId(index, selectedSoundId)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // Set up button click listeners with MediaPlayer instances
        for ((index, button) in soundButtons.withIndex()) {
            button.setOnClickListener {
                val soundId = currentSoundBank.getOrNull(index)
                val soundPlayers = viewModel.soundPlayers.value ?: emptyMap()

                if (soundId != null && soundPlayers.containsKey(soundId)) {
                    val player = soundPlayers[soundId]
                    if (player?.isPlaying == true) {
                        player.pause()
                        player.seekTo(0) // Reset to beginning
                        Log.d("SoundControls", "Stopped sound: $soundId")
                    } else {
                        player?.start()
                        Log.d("SoundControls", "Playing sound: $soundId")
                    }
                } else {
                    Log.e("SoundControls", "Sound not ready or already playing: $soundId")
                }
            }
        }

        // Observer for currentSoundBank
        viewModel.currentSoundBank.observe(viewLifecycleOwner, Observer { soundBank ->
            if (currentSoundBank != soundBank) {
                currentSoundBank = soundBank.toMutableList()

                // Update Spinners without triggering onItemSelected
                for ((index, spinner) in soundSpinners.withIndex()) {
                    val soundNameIndex = soundIds.indexOf(currentSoundBank[index])
                    if (soundNameIndex != -1) {
                        if (spinner.selectedItemPosition != soundNameIndex) {
                            spinner.setSelection(soundNameIndex, false)
                        }
                    }
                }
            }
        })

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun loadSoundBank(soundbank: Soundbank) {
        val soundIds = soundbank.soundIds.split(",").mapNotNull { it.toIntOrNull() }
        if (soundIds.size == 5) {
            // Update the ViewModel with the new soundbank
            viewModel.updateCurrentSoundBank(soundIds)
        }
    }
}