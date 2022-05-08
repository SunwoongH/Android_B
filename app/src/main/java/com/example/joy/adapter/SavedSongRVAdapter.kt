package com.example.joy.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.joy.data.Album
import com.example.joy.data.Song
import com.example.joy.databinding.ItemBookmarkBinding

class SavedSongRVAdapter(): RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {
    private val songs = ArrayList<Song>()

    interface MyItemClickListener {
        fun onRemoveSong(songId: Int, position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSong(position: Int) {
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAllSongs() {
        songs.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemBookmarkBinding = ItemBookmarkBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.binding.bookmarkSongDeleteIv.setOnClickListener { mItemClickListener.onRemoveSong(songs[position].id, position) }
    }

    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding: ItemBookmarkBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.bookmarkSongTitleTv.text = song.title
            binding.bookmarkSongSingerTv.text = song.singer
            binding.bookmarkSongAlbumIv.setImageResource(song.coverImg!!)
        }
    }
}