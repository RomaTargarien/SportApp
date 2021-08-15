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
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ForgotPasswordViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context,
): ViewModel() {

    val passwordResetStatus = BehaviorSubject.create<Resource<String>>()

    val _emailReset = BehaviorSubject.create<String>()
    val emailReset = BehaviorSubject.create<Resource<String>>()

    val resetPasswordButtonEnabled = BehaviorSubject.createDefault(false)

    val buttonResetPassword = PublishSubject.create<Unit>()

    val snackBarMessage = BehaviorSubject.create<String>()

    init {
        val emailResetSubject = _emailReset
            .distinctUntilChanged()
            .doOnNext { resetPasswordButtonEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it,it.validateEmail(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        emailResetSubject.subscribe({
            emailReset.onNext(it.second)
        },{})

        emailResetSubject.subscribe {
            Log.d("TAG",it.first)
            resetPasswordButtonEnabled.onNext(it.second is Resource.Success)
        }

        buttonResetPassword
            .withLatestFrom(emailResetSubject) {_,email -> email.first}
            .observeOn(Schedulers.io())
            .flatMapSingle {
                repository.restPasswordRx(it)
            }
            .map {
                Resource.Success(applicationContext.getString(R.string.go_to_email))
            }
            .doOnError {
                passwordResetStatus.onNext(Resource.Error(it.localizedMessage ?: ""))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(passwordResetStatus)

        passwordResetStatus.subscribe({
            when (it) {
                is Resource.Success -> {snackBarMessage.onNext(it.data)}
                is Resource.Error -> {snackBarMessage.onNext(it.message)}
            }
        },{})
    }
}