package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.joy.adapter.AlbumVPAdapter
import com.example.joy.data.Album
import com.example.joy.data.Like
import com.example.joy.data.SongDatabase
import com.example.joy.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    private lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()
    private val tapInformation = arrayListOf("수록곡", "상세정보", "영상")
    private lateinit var songDB: SongDatabase
    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)

        songDB = SongDatabase.getInstance(requireContext())!!

        isLiked = isLikedAlbum(album.id)
        setInit(album)
        setOnClickListeners(album)

        // 닫기 누르면 HomeFragment로 전환
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment()).commitAllowingStateLoss()
        }

        // TabLayout & ViewPager2의 Synchronization
        val albumAdapter = AlbumVPAdapter(this)
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
            tap, position -> tap.text = tapInformation[position]
        }.attach()

        return binding.root
    }

    private fun setInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()

        if (isLiked) {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt(): String? {
        val sharedPreferences = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences!!.getString("jwt", null)
    }

    private fun likeAlbum(userId: Int, albumId: Int) {
        songDB.albumDao().likeAlbum(Like(userId, albumId))
    }

    private fun isLikedAlbum(albumId: Int): Boolean {
        val jwt = getJwt()
        val userId: Int = if (jwt == null) 0 else jwt.split(',')[0].toInt()

        val likeId: Int? = songDB.albumDao().isLikedAlbum(userId, albumId)
        return likeId != null
    }

    private fun disLikedAlbum(userId: Int, albumId: Int) {
        songDB.albumDao().disLikedAlbum(userId, albumId)
    }

    private fun setOnClickListeners(album: Album) {
        val jwt = getJwt()
        val userId: Int = if (jwt == null) 0 else jwt.split(',')[0].toInt()
        binding.albumLikeIv.setOnClickListener {
            if (isLiked) {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(userId, album.id)
            } else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
        }
    }
}