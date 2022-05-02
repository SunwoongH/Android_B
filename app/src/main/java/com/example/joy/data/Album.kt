package com.example.joy.data

data class Album(
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,
    var name: String? = "",
    var song: ArrayList<Song>? = null // 수록곡
)
