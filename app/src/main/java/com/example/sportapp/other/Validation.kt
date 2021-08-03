package com.example.sportapp.other


import android.content.Context
import android.util.Patterns
import androidx.fragment.app.Fragment
import com.example.sportapp.R
import com.example.sportapp.ui.auth.fragments.LoginFragment
import dagger.hilt.android.qualifiers.ApplicationContext

fun checkEmail(string: String): Resource<String> {
    if (string.isEmpty()) {
        return Resource.Error("")
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
        return Resource.Error("Please enter a valid E-Mail")
    } else {
        return Resource.Success("")
    }
}