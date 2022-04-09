package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.joy.databinding.FragmentSongBinding

class SongFragment : Fragment() {
    lateinit var binding: FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        // 내 취향 mix 버튼 on/off
        binding.songMixoffTg.setOnClickListener {
            setSongMixStatus(false)
        }
        binding.songMixonTg.setOnClickListener {
            setSongMixStatus(true)
        }

        // Toast 메시지 출력
        binding.songLilacLayout.setOnClickListener {
            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
        }
        binding.songSmileyLayout.setOnClickListener {
            Toast.makeText(activity, "SMILEY(Feat. BIBI)", Toast.LENGTH_SHORT).show()
        }
        binding.songRainLayout.setOnClickListener {
            Toast.makeText(activity, "Rain", Toast.LENGTH_SHORT).show()
        }
        binding.songParachuteLayout.setOnClickListener {
            Toast.makeText(activity, "parachute", Toast.LENGTH_SHORT).show()
        }
        binding.songShapeOfYouLayout.setOnClickListener {
            Toast.makeText(activity, "Shape of You", Toast.LENGTH_SHORT).show()
        }
        binding.songPowerUpLayout.setOnClickListener {
            Toast.makeText(activity, "Power Up", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    private fun setSongMixStatus(isMix: Boolean) {
        if (isMix) {
            binding.songMixoffTg.visibility = View.VISIBLE
            binding.songMixonTg.visibility = View.GONE
        } else {
            binding.songMixoffTg.visibility = View.GONE
            binding.songMixonTg.visibility = View.VISIBLE
        }
    }
}