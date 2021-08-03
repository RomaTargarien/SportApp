package com.example.sportapp.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentRegisterBinding
import com.example.sportapp.other.Resource
import com.example.sportapp.other.snackbar
import com.example.sportapp.ui.auth.viewModels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(RegisterViewModel::class.java)
        observe()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        binding.bnRegister.setOnClickListener {
            viewModel.registerRx(
                email = binding.edRegisterEmail.text.toString(),
                username = binding.edRegisterUsername.text.toString(),
                password = binding.edRegisterPassword.text.toString(),
                repeatPassword = binding.edRegisterRepeatedPassword.text.toString()
            )
        }
        return binding.root
    }

    fun observe() {
        viewModel.registerStatus.observe(viewLifecycleOwner) {
            it.let { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.registerProgressBar.isVisible = false
                        snackbar(getString(R.string.successfully_register))
                    }
                    is Resource.Error -> {
                        binding.registerProgressBar.isVisible = false
                        result.message?.let { snackbar(it) }
                    }
                    is Resource.Loading -> {
                        binding.registerProgressBar.isVisible = true
                    }
                }
            }
        }
    }
}































