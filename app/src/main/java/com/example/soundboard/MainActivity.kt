package com.example.soundboard

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class MainActivity : AppCompatActivity() {

    val soundControlsFragment = SoundControlsFragment()
    private val soundbankListFragment = SoundbankListFragment()
    private val viewModel: SoundViewModel by viewModels()
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundbankRepository: SoundbankRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settingsRepository = SettingsRepository(this)

        val database = AppDatabase.getDatabase(this)
        soundbankRepository = SoundbankRepository(database.soundbankDao())

        // Observe the theme preference and update the app theme
        lifecycleScope.launch {
            settingsRepository.theme.collect { theme ->
                when (theme) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Light theme
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Dark theme
                }
            }
        }

        // Load fragments
        replaceFragment(R.id.soundControlsFragment, soundControlsFragment)
        replaceFragment(R.id.soundbankListFragment, soundbankListFragment)

        // Add Save Button
        val saveButton = findViewById<Button>(R.id.saveSoundbankButton)
        saveButton.setOnClickListener {
            showSaveSoundbankDialog()
        }

        // Check if the database is empty and add initial soundbanks if it is
        lifecycleScope.launch {
            val soundbanks = soundbankRepository.allSoundbanks.first()
            if (soundbanks.isEmpty()) {
                addInitialSoundbanks()
            }
        }
    }

    private fun replaceFragment(containerId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    private fun showSaveSoundbankDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = getString(R.string.enter_soundbank_name)
        builder.setView(input)
            .setTitle(R.string.soundbank_name)
            .setPositiveButton(R.string.save) { dialog, _ ->
                val soundbankName = input.text.toString()
                if (soundbankName.isNotBlank()) {
                    saveSoundbank(soundbankName)
                } else {
                    // Show an error message if the name is empty
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        builder.show()
    }


    private fun saveSoundbank(name: String) {
        // Create a comma-separated string of sound IDs from the currentSoundBank list
        val soundIds = soundControlsFragment.currentSoundBank.joinToString(",")

        // Create a Soundbank object with the name and the soundIds string
        val soundbank = Soundbank(name, soundIds)

        lifecycleScope.launch {
            soundbankRepository.insert(soundbank)
            Log.d("MainActivity", "Saved soundbank: $name with sound IDs: $soundIds")
        }
    }

    private suspend fun addInitialSoundbanks() {
        val initialSoundbanks = listOf(
            Soundbank("Sound Bank 1", "110011,418107,700380,527845,467762"),
            Soundbank("Sound Bank 2", "24929,275072,219244,52906,243629"),
            Soundbank("Sound Bank 3", "446753,34338,14777,649208,182472")
        )

        for (soundbank in initialSoundbanks) {
            soundbankRepository.insert(soundbank)
        }

        Log.d("MainActivity", "Added initial soundbanks")
    }
}