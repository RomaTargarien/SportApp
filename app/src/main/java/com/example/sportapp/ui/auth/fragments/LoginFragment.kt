package com.example.sportapp.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentLoginBinding
import com.example.sportapp.other.Constants.REQUEST_CODE
import com.example.sportapp.other.Resource
import com.example.sportapp.other.snackbar
import com.example.sportapp.ui.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
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
        val observable = Observable.create { emitter: ObservableEmitter<Any?> ->
            val watcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged( charSequence: CharSequence, i: Int, i1: Int, i2: Int) { }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }

                override fun afterTextChanged(editable: Editable) {
                    if (!emitter.isDisposed) { //если еще не отписались
                        emitter.onNext(editable.toString()) //отправляем текущее состояние
                    }
                }
            }
            emitter.setCancellable { binding.edLoginEmail.removeTextChangedListener(watcher) } //удаляем листенер при отписке от observable
            binding.edLoginEmail.addTextChangedListener(watcher)
        }
        observable
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TAG",it.toString())
            }, {

            },{

            })

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
//        viewModel.loginObsevrer
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe (
//                {
//            it.data.
//        })
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
}

















