package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.*
import com.example.sportapp.repositories.AuthRepository
import com.google.firebase.auth.AuthResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class RegisterViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

    val registerStatus = BehaviorSubject.create<Resource<AuthResult>>()

    val _registerEmail = BehaviorSubject.create<String>()
    val registerEmail = BehaviorSubject.create<Resource<String>>()

    val _registerUserName = BehaviorSubject.create<String>()
    val registerUserName = BehaviorSubject.create<Resource<String>>()

    val _registerPassword = BehaviorSubject.create<String>()
    val registerPassword = BehaviorSubject.create<Resource<String>>()

    val _registerRepeatPassword = BehaviorSubject.create<String>()
    val registerRepeatPassword = BehaviorSubject.create<Resource<String>>()

    val buttonSignInEnabled = BehaviorSubject.createDefault(false)

    val signIn = BehaviorSubject.create<Unit>()

    val isProgressBarShown = PublishSubject.create<Boolean>()

    val snackBarMessage = BehaviorSubject.create<String>()

    init {
        val emailSubject = _registerEmail
            .distinctUntilChanged()
            .doOnNext{ buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it,it.validateEmail(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        emailSubject.subscribe {
            registerEmail.onNext(it.second)
        }

        val usernameSubject = _registerUserName
            .distinctUntilChanged()
            .doOnNext{ buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it,it.validateUsername(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        usernameSubject.subscribe {
            registerUserName.onNext(it.second)
        }

        val passwordSubject = _registerPassword
            .distinctUntilChanged()
            .doOnNext{ buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it,it.validatePassword(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        passwordSubject.subscribe {
            registerPassword.onNext(it.second)
        }

        val repeatedPasswordSubject = _registerRepeatPassword
            .distinctUntilChanged()
            .doOnNext{ buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it,it.validatePassword(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

       repeatedPasswordSubject.subscribe{
           registerRepeatPassword.onNext(it.second)
       }

        Observable.combineLatest(
            emailSubject,
            usernameSubject,
            passwordSubject,
            repeatedPasswordSubject
        ) { email, username,password, repPassword->
            if (!password.first.equals(repPassword.first)) {
                registerRepeatPassword.onNext(Resource.Error(applicationContext.getString(R.string.error_incorrectly_repeated_password)))
            }
                    email.second is Resource.Success &&
                    username.second is Resource.Success &&
                    password.second is Resource.Success &&
                    repPassword.second is Resource.Success &&
                    password.first.equals(repPassword.first)
        }.subscribe({
            buttonSignInEnabled.onNext(it)
        },{})

        signIn.withLatestFrom(emailSubject,usernameSubject,passwordSubject) {_,email,userName,password ->
            Triple(email.first,userName.first,password.first)
        }
            .distinctUntilChanged()
            .observeOn(Schedulers.io())
            .flatMapSingle {
                repository.registerRx(it.first,it.second,it.third)
        }
            .map {
                Resource.Success(it)
            }
            .doOnError {
                registerStatus.onNext(Resource.Error(it.localizedMessage ?: ""))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(registerStatus)


        registerStatus.subscribe({
            when (it) {
                is Resource.Success -> {isProgressBarShown.onNext(false)}
                is Resource.Error -> {isProgressBarShown.onNext(false)}
                is Resource.Loading -> {isProgressBarShown.onNext(true)}
            }
        },{})
        registerStatus.subscribe({
            when (it) {
                is Resource.Success -> {snackBarMessage.onNext(applicationContext.getString(R.string.successfully_log))}
                is Resource.Error -> {snackBarMessage.onNext(it.message ?: "")}
            }
        },{})
    }
}