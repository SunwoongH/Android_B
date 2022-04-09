package com.example.joy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.joy.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding
    private lateinit var song: Song
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        song = intent.getSerializableExtra("song") as Song
        setMainPlayer()

        switchToMainActivityOnClick()
        mainPlayerOnClick()
    }

    // SongActivity -> MainActivity
    private fun switchToMainActivityOnClick() {
        binding.songDownIb.setOnClickListener {
            timer.flag = true
            timer.interrupt()
            song = Song(song.title, song.singer, timer.getSecond(), timer.getPlayTime(), timer.getIsPlaying())
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("song", song)
            setResult(RESULT_OK, intent)
            if (!isFinishing) finish()
        }
    }

    // 메인 플레이어 재생 / 중지
    private fun mainPlayerOnClick() {
        binding.songMinplayerIv.setOnClickListener {
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
        binding.songProgressSb.progress = (song.second * 100000 / song.playTime)

        startTimer()
        setMainPlayerStatus(song.isPlaying)
    }

    // 메인 플레이어 재생 <-> 중지
    private fun setMainPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = song.isPlaying

        if (isPlaying) {
            binding.songMinplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        } else {
            binding.songMinplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        }
    }

    // 메인 플레이어 timer thread 시작
    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.set(song.second)
        timer.start()
    }

    // 메인 플레이어 timer thread inner class
    inner class Timer(private var playTime: Int, var isPlaying: Boolean = true, var flag: Boolean = false) : Thread() {
        private var second : Int = 0
        private var mills: Float = 0f

        fun set(second: Int) {
            this.second = second
            this.mills = (second * 1000).toFloat()
        }

        fun getSecond(): Int = second

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
                            binding.songProgressSb.progress = (mills * 100 / playTime).toInt()
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
            }catch (e: InterruptedException) {
                Log.d("SONG", "SongActivity Thread가 죽었습니다. ${e.message}")
            }
        }
    }
}