package com.example.sportapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityAuthBinding
import com.example.sportapp.other.Resource
import com.example.sportapp.ui.auth.viewModels.ForgotPasswordViewModel
import com.example.sportapp.ui.auth.viewModels.LoginViewModel
import com.example.sportapp.ui.auth.viewModels.RegisterViewModel
import com.example.sportapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var viewModelLogin: LoginViewModel
    private lateinit var viewModelRegister: RegisterViewModel
    private lateinit var viewModelForgotPassword: ForgotPasswordViewModel
    private lateinit var router: IAuthRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModelLogin = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModelRegister = ViewModelProvider(this).get(RegisterViewModel::class.java)
        viewModelForgotPassword = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        router = AuthRouter(this,MainActivity())

        if (FirebaseAuth.getInstance().currentUser != null) {
            router.enterActivity()
        }
        viewModelLogin.loginStatus.subscribe({
            if (it is Resource.Success) {
                router.enterActivity()
            }
        },{ })

        viewModelRegister.registerStatus.subscribe({
            if (it is Resource.Success) {
                router.enterActivity()
            }
        },{})

        viewModelLogin.goToForgotPasswordScreen.subscribe({
            Log.d("TAG","click")
            router.goToForgotPasswordScreen()
        },{})
        viewModelLogin.goToRegisterScreen.subscribe({
            Log.d("TAG","click2")
           router.goToRegisterScreen()
        },{})
    }
}