package com.example.sportapp.ui.auth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentForgotPasswordBinding
import com.example.sportapp.other.Resource
import com.example.sportapp.other.snackbar
import com.example.sportapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        observe()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater,container,false)
        binding.bnForgotPassword.setOnClickListener {
            viewModel.resetPasswordRx(binding.edForgotPasswordEmail.text.toString())
        }
        return binding.root
    }

    fun observe() {
        viewModel.passwordResetStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {snackbar(it.data ?: "")}
                is Resource.Error -> {snackbar(it.message ?: "")}
            }
        }
    }
}