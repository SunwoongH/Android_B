package com.example.joy.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val bannerFragment: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = bannerFragment.size

    override fun createFragment(position: Int): Fragment = bannerFragment[position]

    fun addFragment(fragment: Fragment) {
        bannerFragment.add(fragment)
        notifyItemInserted(bannerFragment.size - 1)
    }
}