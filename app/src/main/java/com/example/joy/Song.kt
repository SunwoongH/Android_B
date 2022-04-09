package com.example.joy

import java.io.Serializable

data class Song(
    val title: String = "비어있음",
    val singer: String = "비어있음",
    var second: Int = 0, // 현재 진행시간
    var playTime: Int = 0, // 총 진행시간
    var isPlaying: Boolean = false
) : Serializable