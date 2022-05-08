package com.example.joy

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.joy.data.Song
import com.example.joy.data.SongDatabase
import com.example.joy.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongBinding
    private lateinit var timer: Timer
    private var isSwitch: Boolean = false
    private val songs =  arrayListOf<Song>()
    private lateinit var songDB: SongDatabase
    private var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songDB = SongDatabase.getInstance(this)!!

        initPlayList()
        initSong()

        mainPlayerOnClick()
        switchToMainActivityOnClick()
        changeSongOnClick()
        setLikeOnClick()
    }

    override fun onPause() {
        super.onPause()
        if (!isSwitch) setMainPlayerStatus(false)
        timer.flag = true
        timer.interrupt()
        updateCurrentSong()
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
    }

    // 음악 플레이리스트 초기화
    private fun initPlayList() {
        songs.addAll(songDB.songDao().getSongs())
    }

    // 현재 음악 초기화
    private fun initSong() {
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        setMainPlayer(songs[nowPos])
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

    // SongActivity -> MainActivity
    private fun switchToMainActivityOnClick() {
        binding.songDownIb.setOnClickListener {
            isSwitch = true
            finish()
        }
    }

    // previous, next 이동 버튼
    private fun changeSongOnClick() {
        binding.songNextIv.setOnClickListener {
            changeSong(1)
        }

        binding.songPreviousIv.setOnClickListener {
            changeSong(-1)
        }
    }

    // previous <- current -> next 이동
    private fun changeSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        } else if (nowPos + direct >= songs.size){
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }
        nowPos += direct
        if (MainActivity.music.statusMediaPlayer()) {
            MainActivity.music.releaseMediaPlayer()
            timer.flag = true
            timer.interrupt()
        }
        updateCurrentSong(true)
        setMainPlayer(songs[nowPos])
    }

    // 음악 좋아요 on / off
    private fun setLikeOnClick() {
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

    private fun setLike(isLike: Boolean) {
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if (!isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            Toast.makeText(this,"${songs[nowPos].title} 곡을 좋아요 합니다.", Toast.LENGTH_SHORT).show()
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            Toast.makeText(this,"${songs[nowPos].title} 곡을 좋아요 취소합니다.", Toast.LENGTH_SHORT).show()
        }
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

    // 메인 플레이어 synchronization
    private fun setMainPlayer(song: Song) {
        if (!MainActivity.music.statusMediaPlayer()) {
            MainActivity.music.createMediaPlayer(resources.getIdentifier(song.music, "raw", this.packageName), this)
        }

        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = ((song.mills * 100) / song.playTime).toInt()
        binding.songAlbumIv.setImageResource(song.coverImg!!)

        if (song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        startTimer()
        setMainPlayerStatus(song.isPlaying)
    }

    // 메인 플레이어 재생 / 중지
    private fun setMainPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = songs[nowPos].isPlaying

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
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.set(songs[nowPos].second, songs[nowPos].mills)
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