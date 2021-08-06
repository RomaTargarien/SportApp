package com.example.sportapp.ui.auth.viewModels

import android.content.Context
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
    private val _passwordResetStatus = MutableLiveData<Resource<String>>()
    val passwordResetStatus: LiveData<Resource<String>> = _passwordResetStatus

    val _emailReset = BehaviorSubject.create<String>()
    val emailReset = BehaviorSubject.create<Resource<String>>()

    val resetPasswordButtonEnabled = BehaviorSubject.createDefault(false)

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
    }

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