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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoundControlsFragment : Fragment() {
    private val viewModel: SoundViewModel by activityViewModels()
    private val soundPlayers: MutableMap<Int, MediaPlayer> = mutableMapOf()

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

    var currentSoundBank: MutableList<Int> = soundIds.subList(0, 5).toMutableList() // First 5 sounds initially

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

        val soundAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soundNames).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        for ((index, spinner) in soundSpinners.withIndex()) {
            spinner.adapter = soundAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedSound = parent.getItemAtPosition(position).toString()

                    // Update the current sound bank with the new sound ID only if it's different
                    val newSoundId = soundIds[soundNames.indexOf(selectedSound)]
                    if (newSoundId != currentSoundBank[index]) {
                        val updatedSoundBank = currentSoundBank.toMutableList()
                        updatedSoundBank[index] = newSoundId
                        currentSoundBank = updatedSoundBank
                        Log.d("SoundControls", "Updated currentSoundBank: $currentSoundBank")

                        // Fetch the new sound
                        fetchSoundsByIds(listOf(newSoundId))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // Set up button click listeners with MediaPlayer instances
        for ((index, button) in soundButtons.withIndex()) {
            button.setOnClickListener {
                val soundId = currentSoundBank[index]
                if (soundPlayers.containsKey(soundId)) {
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

        // Fetch sounds for the current sound bank
        fetchSoundsByIds(currentSoundBank)

        return view
    }

    private fun fetchSoundsByIds(ids: List<Int>) {
        ids.forEach { id ->
            if (!soundPlayers.containsKey(id)) {
                Log.d("API", "Fetching sound with ID: $id")
                val call = RetrofitInstance.api.getSoundById(id)
                call.enqueue(object : Callback<Sound> {
                    override fun onResponse(call: Call<Sound>, response: Response<Sound>) {
                        if (response.isSuccessful) {
                            val sound = response.body()
                            sound?.let {
                                Log.d(
                                    "API",
                                    "Fetched sound: ID: ${it.id}, Name: ${it.name}, URL: ${it.url}"
                                )
                                val previewUrl = it.previews["preview-hq-mp3"]
                                    ?: it.previews["preview-lq-mp3"]
                                Log.d("API", "Using preview URL: $previewUrl")

                                soundPlayers[id] = MediaPlayer().apply {
                                    setDataSource(previewUrl as String)
                                    setOnPreparedListener {
                                        Log.d("MediaPlayer", "Sound $id is prepared")
                                    }
                                    setOnErrorListener { mp, what, extra ->
                                        Log.e(
                                            "MediaPlayer",
                                            "Error on sound $id: what=$what, extra=$extra"
                                        )
                                        false
                                    }
                                    prepareAsync()
                                }
                            } ?: Log.d("API", "No sound found for ID: $id")
                        } else {
                            Log.e(
                                "API",
                                "Failed to fetch sound for ID: $id, Response code: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<Sound>, t: Throwable) {
                        Log.e("API", "Error fetching sound for ID: $id, Message: ${t.message}")
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayers.forEach { (_, player) ->
            player.release()
        }
        soundPlayers.clear()
    }

    fun loadSoundBank(soundbank: Soundbank) {
        val soundIds = soundbank.soundIds.split(",").mapNotNull { it.toIntOrNull() }
        if (soundIds.size == 5) {
            currentSoundBank = soundIds.toMutableList()
            fetchSoundsByIds(currentSoundBank)

            // Update the Spinners with the correct sound names
            val soundNames = arrayOf(
                "Dog Bark", "Cat Meow", "Cow Moo", "Elephant Trumpet", "Lion Growl",
                "Phone Ring", "Doorbell", "Alarm Clock", "Siren", "Rain",
                "Thunder", "Wind", "Waves", "Fire Crackling", "Car Horn"
            )
            val soundSpinners = listOf(
                view?.findViewById<Spinner>(R.id.soundSpinner1),
                view?.findViewById<Spinner>(R.id.soundSpinner2),
                view?.findViewById<Spinner>(R.id.soundSpinner3),
                view?.findViewById<Spinner>(R.id.soundSpinner4),
                view?.findViewById<Spinner>(R.id.soundSpinner5)
            )

            for (i in soundIds.indices) {
                val soundNameIndex = this.soundIds.indexOf(soundIds[i])
                if (soundNameIndex != -1) {
                    val soundName = soundNames[soundNameIndex]
                    updateSpinnerWithSoundName(soundSpinners[i]!!, soundName)
                }
            }
        }
    }

    // Add this function to update the sound names in the Spinners
    private fun updateSpinnerWithSoundName(spinner: Spinner, soundName: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String>
        val position = adapter?.getPosition(soundName) ?: -1
        if (position != -1) {
            spinner.setSelection(position)
        }
    }
}