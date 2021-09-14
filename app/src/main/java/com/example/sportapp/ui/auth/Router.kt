package com.example.sportapp.ui.auth

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.sportapp.R
import com.example.sportapp.ui.auth.fragments.LoginFragmentDirections
import com.example.sportapp.ui.main.MainActivity

class AuthRouter(
    val startActivity: AppCompatActivity,
    val activityToOpen: AppCompatActivity
): IAuthRouter {

    override fun goToForgotPasswordScreen() {
        startActivity.findNavController(R.id.navHostFragment).navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    override fun goToRegisterScreen() {
        startActivity.findNavController(R.id.navHostFragment).navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun enterActivity() {
        val intent = Intent(startActivity,activityToOpen::class.java)
        startActivity.startActivity(intent)
        startActivity.finish()
    }

}