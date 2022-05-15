package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.joy.adapter.SavedAlbumRVAdapter
import com.example.joy.data.Album
import com.example.joy.data.SongDatabase
import com.example.joy.databinding.FragmentLockerSavedalbumBinding

class SavedAlbumFragment : Fragment() {
    private lateinit var binding: FragmentLockerSavedalbumBinding
    private lateinit var savedAlbumRVAdapter: SavedAlbumRVAdapter
    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        val jwt: String? = getJwt()
        if (jwt == null) {
            initRecyclerView(0)
        } else {
            initRecyclerView(jwt.split(',')[0].toInt())
        }

        return binding.root
    }

    private fun initRecyclerView(userId: Int) {
        savedAlbumRVAdapter = SavedAlbumRVAdapter()
        binding.savedAlbumRv.adapter = savedAlbumRVAdapter
        binding.savedAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savedAlbumRVAdapter.addAlbums(songDB.albumDao().getLikedAlbums(userId) as ArrayList<Album>)

        savedAlbumRVAdapter.setMyItemClickListener(object: SavedAlbumRVAdapter.MyItemClickListener {
            override fun onRemoveAlbum(albumId: Int, position: Int) {
                songDB.albumDao().disLikedAlbum(userId, albumId)
                savedAlbumRVAdapter.removeAlbum(position)
            }
        })
    }

    private fun getJwt(): String? {
        val sharedPreferences = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences!!.getString("jwt", null)
    }
}