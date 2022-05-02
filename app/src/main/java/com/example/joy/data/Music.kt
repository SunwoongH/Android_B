package com.example.joy.data

import android.content.Context
import android.media.MediaPlayer

class Music {
    var mediaPlayer: MediaPlayer? = null

    fun createMediaPlayer(music: Int, context: Context) {
        mediaPlayer = MediaPlayer.create(context, music)
    }

    fun getDuration(): Int = mediaPlayer!!.duration

    fun startMediaPlayer() {
        mediaPlayer?.start()
    }

    fun pauseMediaPlayer() {
        mediaPlayer?.pause()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isPlayingMediaPlayer(): Boolean? = mediaPlayer?.isPlaying

    fun statusMediaPlayer(): Boolean {
        return mediaPlayer != null
    }
}