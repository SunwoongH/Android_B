package com.example.joy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.joy.data.Album
import com.example.joy.databinding.ItemBookmarkBinding

class StoreRVAdapter(private val albumList: ArrayList<Album>): RecyclerView.Adapter<StoreRVAdapter.ViewHolder>() {

    interface MyItemClickListener {
        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun removeItem(position: Int) {
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemBookmarkBinding = ItemBookmarkBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.binding.bookmarkSongDeleteIv.setOnClickListener { mItemClickListener.onRemoveAlbum(position) }
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemBookmarkBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.bookmarkSongTitleTv.text = album.title
            binding.bookmarkSongSingerTv.text = album.singer
            binding.bookmarkSongAlbumIv.setImageResource(album.coverImg!!)
        }
    }
}