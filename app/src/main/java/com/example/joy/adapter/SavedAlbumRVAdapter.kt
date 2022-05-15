package com.example.joy.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.joy.data.Album
import com.example.joy.databinding.ItemBookmarkAlbumBinding

class SavedAlbumRVAdapter : RecyclerView.Adapter<SavedAlbumRVAdapter.ViewHolder>() {
    private val albums = ArrayList<Album>()

    interface MyItemClickListener {
        fun onRemoveAlbum(albumId: Int, position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAlbum(position: Int) {
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemBookmarkAlbumBinding = ItemBookmarkAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])
        holder.binding.bookmarkAlbumDeleteIv.setOnClickListener { mItemClickListener.onRemoveAlbum(albums[position].id, position) }
    }

    override fun getItemCount(): Int = albums.size

    inner class ViewHolder(val binding: ItemBookmarkAlbumBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.bookmarkAlbumTitleTv.text = album.title
            binding.bookmarkAlbumSingerTv.text = album.singer
            binding.bookmarkAlbumIv.setImageResource(album.coverImg!!)
        }
    }
}