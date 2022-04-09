package com.example.joy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.joy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var song: Song
    private lateinit var timer: Timer
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Joy)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        startTimer()
        setMiniPlayer()

        setActivityLauncher()

        switchToSongActivityOnClick()
        miniPlayerOnClick()

        initBottomNavigation()
    }
    
    // song data class 초기값 설정
    private fun initSong() {
        song = Song("LILAC", "아이유 (IU)", 0, 60, false)
    }
    
    // activity launcher 설정
    private fun setActivityLauncher() {
        activityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                song = result.data?.getSerializableExtra("song") as Song
                startTimer()
                setMiniPlayer()
            }
        }
    }
    
    // MainActivity -> SongActivity
    private fun switchToSongActivityOnClick() {
        binding.mainPlayerCl.setOnClickListener {
            timer.flag = true
            timer.interrupt()
            song = Song(song.title, song.singer, timer.getSecond(), timer.getPlayTime(), timer.getIsPlaying())
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("song", song)
            activityLauncher.launch(intent)
        }
    }
    
    // 미니 플레이어 재생 / 중지
    private fun miniPlayerOnClick() {
        binding.mainMiniplayerOffBtn.setOnClickListener {
            setMiniPlayerStatus(true)
        }
        binding.mainMiniplayerOnBtn.setOnClickListener {
            setMiniPlayerStatus(false)
        }
    }

    // 미니 플레이어 synchronization
    private fun setMiniPlayer() {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainSongProgressSb.progress = (song.second * 100000 / song.playTime)

        setMiniPlayerStatus(song.isPlaying)
    }

    // 미니 플레이어 재생 <-> 중지
    private fun setMiniPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = song.isPlaying

        if (isPlaying) {
            binding.mainMiniplayerOffBtn.visibility = View.GONE
            binding.mainMiniplayerOnBtn.visibility = View.VISIBLE
        } else {
            binding.mainMiniplayerOffBtn.visibility = View.VISIBLE
            binding.mainMiniplayerOnBtn.visibility = View.GONE
        }
    }

    // 미니 플레이어 timer thread 시작
    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.set(song.second)
        timer.start()
    }

    // 미니 플레이어 timer thread inner class
    inner class Timer(private var playTime: Int, var isPlaying: Boolean, var flag: Boolean = false) : Thread() {
        private var second: Int = 0
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
                            binding.mainSongProgressSb.progress = (mills * 100 / playTime).toInt()
                        }
                        if (mills % 1000 == 0f) {
                            second++
                        }
                    }
                    if (flag) sleep(1L)
                }
            } catch (e: InterruptedException) {
                Log.d("MAIN", "MainActivity Thread가 죽었습니다. ${e.message}")
            }
        }
    }

    // 바텀 네비게이션
    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true

                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}