package com.example.soundboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.coroutines.flow.first
import androidx.appcompat.app.AlertDialog

class SoundbankListFragment : Fragment() {

    private val viewModel: SoundViewModel by activityViewModels()
    private lateinit var soundbankRepository: SoundbankRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundBankSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_soundbank_list, container, false)

        val database = AppDatabase.getDatabase(requireContext())
        soundbankRepository = SoundbankRepository(database.soundbankDao())
        settingsRepository = SettingsRepository(requireContext())

        soundBankSpinner = view.findViewById(R.id.soundBankSpinner)

        // Observe soundbanks and update the Spinner
        lifecycleScope.launch {
            soundbankRepository.allSoundbanks.collect { soundbanks ->
                // Update the spinner
                val soundbankNames = soundbanks.map { it.name }
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, soundbankNames)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                soundBankSpinner.adapter = spinnerAdapter

                // Load last selected soundbank and update UI
                val lastSoundbankName = settingsRepository.lastSoundbank.first()
                if (lastSoundbankName.isNotEmpty() && soundbankNames.contains(lastSoundbankName)) {
                    soundBankSpinner.setSelection(soundbankNames.indexOf(lastSoundbankName))
                    val soundbank = soundbanks.firstOrNull { it.name == lastSoundbankName }
                    soundbank?.let {
                        viewModel.updateSelectedSound(0, it.sound1)
                        viewModel.updateSelectedSound(1, it.sound2)
                        viewModel.updateSelectedSound(2, it.sound3)
                        viewModel.updateSelectedSound(3, it.sound4)
                        viewModel.updateSelectedSound(4, it.sound5)
                        Log.d("SoundbankList", "Loaded last selected soundbank: ${it.name}")
                    }
                }
            }
        }

        soundBankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSoundbankName = parent.getItemAtPosition(position).toString()

                lifecycleScope.launch {
                    val selectedSoundbank = soundbankRepository.allSoundbanks.first().firstOrNull { it.name == selectedSoundbankName }
                    selectedSoundbank?.let {
                        viewModel.updateSelectedSound(0, it.sound1)
                        viewModel.updateSelectedSound(1, it.sound2)
                        viewModel.updateSelectedSound(2, it.sound3)
                        viewModel.updateSelectedSound(3, it.sound4)
                        viewModel.updateSelectedSound(4, it.sound5)
                        Log.d("SoundbankList", "Loaded soundbank: ${it.name}")

                        // Save last selected soundbank to DataStore
                        settingsRepository.saveLastSoundbank(it.name)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        soundBankSpinner.setOnItemLongClickListener { parent, view, position, id ->
            val selectedSoundbankName = parent.getItemAtPosition(position).toString()
            showDeleteConfirmationDialog(selectedSoundbankName)
            true // Consume the long-click event
        }

        return view
    }

    private fun showDeleteConfirmationDialog(soundbankName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Soundbank")
            .setMessage("Are you sure you want to delete $soundbankName?")
            .setPositiveButton("Delete") { dialog, _ ->
                lifecycleScope.launch {
                    val soundbankToDelete = soundbankRepository.allSoundbanks.first().firstOrNull { it.name == soundbankName }
                    soundbankToDelete?.let {
                        soundbankRepository.delete(it)
                        Log.d("SoundbankList", "Deleted soundbank: ${it.name}")
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}