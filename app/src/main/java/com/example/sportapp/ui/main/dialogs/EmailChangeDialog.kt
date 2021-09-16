package com.example.sportapp.ui.main.dialogs

import android.animation.AnimatorSet
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.databinding.DialogEmailChangingBinding
import com.example.sportapp.ui.main.dialogs.dialogViewModels.EmailChangeDialogViewModel
import render.animations.Bounce
import render.animations.Render
import render.animations.Slide

class EmailChangeDialog : DialogFragment() {

    private lateinit var viewModel: EmailChangeDialogViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogEmailChangingBinding.inflate(inflater,container,false)
        val render1 = Render(requireContext())
        val render2 = Render(requireContext())
        val render3 = Render(requireContext())
        val render4 = Render(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(EmailChangeDialogViewModel::class.java)
        binding.bnDone.setOnClickListener {
            render1.setAnimation(Slide().OutLeft(binding.textPassword))
            render1.setDuration(800)
            render1.start()
            render3.setAnimation(Slide().OutLeft(binding.textInputPassword))
            render3.setDuration(800)
            render3.start()
            render2.setAnimation(Slide().InRight(binding.textEmail))
            render2.setDuration(1200)
            render2.start()
            render4.setAnimation(Slide().InRight(binding.textInputEmail))
            render4.setDuration(1200)
            binding.seekBar.setProgress(100,true)

            render4.start()
            binding.textEmail.isVisible = true
            binding.textInputEmail.isVisible = true

        }
        return binding.root
    }
}