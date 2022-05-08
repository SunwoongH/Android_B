package com.example.joy.data

import androidx.room.*

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song

    @Query("SELECT * FROM SongTable WHERE title = :title")
    fun getSongByTitle(title: String?): Song

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song> // 왜 ArrayList 는 안될까?

    @Query("SELECT * FROM SongTable WHERE isLike = :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

    @Query("UPDATE SongTable SET isLike = :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int)

    @Query("UPDATE SongTable SET isLike = :isLike")
    fun updateAllIsLike(isLike: Boolean)

    @Query("UPDATE SongTable SET second = :second, mills = :mills, isPlaying = :isPlaying")
    fun updatePlaytime(second: Int, mills: Float, isPlaying: Boolean)
}