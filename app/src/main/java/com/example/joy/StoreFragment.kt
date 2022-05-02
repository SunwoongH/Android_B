package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.joy.adapter.StoreRVAdapter
import com.example.joy.data.Album
import com.example.joy.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {
    lateinit var binding: FragmentStoreBinding
    private var albumData: ArrayList<Album> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(inflater, container, false)

        albumData.apply {
            add(Album("LILAC", "아이유 (IU)", R.drawable.img_album_exp2))
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
            add(Album("Next Level", "aespa", R.drawable.img_album_exp3))
            add(Album("작은 것들을 위한 시", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
            add(Album("BAAM", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
            add(Album("Weekeng", "태연 (TAEYEON)", R.drawable.img_album_exp6))
        }

        val storeRVAdapter = StoreRVAdapter(albumData)
        binding.storeRv.adapter = storeRVAdapter
        binding.storeRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        storeRVAdapter.setMyItemClickListener(object: StoreRVAdapter.MyItemClickListener {
            override fun onRemoveAlbum(position: Int) {
                storeRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }
}