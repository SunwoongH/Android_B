package com.example.joy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongTable")
data class Song(
    val title: String? = "비어있음",
    val singer: String? = "비어있음",
    var second: Int = 0, // 현재 진행시간
    var mills: Float = 0f, // 현재 진행시간
    var playTime: Int = 0, // 총 진행시간
    var isPlaying: Boolean = false,
    var music: String? = "",
    var coverImg: Int? = null,
    var isLike: Boolean = false
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}