package com.example.sportapp.screen.auth.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentRegisterBinding
import com.example.sportapp.other.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by activityViewModels()
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
        binding.lifecycleOwner = viewLifecycleOwner
        animation = AnimationUtils.loadAnimation(this.requireContext(),R.anim.anim)
        disposes = CompositeDisposable()

        binding.bnRegister.setOnClickListener {
            viewModel.signIn.onNext(Unit)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG","onCreate register")
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































