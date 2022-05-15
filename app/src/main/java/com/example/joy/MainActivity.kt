package com.example.joy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.joy.data.Album
import com.example.joy.data.Music
import com.example.joy.data.Song
import com.example.joy.data.SongDatabase
import com.example.joy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private var isSwitch: Boolean = false
    private val songs = arrayListOf<Song>()
    private lateinit var songDB: SongDatabase
    private var nowPos = 0

    companion object { var music: Music = Music() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Joy)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songDB = SongDatabase.getInstance(this)!!

        inputSongs()
        inputAlbums()

        initPlayList()

        miniPlayerOnClick()
        switchToSongActivityOnClick()
        changeSongOnClick()

        initBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        isSwitch = false
        initPlayList()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        setMiniPlayer(songs[nowPos])
    }

    override fun onPause() {
        super.onPause()
        if (!isSwitch) setMiniPlayerStatus(false)
        timer.flag = true
        timer.interrupt()
        updateCurrentSong()
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
    }
    
    // 음악 플레이리스트 초기화
    private fun initPlayList() {
        songs.clear()
        songs.addAll(songDB.songDao().getSongs())
    }

    // Room DB 초기 음악 설정
    private fun inputSongs() {
        val songs = songDB.songDao().getSongs()
        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song("LILAC", "아이유 (IU)", 0, 0f, 215, false, "music_lilac", R.drawable.img_album_exp2, false)
        )
        songDB.songDao().insert(
            Song("Butter", "방탄소년단 (BTS)", 0, 0f, 165, false, "music_butter", R.drawable.img_album_exp, false)
        )
        songDB.songDao().insert(
            Song("Next Level", "aespa", 0, 0f, 222, false, "music_next", R.drawable.img_album_exp3, false)
        )
        songDB.songDao().insert(
            Song("작은 것들을 위한 시", "방탄소년단 (BTS)", 0, 0f, 252, false, "music_boy", R.drawable.img_album_exp4, false)
        )
        songDB.songDao().insert(
            Song("BAAM", "모모랜드 (MOMOLAND)", 0, 0f, 210, false, "music_bboom", R.drawable.img_album_exp5, false)
        )
        songDB.songDao().insert(
            Song("Weekend", "태연 (TAEYEON)", 0, 0f, 190, false, "music_flu", R.drawable.img_album_exp6, false)
        )
    }

    private fun inputAlbums() {
        val albums = songDB.albumDao().getAlbums()
        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(Album(0, "LILAC", "아이유 (IU)", R.drawable.img_album_exp2, "music_lilac"))
        songDB.albumDao().insert(Album(1, "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, "music_butter"))
        songDB.albumDao().insert(Album(2, "Next Level", "aespa", R.drawable.img_album_exp3, "music_next"))
        songDB.albumDao().insert(Album(3, "작은 것들을 위한 시", "방탄소년단 (BTS)", R.drawable.img_album_exp4, "music_boy"))
        songDB.albumDao().insert(Album(4,"BAAM", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, "music_bboom"))
        songDB.albumDao().insert(Album(5,"Weekend", "태연 (TAEYEON)", R.drawable.img_album_exp6, "music_flu"))
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

    // MainActivity -> SongActivity
    private fun switchToSongActivityOnClick() {
        binding.mainPlayerCl. setOnClickListener {
            isSwitch = true
            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
    }

    // previous, next 이동 버튼
    private fun changeSongOnClick() {
        binding.mainMiniplayerNextBtn.setOnClickListener {
            changeSong(1)
        }
        binding.mainMiniplayerPreviousBtn.setOnClickListener {
            changeSong(-1)
        }
    }

    // previous <- current -> next 이동
    private fun changeSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        } else if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct
        if (music.statusMediaPlayer()) {
            music.releaseMediaPlayer()
            timer.flag = true
            timer.interrupt()
        }
        updateCurrentSong(true)
        setMiniPlayer(songs[nowPos])
    }

    // 음악 플레이리스트에서 현재 음악의 position 값 반환
    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    // 현재 음악 정보 Room DB에 update
    private fun updateCurrentSong(isInit: Boolean = false) {
        if (isInit) {
            songs[nowPos].second = 0
            songs[nowPos].mills = 0f
            songs[nowPos].isPlaying = true
        } else {
            songs[nowPos].second = timer.getSecond()
            songs[nowPos].mills = timer.getMills()
            songs[nowPos].isPlaying = timer.getIsPlaying()
        }

        songDB.songDao().updatePlaytime(songs[nowPos].second, songs[nowPos].mills, songs[nowPos].isPlaying)
        //Log.d("sss", songDB.songDao().getSong(songs[nowPos].id).toString())
    }

    // 앨범 플레이 아이콘 클릭시 미니 플레이어 synchronization
    fun changeAlbumSong(album: Album) {
        if (music.statusMediaPlayer()) {
            music.releaseMediaPlayer()
            timer.flag = true
            timer.interrupt()
        }
        nowPos = getPlayingSongPosition(songDB.songDao().getSongByTitle(album.title).id)
        updateCurrentSong(true)
        setMiniPlayer(songs[nowPos])
    }

    // 미니 플레이어 synchronization
    private fun setMiniPlayer(song: Song) {
        if (!music.statusMediaPlayer()) {
            music.createMediaPlayer(resources.getIdentifier(song.music, "raw", this.packageName), this)
        }

        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainSongProgressSb.progress = ((song.mills * 100) / song.playTime).toInt()

        startTimer()
        setMiniPlayerStatus(song.isPlaying)
    }

    // 미니 플레이어 재생 / 중지
    private fun setMiniPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = songs[nowPos].isPlaying

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
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.set(songs[nowPos].second, songs[nowPos].mills)
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