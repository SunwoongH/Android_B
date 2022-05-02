package com.example.joy.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PanelVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val panelFragment: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = panelFragment.size

    override fun createFragment(position: Int): Fragment = panelFragment[position]

    fun addFragment(fragment: Fragment) {
        panelFragment.add(fragment)
        notifyItemInserted(panelFragment.size - 1)
    }
}