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
import com.example.sportapp.other.*
import com.example.sportapp.ui.auth.viewModels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(RegisterViewModel::class.java)

        binding.textInputRegisterEmail.textInputBehavior(
            subjectInput = viewModel._registerEmail,
            subjectOutput = viewModel.registerEmail
        )
        binding.textInputRegisterUsername.textInputBehavior(
            subjectInput = viewModel._registerUserName,
            subjectOutput = viewModel.registerUserName
        )
        binding.textInputRegisterPassword.textInputBehavior(
            subjectInput = viewModel._registerPassword,
            subjectOutput = viewModel.registerPassword
        )
        binding.textInputRegisterRepeatPassword.textInputBehavior(
            subjectInput = viewModel._registerRepeatPassword,
            subjectOutput = viewModel.registerRepeatPassword
        )

        viewModel.buttonSignInEnabled.subscribe( {
            binding.bnRegister.alpha = if (it) 1f else 0.7f
            binding.bnRegister.isEnabled = it
        },{},{})

        binding.bnRegister.setOnClickListener {
            viewModel.signIn.onNext(Unit)
        }

        viewModel.isProgressBarShown.subscribe({
            binding.registerProgressBar.isVisible = it
        },{})

        viewModel.snackBarMessage.subscribe({
            snackbar(it)
        },{})

        return binding.root
    }
}































