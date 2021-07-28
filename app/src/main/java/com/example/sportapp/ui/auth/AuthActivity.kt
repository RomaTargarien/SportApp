package com.example.sportapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.databinding.ActivityAuthBinding
import com.example.sportapp.other.Resource
import com.example.sportapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        if (FirebaseAuth.getInstance().currentUser != null) {
            enter()
        }
        viewModel.loginStatus.observe(this) {
            if (it is Resource.Success) {
                enter()
            }
        }
        viewModel.registerStatus.observe(this) {
            if (it is Resource.Success) {
                enter()
            }
        }
    }
    fun enter() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}