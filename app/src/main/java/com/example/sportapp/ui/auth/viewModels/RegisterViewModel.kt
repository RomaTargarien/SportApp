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
import java.util.*
import java.util.concurrent.TimeUnit

class RegisterViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<AuthResult>>()
    val registerStatus: LiveData<Resource<AuthResult>> = _registerStatus

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
                _registerStatus.postValue(Resource.Loading())
                repository.registerRx(_registerEmail.value, _registerUserName.value, _registerPassword.value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _registerStatus.postValue(Resource.Success(it))
                    }, {
                        _registerStatus.postValue(Resource.Error(it.message ?: ""))
                    })
            }
        },{})
    }
}