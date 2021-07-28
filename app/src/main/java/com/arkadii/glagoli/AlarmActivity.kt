package com.arkadii.glagoli

import android.media.AudioAttributes
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.databinding.ActivityAlarmBinding
import com.arkadii.glagoli.record.MediaPlayerManager

class AlarmActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmViewModel: AlarmViewModel
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var alarm: Alarm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Start AlarmActivity")
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alarmViewModel = ViewModelProvider.
        AndroidViewModelFactory(application).create(AlarmViewModel::class.java)
        val path = intent.getStringExtra("path")
        Log.d(TAG, "Get $path")

        if(path != null) {
            alarmViewModel.getAlarmByPath(path).observe(this) { alarm ->
                if(alarm != null) {
                    this.alarm = alarm

                    Log.i(TAG, "Init MediaPlayerManager")
                    mediaPlayerManager = MediaPlayerManager()
                    mediaPlayerManager?.initMediaPlayer(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build(),
                            alarm.recordPath
                    )
                    setStopListener()
                    Log.i(TAG, "Play record from $alarm")
                    mediaPlayerManager?.isLooping(true)
                    mediaPlayerManager?.play()
                } else error("Alarm is null")
            }
        }
        }
    private fun disableAlarm(alarm: Alarm) {
        alarm.isEnabled = false
        alarmViewModel.updateAlarm(alarm)
    }

    private fun setStopListener() {
        binding.stopAlarm.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Close AlarmActivity")
        mediaPlayerManager?.stop()
        mediaPlayerManager?.closeMediaPlayer()

        val alarm = this.alarm
        if(alarm != null) {
            disableAlarm(alarm)
        }
    }

    companion object {
        const val TAG = "AlarmActivity"
    }
}