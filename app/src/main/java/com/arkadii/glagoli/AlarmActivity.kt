package com.arkadii.glagoli

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.databinding.ActivityAlarmBinding
import com.arkadii.glagoli.record.MediaPlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlarmActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmViewModel: AlarmViewModel
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var alarm: Alarm? = null
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Start AlarmActivity")
        allowOnLockScreen()

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alarmViewModel = ViewModelProvider.
        AndroidViewModelFactory(application).create(AlarmViewModel::class.java)
        val path = intent.getStringExtra("path")
        Log.d(TAG, "Get $path")

        if(path != null) {
            runBlocking {
              val job = launch(Dispatchers.IO) {
                   alarm = alarmViewModel.getAlarmByPath(path)
                }
                job.join()
            }
            val alarm = this.alarm
            if(alarm != null) {
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
                vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            Log.i(TAG, "Play record from $alarm")
            mediaPlayerManager?.isLooping(true)
            mediaPlayerManager?.play()
            vibrate() }
        }

    private fun allowOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }

    private fun vibrate() {
        val array = arrayOf(0L, 100L, 1000L, 200L, 2000L).toLongArray()
        val vibrationEffect = VibrationEffect.createWaveform(array, -1)
        vibrator.cancel()
        vibrator.vibrate(vibrationEffect)
    }

    private fun setStopListener() {
        binding.stopAlarm.setOnClickListener {
            val alarm = this.alarm
            if(alarm != null) disableAlarm(alarm)
            Log.d(TAG, "media player = ${mediaPlayerManager == null}")
            mediaPlayerManager?.stop()
            mediaPlayerManager?.closeMediaPlayer()
            vibrator.cancel()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Close AlarmActivity")
        mediaPlayerManager?.stop()
        mediaPlayerManager?.closeMediaPlayer()
        vibrator.cancel()

        val alarm = this.alarm
        if(alarm != null && alarm.isEnabled) {
            disableAlarm(alarm)
        }
    }

    private fun disableAlarm(alarm: Alarm) {
        alarm.isEnabled = false
        alarm.pendingIntentId = 0
        alarmViewModel.updateAlarm(alarm)
    }

    companion object {
        const val TAG = "AlarmActivity"
    }
}