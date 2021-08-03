package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import android.util.Patterns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.Constants
import com.example.sportapp.other.Resource
import com.example.sportapp.repositories.AuthRepository
import com.google.firebase.auth.AuthResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class RegisterViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<AuthResult>>()
    val registerStatus: LiveData<Resource<AuthResult>> = _registerStatus

    fun registerRx(email: String,username: String,password: String, repeatPassword: String) {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != repeatPassword) {
            applicationContext.getString(R.string.error_incorrectly_repeated_password)
        } else if (username.length < Constants.MIN_USERNAME_LENGHT) {
            applicationContext.getString(
                R.string.error_username_too_short,
                Constants.MIN_USERNAME_LENGHT
            )
        } else if (username.length > Constants.MAX_USERNAME_LENGHT) {
            applicationContext.getString(
                R.string.error_username_too_long,
                Constants.MAX_USERNAME_LENGHT
            )
        } else if (password.length < Constants.MIN_PASSWORD_LENGHT) {
            applicationContext.getString(
                R.string.error_password_too_short,
                Constants.MIN_PASSWORD_LENGHT
            )
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        }
        else null

        error?.let {
            _registerStatus.postValue(Resource.Error(it))
            return
        }
        _registerStatus.postValue(Resource.Loading())
        repository.registerRx(email, username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _registerStatus.postValue(Resource.Success(it))
            }, {
                _registerStatus.postValue(Resource.Error(it.message ?: ""))
            })
    }
}