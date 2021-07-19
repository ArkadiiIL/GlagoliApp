package com.arkadii.glagoli.record

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class MediaPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun initMediaPlayer(audioAttributes: AudioAttributes, url: String): Boolean  {
        return if(mediaPlayer != null) {
            closeMediaPlayer()
            initMediaPlayer(audioAttributes, url)
        } else {
            Log.i(TAG, "Init MediaPlayer with $url")
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioAttributes(audioAttributes)
            mediaPlayer?.setDataSource(url)
            true
        }
    }

    fun play() {
        Log.i(TAG, "MediaPlayer play")
        mediaPlayer?.apply {
            prepare()
            start()
        }
    }

    fun isLooping(isLooping: Boolean) {
        mediaPlayer?.isLooping = isLooping
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

    fun setCompletionListener(listener: () -> Unit) {
        mediaPlayer?.setOnCompletionListener {
            listener()
            stop()
        }
    }

    companion object {
        const val TAG = "MediaPlayerManager"
    }
}