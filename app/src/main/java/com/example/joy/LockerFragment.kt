package com.example.joy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.joy.adapter.LockerVPAdapter
import com.example.joy.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    lateinit var binding: FragmentLockerBinding
    private val tapInformation = arrayListOf("저장한 곡", "음악 파일", "저장 앨범")

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

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun getJwt(): String? {
        val sharedPreferences = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences!!.getString("jwt", null)
    }

    private fun initView() {
        val jwt: String? = getJwt()
        if (jwt == null) {
            binding.songLockerLoginTv.text = "로그인"
            binding.songLockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        } else {
            binding.songLockerLoginTv.text = "로그아웃"
            binding.songLockerNameTv.text = jwt.split(',')[1]
            binding.songLockerLoginTv.setOnClickListener {
                logOut()
                activity?.finish()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logOut() {
        val sharedPreferences = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        editor.remove("jwt")
        editor.apply()
    }
}