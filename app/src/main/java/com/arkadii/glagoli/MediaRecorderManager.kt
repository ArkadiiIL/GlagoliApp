package com.arkadii.glagoli

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*

class MediaRecorderManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var startPlayer = false
    private var fileName = ""
    private val simpleDateFormat =
        SimpleDateFormat(
            "ddMMyy-hhmmss", Locale.getDefault())

    fun startRecording() {
        Log.i(TAG, "Start recording audio")
        if(!startPlayer) {
            initRecorder()
            recorder?.start()
            startPlayer = true
        }
    }

    fun stopRecording() {
        Log.i(TAG, "Stop recording audio")
        if(startPlayer) {
            recorder?.apply {
                try {
                    stop()
                    release() }
                catch (error: RuntimeException) {
                    Log.w(TAG, "RuntimeException in stopRecording, " +
                            "MediaRecorder hasn't received any data")
                    Log.e(TAG, error.toString())
                }
            }
            recorder = null
            fileName = ""
            startPlayer = false
        }
    }

    private fun initRecorder() {
        Log.i(TAG, "Init recorder")
        fileName =
            "${context.externalCacheDir?.absolutePath}" +
                    "/audiorecord-${simpleDateFormat.format(Date())}.3gp"
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
        const val TAG = "MediaRecorderManagerCHECKTAG"
    }
}