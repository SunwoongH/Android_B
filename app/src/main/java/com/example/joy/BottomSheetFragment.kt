package com.example.joy

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.joy.adapter.SavedSongRVAdapter
import com.example.joy.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetLayoutBinding

    interface MyItemClickListener {
        fun onRemoveAllSong()
    }

    interface MyItemDismissClickListener {
        fun onDismiss()
    }

    private lateinit var myItemClickListener: MyItemClickListener
    private lateinit var myItemDismissClickListener: MyItemDismissClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener, itemDismissClickListener: MyItemDismissClickListener) {
        myItemClickListener = itemClickListener
        myItemDismissClickListener = itemDismissClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetUnlikeIv.setOnClickListener {
            myItemClickListener.onRemoveAllSong()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        myItemDismissClickListener.onDismiss()
    }
}