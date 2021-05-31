package com.arkadii.glagoli.record

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class MediaPlayerManager() {
    private var mediaPlayer: MediaPlayer? = null

    fun initMediaPlayer(audioAttributes: AudioAttributes): Boolean  {
        return if(mediaPlayer != null) {
            closeMediaPlayer()
            initMediaPlayer(audioAttributes)
        } else {
            Log.i(TAG, "Init MediaPlayer")
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioAttributes(audioAttributes)
            true
        }
    }

    fun play(url: String) {
        Log.i(TAG, "MediaPlayer play $url")
        mediaPlayer?.apply {
            setDataSource(url)
            prepare()
            start()
        }

    }

    fun stop() {
        Log.i(TAG, "Stop MediaPlayer")
        mediaPlayer?.stop()
    }

    fun closeMediaPlayer() {
        Log.i(TAG, "Close MediaPlayer")
        mediaPlayer?.release()
        mediaPlayer = null
    }


    fun changeAudioAttributes(audioAttributes: AudioAttributes): Boolean {
        return if(mediaPlayer != null) {
            mediaPlayer?.setAudioAttributes(audioAttributes)
            true
        } else false
    }

    companion object {
        const val TAG = "MediaPlayerManagerCHECKTAG"
    }
}