package com.example.sportapp.other


import android.content.Context
import android.util.Patterns
import com.example.sportapp.R
import com.example.sportapp.other.Constants.MAX_USERNAME_LENGHT
import com.example.sportapp.other.Constants.MIN_PASSWORD_LENGHT
import com.example.sportapp.other.Constants.MIN_USERNAME_LENGHT
import com.example.sportapp.other.states.Resource

fun String.validateEmail(context: Context): Resource<String> {
    if (this.isEmpty()) {
        return Resource.Error(context.getString(R.string.error_input_empty))
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
        return Resource.Error(context.getString(R.string.error_not_a_valid_email))
    } else {
        return Resource.Success("")
    }
}

fun String.validatePassword(context: Context): Resource<String> {
    if (this.isEmpty()) {
        return Resource.Error(context.getString(R.string.error_input_empty))
    }
    if (this.length < MIN_PASSWORD_LENGHT) {
        return Resource.Error(context.getString(R.string.error_password_too_short, MIN_PASSWORD_LENGHT))
    } else {
        return Resource.Success("")
    }
}

fun String.validateUsername(context: Context): Resource<String> {
    if (this.isEmpty()) {
        return Resource.Error(context.getString(R.string.error_input_empty))
    }
    if (this.length < MIN_USERNAME_LENGHT) {
        return Resource.Error(context.getString(R.string.error_username_too_short, MIN_USERNAME_LENGHT))
    }
    if (this.length > MAX_USERNAME_LENGHT) {
        return Resource.Error(context.getString(R.string.error_username_too_long, MAX_USERNAME_LENGHT))
    } else {
        return Resource.Success("")
    }
}