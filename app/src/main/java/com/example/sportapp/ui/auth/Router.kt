package com.example.sportapp.ui.auth

import androidx.navigation.NavController
import com.example.sportapp.R
import com.example.sportapp.ui.auth.fragments.LoginFragmentDirections
import com.example.sportapp.ui.main.MainActivity

class Router(val navigator: NavController) {


    fun goToForgotPasswordScreen() {
        navigator.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
    }
}