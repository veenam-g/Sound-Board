package com.example.soundboard

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var sound1Player: MediaPlayer? = null
    private var sound2Player: MediaPlayer? = null
    private var sound3Player: MediaPlayer? = null
    private var sound4Player: MediaPlayer? = null
    private var sound5Player: MediaPlayer? = null
    private var isSound1Prepared = false
    private var isSound2Prepared = false
    private var isSound3Prepared = false
    private var isSound4Prepared = false
    private var isSound5Prepared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchSoundsByIds(listOf(681930, 315617))

        val sound1Button: Button = findViewById(R.id.Sound1_Button)
        val sound2Button: Button = findViewById(R.id.Sound2_Button)
        val sound3Button: Button = findViewById(R.id.Sound3_Button)
        val sound4Button: Button = findViewById(R.id.Sound4_Button)
        val sound5Button: Button = findViewById(R.id.Sound5_Button)

        sound1Button.setOnClickListener {
            if (isSound1Prepared) {
                sound1Player?.start()
            } else {
                Log.e("MediaPlayer", "Sound1 is not prepared")
            }
        }

        sound2Button.setOnClickListener {
            if (isSound2Prepared) {
                sound2Player?.start()
            } else {
                Log.e("MediaPlayer", "Sound2 is not prepared")
            }
        }

        sound3Button.setOnClickListener {
            if (isSound3Prepared) {
                sound3Player?.start()
            } else {
                Log.e("MediaPlayer", "Sound3 is not prepared")
            }
        }

        sound4Button.setOnClickListener {
            if (isSound4Prepared) {
                sound4Player?.start()
            } else {
                Log.e("MediaPlayer", "Sound4 is not prepared")
            }
        }

        sound5Button.setOnClickListener {
            if (isSound5Prepared) {
                sound5Player?.start()
            } else {
                Log.e("MediaPlayer", "Sound5 is not prepared")
            }
        }
    }

    private fun fetchSoundsByIds(ids: List<Int>) {
        ids.forEach { id ->
            Log.d("API", "Fetching sound with ID: $id")
            val call = RetrofitInstance.api.getSoundById(id)
            call.enqueue(object : Callback<Sound> {
                override fun onResponse(call: Call<Sound>, response: Response<Sound>) {
                    if (response.isSuccessful) {
                        val sound = response.body()
                        sound?.let {
                            Log.d("API", "Fetched sound: ID: ${it.id}, Name: ${it.name}, URL: ${it.url}")
                            val previewUrl = it.previews["preview-hq-mp3"] ?: it.previews["preview-lq-mp3"]
                            Log.d("API", "Using preview URL: $previewUrl")
                            when (it.id) {
                                681930 -> {
                                    sound1Player = MediaPlayer().apply {
                                        setDataSource(previewUrl as String)
                                        setOnPreparedListener { player ->
                                            isSound1Prepared = true
                                            Log.d("MediaPlayer", "Sound1 is prepared")
                                        }
                                        prepareAsync()
                                    }
                                    sound3Player = MediaPlayer().apply {
                                        setDataSource(previewUrl as String)
                                        setOnPreparedListener { player ->
                                            isSound3Prepared = true
                                            Log.d("MediaPlayer", "Sound3 is prepared")
                                        }
                                        prepareAsync()
                                    }
                                    sound5Player = MediaPlayer().apply {
                                        setDataSource(previewUrl as String)
                                        setOnPreparedListener { player ->
                                            isSound5Prepared = true
                                            Log.d("MediaPlayer", "Sound5 is prepared")
                                        }
                                        prepareAsync()
                                    }
                                }
                                315617 -> {
                                    sound2Player = MediaPlayer().apply {
                                        setDataSource(previewUrl as String)
                                        setOnPreparedListener { player ->
                                            isSound2Prepared = true
                                            Log.d("MediaPlayer", "Sound2 is prepared")
                                        }
                                        prepareAsync()
                                    }
                                    sound4Player = MediaPlayer().apply {
                                        setDataSource(previewUrl as String)
                                        setOnPreparedListener { player ->
                                            isSound4Prepared = true
                                            Log.d("MediaPlayer", "Sound4 is prepared")
                                        }
                                        prepareAsync()
                                    }
                                }
                            }
                        } ?: Log.d("API", "No sound found for ID: $id")
                    } else {
                        Log.e("API", "Failed to fetch sound for ID: $id, Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Sound>, t: Throwable) {
                    Log.e("API", "Error fetching sound for ID: $id, Message: ${t.message}")
                }
            })
        }
    }
}