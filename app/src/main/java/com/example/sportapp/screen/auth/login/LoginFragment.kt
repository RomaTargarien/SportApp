package com.example.sportapp.screen.auth.login

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentLoginBinding
import com.example.sportapp.other.progressScreen
import com.example.sportapp.other.textInputBehavior
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var disposes: CompositeDisposable
    private lateinit var emialDispose: CompositeDisposable
    private lateinit var passwordDispose: CompositeDisposable
    private lateinit var animation: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        disposes = CompositeDisposable()
        animation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.anim)

        binding.bnGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientId))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(requireContext(), options)
            resultLauncher.launch(Intent(signInClient.signInIntent))
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate login")

    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume login")
        viewModel.onLoginFragmnentCreated.onNext(true)

        //editTextLoginEmail
        emialDispose = binding.textInputLoginEmail.textInputBehavior(
            subjectInput = viewModel._loginEmail,
            subjectOutput = viewModel.loginEmail
        )

        //editTextLoginPassword
        passwordDispose = binding.textInputLoginPassword.textInputBehavior(
            subjectInput = viewModel._loginPassword,
            subjectOutput = viewModel.loginPassword
        )

        //buttonLogIn
        disposes.add(viewModel.loginButtonEnabled.subscribe({
            binding.bnLogIn.alpha = if (it) 1f else 0.7f
            binding.bnLogIn.isEnabled = it
        }, {}))

        //progressScreen
        disposes.add(
            viewModel.loginScreenBehavior.observeOn(AndroidSchedulers.mainThread()).subscribe({
                this.progressScreen(binding.progresScreen, it, animation, binding.loginScreenLayout)
            }, {})
        )
    }

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = it.data
                Log.d("TAG", it.data.toString())
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(intent).result
                    Log.d("TAG", "google ${account?.id}")
                    account?.let {
                        viewModel.loginWithGoogle(it)
                    }
                } catch (e: Exception) {
                    Log.d("TAG", e.message.toString())
                }
            }
            if (it.resultCode == Activity.RESULT_CANCELED) {
                Log.d("TAG", it.data?.data.toString())
            }
        })

    override fun onPause() {
        super.onPause()
        viewModel.onLoginFragmnentCreated.onNext(false)
        emialDispose.clear()
        passwordDispose.clear()
        disposes.clear()
    }
}

















