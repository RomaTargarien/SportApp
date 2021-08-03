package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.Resource
import com.example.sportapp.repositories.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ForgotPasswordViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {
    private val _passwordResetStatus = MutableLiveData<Resource<String>>()
    val passwordResetStatus: LiveData<Resource<String>> = _passwordResetStatus

    fun resetPasswordRx(email: String) {
        if (!email.isEmpty()) {
            repository.restPasswordRx(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _passwordResetStatus.postValue(Resource.Success("Password reset link was sent to your email"))
                }, {
                    _passwordResetStatus.postValue(Resource.Error(it.message ?: ""))
                })
        } else {
            _passwordResetStatus.postValue(Resource.Error(applicationContext.getString(R.string.error_input_empty)))
        }
    }
}