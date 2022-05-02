package com.example.joy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.joy.data.Album
import com.example.joy.data.Music
import com.example.joy.data.Song
import com.example.joy.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var song: Song = Song()
    private lateinit var timer: Timer
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private var isSwitch: Boolean = false
    private var gson: Gson = Gson()
    companion object { var music: Music = Music() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Joy)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActivityLauncher()

        switchToSongActivityOnClick()
        miniPlayerOnClick()

        initBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)
        song = if (songJson == null) {
            Song("LILAC", "아이유 (IU)", 0, 0f,60, false, "music_lilac", R.drawable.img_album_exp2)
        } else {
            gson.fromJson(songJson, Song::class.java)
        }
        //if (song.second == song.playTime) song.second = 0

        setMiniPlayer()
    }

    override fun onPause() {
        super.onPause()
        if (!isSwitch) setMiniPlayerStatus(false)
        song.second = timer.getSecond()
        song.mills = timer.getMills()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()
        timer.flag = true
        timer.interrupt()
    }
    
    // activity launcher 설정
    private fun setActivityLauncher() {
        activityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                song = result.data?.getSerializableExtra("song") as Song
                isSwitch = false
            }
        }
    }
    
    // MainActivity -> SongActivity
    private fun switchToSongActivityOnClick() {
        binding.mainPlayerCl.setOnClickListener {
            isSwitch = true
            timer.flag = true
            timer.interrupt()
            song = Song(song.title, song.singer, timer.getSecond(), timer.getMills(), timer.getPlayTime(), timer.getIsPlaying(), song.music, song.coverImg)
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

    // 앨범 플레이 아이콘 클릭시 미니 플레이어 synchronization
    fun changeMusic(album: Album) {
        if (music.statusMediaPlayer()) {
            music.releaseMediaPlayer()
            timer.flag = true
            timer.interrupt()
        }
        song = Song(album.title, album.singer, 0, 0f, 0, true, album.name, album.coverImg)
        setMiniPlayer()
    }

    // 미니 플레이어 synchronization
    private fun setMiniPlayer() {
        if (!music.statusMediaPlayer()) {
            music.createMediaPlayer(resources.getIdentifier(song.music, "raw", this.packageName), this)
            song.playTime = music.getDuration() / 1000
        }

        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainSongProgressSb.progress = ((song.mills * 100) / song.playTime).toInt()

        startTimer()
        setMiniPlayerStatus(song.isPlaying)
    }

    // 미니 플레이어 재생 <-> 중지
    private fun setMiniPlayerStatus(isPlaying: Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = song.isPlaying

        if (isPlaying) {
            binding.mainMiniplayerOffBtn.visibility = View.GONE
            binding.mainMiniplayerOnBtn.visibility = View.VISIBLE
            music.startMediaPlayer()
        } else {
            binding.mainMiniplayerOffBtn.visibility = View.VISIBLE
            binding.mainMiniplayerOnBtn.visibility = View.GONE
            if (music.isPlayingMediaPlayer() == true) {
                music.pauseMediaPlayer()
            }
        }
    }

    // 미니 플레이어 timer thread 시작
    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.set(song.second, song.mills)
        timer.start()
    }

    // 미니 플레이어 timer thread inner class
    inner class Timer(private var playTime: Int, var isPlaying: Boolean, var flag: Boolean = false) : Thread() {
        private var second: Int = 0
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
                            binding.mainSongProgressSb.progress = ((mills * 100) / playTime).toInt()
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