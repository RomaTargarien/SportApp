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

    val buttonSignIn = BehaviorSubject.createDefault(false)

    val isProgressBarShown = PublishSubject.create<Boolean>()

    val snackBarMessage = BehaviorSubject.create<String>()

    init {
        val emailSubject = _registerEmail
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .map { it.validateEmail(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()
        emailSubject.subscribe(registerEmail)

        val usernameSubject = _registerUserName
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .map { it.validateUsername(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()
        usernameSubject.subscribe(registerUserName)

        val passwordSubject = _registerPassword
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()

        val passwordSubjectValidation = passwordSubject
            .observeOn(Schedulers.computation())
            .map { it.validatePassword(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()

        passwordSubjectValidation.subscribe(registerPassword)

        val repeatedPasswordSubject = _registerRepeatPassword
            .subscribeOn(AndroidSchedulers.mainThread())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()

        val repeatedPasswordSubjectValidaion = repeatedPasswordSubject
            .observeOn(Schedulers.computation())
            .map { it.validatePassword(applicationContext) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()

        repeatedPasswordSubjectValidaion.subscribe(registerRepeatPassword)

        val comparingPasswords = Observable.combineLatest(
            passwordSubject,
            repeatedPasswordSubject
        ) { first, second ->
            first.equals(second)
        }

        Observable.combineLatest(
            emailSubject,
            usernameSubject,
            passwordSubjectValidation,
            repeatedPasswordSubjectValidaion,
            comparingPasswords
        ) { first, second,third, fourth, fifth ->
            first is Resource.Success && // emailSubject
                    second is Resource.Success && // usernameSubject
                    third is Resource.Success && // passwordSubjectValidation
                    fourth is Resource.Success && // repeatedPasswordSubjectValidaion
                    fifth // comparingPasswords
        }.subscribe({
            buttonSignInEnabled.onNext(it)
        },{})

        buttonSignIn.subscribe({
            if (it) {
                registerStatus.onNext(Resource.Loading())
                repository.registerRx(_registerEmail.value, _registerUserName.value, _registerPassword.value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        registerStatus.onNext(Resource.Success(it))
                    }, {
                        registerStatus.onNext(Resource.Error(it.message ?: ""))
                    })
            }
        },{})

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