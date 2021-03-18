package com.arkadii.glagoli

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException

class MediaRecorderManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private val fileName = "${context.externalCacheDir?.absolutePath}/audiorecordtest.3gp"

    fun startRecording() {
        Log.i(TAG, "Start recording audio")
        initRecorder()
        recorder?.start()
    }

    fun stopRecording() {
        Log.i(TAG, "Stop recording audio")
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun initRecorder() {
        Log.i(TAG, "Init recorder")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            try {
                prepare()
            } catch (error: IOException) {
                Log.e(TAG, "prepare failed")
                Log.e(TAG, error.toString())
            }
        }
    }


    companion object {
        const val TAG = "MediaRecorderManagerTAG"
    }
}