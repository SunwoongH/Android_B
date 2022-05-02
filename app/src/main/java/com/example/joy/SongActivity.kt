package com.example.joy

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.joy.data.Music
import com.example.joy.data.Song
import com.example.joy.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding
    private lateinit var song: Song
    private lateinit var timer: Timer
    private var isSwitch: Boolean = false
    private var gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        song = intent.getSerializableExtra("song") as Song

        setMainPlayer()

        switchToMainActivityOnClick()
        mainPlayerOnClick()
    }

    override fun onPause() {
        super.onPause()
        if (!isSwitch) setMainPlayerStatus(false)
        song.second = timer.getSecond()
        song.mills = timer.getMills()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()
    }

    // SongActivity -> MainActivity
    private fun switchToMainActivityOnClick() {
        binding.songDownIb.setOnClickListener {
            isSwitch = true
            timer.flag = true
            timer.interrupt()
            song = Song(song.title, song.singer, timer.getSecond(), timer.getMills(), timer.getPlayTime(), timer.getIsPlaying(), song.music, song.coverImg)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("song", song)
            setResult(RESULT_OK, intent)
            if (!isFinishing) finish()
        }
    }

    // 메인 플레이어 재생 / 중지
    private fun mainPlayerOnClick() {
        binding.songMainPlayerIv.setOnClickListener {
            setMainPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setMainPlayerStatus(false)
        }
    }

    // 메인 플레이어 synchronization
    private fun setMainPlayer() {
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = ((song.mills * 100) / song.playTime).toInt()
        binding.songAlbumIv.setImageResource(song.coverImg!!)

        startTimer()
        setMainPlayerStatus(song.isPlaying)
    }

    // 메인 플레이어 재생 <-> 중지
    private fun setMainPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = song.isPlaying

        if (isPlaying) {
            binding.songMainPlayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            MainActivity.music.startMediaPlayer()
        } else {
            binding.songMainPlayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if (MainActivity.music.isPlayingMediaPlayer() == true) {
                MainActivity.music.pauseMediaPlayer()
            }
        }
    }

    // 메인 플레이어 timer thread 시작
    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.set(song.second, song.mills)
        timer.start()
    }

    // 메인 플레이어 timer thread inner class
    inner class Timer(private var playTime: Int, var isPlaying: Boolean = true, var flag: Boolean = false) : Thread() {
        private var second : Int = 0
        private var mills: Float = 0f

        fun set(second: Int, mills: Float) {
            this.second = second
            this.mills = mills
        }

        fun getSecond(): Int = second

        fun getMills(): Float = mills

        fun getPlayTime(): Int = playTime

        fun getIsPlaying(): Boolean = isPlaying

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) break
                    if (isPlaying) {
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills * 100) / playTime).toInt()
                        }
                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                    if (flag) sleep(1L)
                }
            } catch (e: InterruptedException) {
                Log.d("SONG", "SongActivity Thread가 죽었습니다. ${e.message}")
            }
        }
    }
}