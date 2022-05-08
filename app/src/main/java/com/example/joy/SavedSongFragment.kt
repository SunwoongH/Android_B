package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.joy.adapter.SavedSongRVAdapter
import com.example.joy.data.Song
import com.example.joy.data.SongDatabase
import com.example.joy.databinding.FragmentLockerSavedsongBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SavedSongFragment : Fragment() {
    private lateinit var binding: FragmentLockerSavedsongBinding
    private lateinit var savedSongRVAdapter: SavedSongRVAdapter
    private lateinit var songDB: SongDatabase
    private var isAllSelected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        initRecyclerView()
        allSelectOnClick()

        return binding.root
    }

    private fun initRecyclerView() {
        savedSongRVAdapter = SavedSongRVAdapter()
        binding.savedSongRv.adapter = savedSongRVAdapter
        binding.savedSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savedSongRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)

        savedSongRVAdapter.setMyItemClickListener(object: SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int, position: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
                savedSongRVAdapter.removeSong(position)
            }
        })
    }

    private fun allSelectOnClick() {
        binding.savedSongAllSelectTv.setOnClickListener {
            setAllSelect()
        }
    }

    private fun setAllSelect() {
        isAllSelected = !isAllSelected
        if (isAllSelected) {
            binding.savedSongAllSelectIv.setColorFilter(ContextCompat.getColor(requireContext(), R.color.select_color))
            binding.savedSongAllSelectTv.text = "선택해제"
            binding.savedSongAllSelectTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.select_color))
            bottomSheetDialog()
        } else {
            binding.savedSongAllSelectIv.setColorFilter(ContextCompat.getColor(requireContext(), R.color.original_select_color))
            binding.savedSongAllSelectTv.text = "전체선택"
            binding.savedSongAllSelectTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.original_select_color))
        }
    }

    private fun bottomSheetDialog() {
        val bottomSheetDialog = BottomSheetFragment()
        bottomSheetDialog.setMyItemClickListener(object: BottomSheetFragment.MyItemClickListener {
            override fun onRemoveAllSong() {
                savedSongRVAdapter.removeAllSongs()
                songDB.songDao().updateAllIsLike(false)
            }
        }, object: BottomSheetFragment.MyItemDismissClickListener {
            override fun onDismiss() {
                setAllSelect()
            }
        })
        bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
    }
}