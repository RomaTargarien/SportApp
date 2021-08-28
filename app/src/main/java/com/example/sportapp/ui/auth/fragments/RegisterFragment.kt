package com.example.sportapp.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentRegisterBinding
import com.example.sportapp.other.*
import com.example.sportapp.ui.auth.viewModels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var animation: Animation
    private lateinit var disposes: CompositeDisposable
    private lateinit var emailDispose: CompositeDisposable
    private lateinit var passwordDispose: CompositeDisposable
    private lateinit var userNameDispose: CompositeDisposable
    private lateinit var repeatedPasswordDispose: CompositeDisposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(RegisterViewModel::class.java)
        animation = AnimationUtils.loadAnimation(this.requireContext(),R.anim.anim)
        disposes = CompositeDisposable()

        binding.bnRegister.setOnClickListener {
            viewModel.signIn.onNext(Unit)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //editTextRegisterEmail
        emailDispose = binding.textInputRegisterEmail.textInputBehavior(
            subjectInput = viewModel._registerEmail,
            subjectOutput = viewModel.registerEmail
        )

        //editTextRegisterUsername
        userNameDispose = binding.textInputRegisterUsername.textInputBehavior(
            subjectInput = viewModel._registerUserName,
            subjectOutput = viewModel.registerUserName
        )

        //editTextRegisterPassword
        passwordDispose = binding.textInputRegisterPassword.textInputBehavior(
            subjectInput = viewModel._registerPassword,
            subjectOutput = viewModel.registerPassword
        )

        //editTextRegisterRepeatedPassword
        repeatedPasswordDispose = binding.textInputRegisterRepeatPassword.textInputBehavior(
            subjectInput = viewModel._registerRepeatPassword,
            subjectOutput = viewModel.registerRepeatPassword
        )

        //buttonRegister
        disposes.add(viewModel.buttonSignInEnabled.subscribe( {
            binding.bnRegister.alpha = if (it) 1f else 0.7f
            binding.bnRegister.isEnabled = it
        },{}))

        //progressScreen
        disposes.add(viewModel.registerScreenBehavior.observeOn(AndroidSchedulers.mainThread()).subscribe({
            this.progressScreen(binding.progresScreen,it,animation,binding.registerScreen,240)
        },{}))
    }

    override fun onPause() {
        super.onPause()
        disposes.clear()
        emailDispose.clear()
        userNameDispose.clear()
        passwordDispose.clear()
        repeatedPasswordDispose.clear()
    }
}































