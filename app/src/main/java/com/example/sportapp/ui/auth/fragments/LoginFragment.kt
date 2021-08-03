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
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
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

        binding.bnLogIn.setOnClickListener {
            viewModel.loginRX(
                email = binding.edLoginEmail.text.toString(),
                password = binding.edLoginPassword.text.toString()
            )
        }


        binding.textInputLoginEmail.observe().subscribe(viewModel.loginEmail)
        viewModel.loginEmail.subscribe(binding.textInputLoginEmail.observer())

//           .subscribeOn(AndroidSchedulers.mainThread())
//           .debounce(1000,TimeUnit.MILLISECONDS)
//           .observeOn(Schedulers.computation())
//           .map {  checkEmail(it) }
//           .observeOn(AndroidSchedulers.mainThread())
//           .subscribe({
//               binding.textInputLoginEmail.enableError(it)
//                      }, { },{ })




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
        if (requestCode == REQUEST_CODE) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                viewModel.loginWithGoogle(it)
            }
        }
    }


    private fun result(string: String): Resource<String> {
        if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            return Resource.Error(getString(R.string.error_not_a_valid_email))
        } else {
            return Resource.Success("")
        }
    }
}

















