package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.repositories.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class ForgotPasswordViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

    val passwordResetStatus = BehaviorSubject.create<Resource<String>>()

    val _emailReset = BehaviorSubject.create<String>()
    val emailReset = BehaviorSubject.create<Resource<String>>()

    val resetPasswordButtonEnabled = BehaviorSubject.createDefault(false)

    val buttonResetPassword = BehaviorSubject.createDefault(false)

    val snackBarMessage = BehaviorSubject.create<String>()

    init {
        val emailResetSubject = _emailReset
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .map { it.validateEmail(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()
        emailResetSubject.subscribe(emailReset)

        emailResetSubject.subscribe { resetPasswordButtonEnabled.onNext(it is Resource.Success) }

        buttonResetPassword.subscribe({
            if (it) {
                repository.restPasswordRx(_emailReset.value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        passwordResetStatus.onNext(Resource.Success("Password reset link was sent to your email"))
                        Log.d("TAG","Success")
                    }, {
                        Log.d("TAG","Error")
                        passwordResetStatus.onNext(Resource.Error(it.message ?: ""))
                    })
            }
        },{})

        passwordResetStatus.subscribe({
            Log.d("TAG",it.toString())
            when (it) {
                is Resource.Success -> {snackBarMessage.onNext(it.message)}
                is Resource.Error -> {snackBarMessage.onNext(it.message)}
            }
        },{})


    }
}