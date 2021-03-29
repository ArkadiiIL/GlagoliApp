package com.arkadii.glagoli

import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.roundToInt

class TimerManager(private val appCompatActivity: AppCompatActivity) {

    private val timer = Timer()
    private var time = 0.0
    private var timerTask: TimerTask? = null
    private var isStarted = false

    fun start(timeText: TextView) {
        Log.i(TAG, "Start timer")
        isStarted = true
        timerTask = object: TimerTask() {
            override fun run() {
                appCompatActivity.runOnUiThread {
                    time++
                    if(isStarted) {
                        timeText.text = getTimerText()
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    fun stop(timeText: TextView) {
        Log.i(TAG, "Stop timer")
        timerTask?.cancel()
        time = 0.0
        isStarted = false
        timeText.text = "00:00"
    }

    private fun getTimerText(): String {
        val rounded = time.roundToInt()

        val seconds = ((rounded % 86400) % 3600) % 60
        val minutes = ((rounded % 86400) % 3600) / 60
        val hours = ((rounded % 86400) / 3600)

        return if(hours == 0) "%02d:%02d".format(minutes, seconds)
        else "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    companion object {
        const val TAG = "TimeManagerCHECKTAG"
    }
}