package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.joy.adapter.LockerVPAdapter
import com.example.joy.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    lateinit var binding: FragmentLockerBinding
    private val tapInformation = arrayListOf("저장한 곡", "음악 파일")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.songLockerVp.adapter = lockerAdapter
        TabLayoutMediator(binding.songLockerTb, binding.songLockerVp) {
            tab, position -> tab.text = tapInformation[position]
        }.attach()

        return binding.root
    }
}