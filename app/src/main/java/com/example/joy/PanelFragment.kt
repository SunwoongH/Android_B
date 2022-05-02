package com.example.joy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.joy.databinding.FragmentPanelBinding

class PanelFragment(private val imageResource: Int) : Fragment() {
    lateinit var binding: FragmentPanelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanelBinding.inflate(inflater, container, false)
        binding.homePannelBackgroundIv.setImageResource(imageResource)

        return binding.root
    }
}