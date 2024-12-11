package com.example.soundboard

import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SoundControlsFragment : Fragment() {

    private val viewModel: SoundViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sound_controls, container, false)

        // Set up sound buttons
        val soundButtons = listOf(
            view.findViewById<Button>(R.id.soundButton1),
            view.findViewById<Button>(R.id.soundButton2),
            view.findViewById<Button>(R.id.soundButton3),
            view.findViewById<Button>(R.id.soundButton4),
            view.findViewById<Button>(R.id.soundButton5)
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
            "Cat Meow", "Dog Bark", "Cow Moo", "Elephant Trumpet", "Lion Roar",
            "Phone Ring", "Doorbell", "Alarm Clock", "Siren", " পাখির কিচিরমিচির",
            "Rain", "Thunder", "Wind", "Waves", "Fire Crackling"
        )

        val soundAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soundNames).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        for ((index, spinner) in soundSpinners.withIndex()) {
            spinner.adapter = soundAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedSound = parent.getItemAtPosition(position).toString()
                    viewModel.updateSelectedSound(index, selectedSound)
                    Log.d("SoundControls", "Button ${index + 1} sound updated to: $selectedSound")
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // Observe selected sounds and update UI
        viewModel.selectedSounds.observe(viewLifecycleOwner) { sounds ->
            for ((index, sound) in sounds.withIndex()) {
                if (sound.isNotEmpty()) {
                    val soundIndex = soundAdapter.getPosition(sound)
                    if (soundIndex != -1) {
                        soundSpinners[index].setSelection(soundIndex)
                    }
                }
            }
        }

        for ((index, button) in soundButtons.withIndex()) {
            button.setOnClickListener {
                // Placeholder action: Log button click with selected sound
                val selectedSound = viewModel.selectedSounds.value?.get(index) ?: ""
                Log.d("SoundControls", "Sound button ${index + 1} clicked. Sound: $selectedSound")
            }
        }

        return view
    }

}