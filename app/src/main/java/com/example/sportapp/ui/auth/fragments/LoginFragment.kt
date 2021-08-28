package com.example.sportapp.ui.auth.fragments

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentLoginBinding
import com.example.sportapp.other.*
import com.example.sportapp.other.ext.forEachChildView
import com.example.sportapp.ui.auth.viewModels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var disposes: CompositeDisposable
    private lateinit var emialDispose: CompositeDisposable
    private lateinit var passwordDispose: CompositeDisposable
    private lateinit var animation: Animation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setOnClickListener {
            viewModel.goToRegisterScreen.onNext(Unit)
        }
        binding.tvForgotPassword.setOnClickListener {
            viewModel.goToForgotPasswordScreen.onNext(Unit)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        disposes = CompositeDisposable()
        animation = AnimationUtils.loadAnimation(this.requireContext(),R.anim.anim)

        binding.bnLogIn.setOnClickListener {
            viewModel.logIn.onNext(Unit)
        }

        binding.bnGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientId))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(requireContext(),options)
            resultLauncher.launch(Intent(signInClient.signInIntent))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //editTextLoginEmail
        emialDispose = binding.textInputLoginEmail.textInputBehavior(
            subjectInput = viewModel._loginEmail,
            subjectOutput = viewModel.loginEmail)

        //editTextLoginPassword
        passwordDispose = binding.textInputLoginPassword.textInputBehavior(
            subjectInput = viewModel._loginPassword,
            subjectOutput = viewModel.loginPassword)

        //buttonLogIn
        disposes.add(viewModel.loginButtonEnabled.subscribe({
            binding.bnLogIn.alpha = if (it) 1f else 0.7f
            binding.bnLogIn.isEnabled = it
        },{}))

        //progressScreen
        disposes.add(viewModel.loginScreenBehavior.observeOn(AndroidSchedulers.mainThread()).subscribe({
            this.progressScreen(binding.progresScreen,it,animation,binding.loginScreenLayout)
        },{}))
    }

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = it.data
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(intent).result
                    account?.let {
                        viewModel.loginWithGoogle(it)
                    }
                } catch (e: Exception) {
                    Log.d("TAG",e.message.toString())
                }
            }
        })

    override fun onPause() {
        super.onPause()
        emialDispose.clear()
        passwordDispose.clear()
        disposes.clear()
    }
}

















