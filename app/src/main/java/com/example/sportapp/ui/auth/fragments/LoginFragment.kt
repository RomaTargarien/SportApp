package com.example.sportapp.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentLoginBinding
import com.example.sportapp.other.*
import com.example.sportapp.other.Constants.REQUEST_CODE
import com.example.sportapp.ui.auth.viewModels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var subscriptionEmail: Pair<Disposable,Disposable>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
            viewModel._loginStatus.postValue(null)
        }
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            )
        }
        observe()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)


        //editTextLoginEmail
        subscriptionEmail = binding.textInputLoginEmail.textInputBehavior(
            subjectInput = viewModel._loginEmail,
            subjectOutput = viewModel.loginEmail)

        //editTextLoginPassword
        binding.textInputLoginPassword.textInputBehavior(
            subjectInput = viewModel._loginPassword,
            subjectOutput = viewModel.loginPassword
        )
        //buttonLogIn
        viewModel.loginButtonEnabled.subscribe({
            binding.bnLogIn.alpha = if (it) 1f else 0.7f
            binding.bnLogIn.isEnabled = it
        },{})

        binding.bnLogIn.setOnClickListener {
            viewModel.logIn.onNext(true)
        }

        binding.bnGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientId))
                .requestEmail()
                .build()
            val signInClient = GoogleSignIn.getClient(requireContext(),options)
            signInClient.signInIntent.also {
                startActivityForResult(it,REQUEST_CODE)
            }
        }
        return binding.root
    }

    fun observe() {
        viewModel.loginStatus.observe(viewLifecycleOwner) {
            it.let { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.loginProgressBar.isVisible = false
                        snackbar(getString(R.string.successfully_log))
                    }
                    is Resource.Error -> {
                        binding.loginProgressBar.isVisible = false
                        result.message?.let { snackbar(it) }
                    }
                    is Resource.Loading -> {
                        binding.loginProgressBar.isVisible = true
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG",requestCode.toString())
        if (requestCode == REQUEST_CODE) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                Log.d("TAG",account.toString())
                account?.let {
                    viewModel.loginWithGoogle(it)
                }
            } catch (e: Exception) {
                Log.d("TAG",e.message.toString())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        subscriptionEmail.first.dispose()
        subscriptionEmail.second.dispose()
    }
}

















