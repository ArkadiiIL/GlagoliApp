package com.arkadii.glagoli.record

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException
import java.lang.RuntimeException
import java.nio.file.NotLinkException
import java.text.SimpleDateFormat
import java.util.*

class MediaRecorderManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var startPlayer = false
    private var fileName = ""
    var currentRecord = ""
        get() {
            if(field == "") throw NotLinkException("Link on currentRecord does not Exist!")
            else return field
        }
        private set
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
            currentRecord = fileName
            recorder = null
            fileName = ""
            startPlayer = false
        }
    }

    private fun initRecorder() {
        Log.i(TAG, "Init recorder")
        fileName =
            "${context.externalCacheDir?.absolutePath}" +
                    "/audiorecord-${simpleDateFormat.format(Date())}.m4a"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000);
            setAudioSamplingRate(44100);
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