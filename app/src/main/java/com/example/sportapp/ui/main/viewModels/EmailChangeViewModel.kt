package com.example.sportapp.ui.main.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.main.MainApiRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class EmailChangeViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository,
    private val applicationContext: Context
): ViewModel() {

    val reauthenticate = PublishSubject.create<Unit>()
    val reauthenticationState = PublishSubject.create<Resource<String>>()

    val emailChahge = PublishSubject.create<Unit>()
    val emailChangingState = PublishSubject.create<Resource<String>>()

    val _password = BehaviorSubject.create<String>()
    val password = BehaviorSubject.create<Resource<String>>()
    val tvNextEnabled = BehaviorSubject.createDefault(false)

    val _newEmail = BehaviorSubject.create<String>()
    val newEmail = BehaviorSubject.create<Resource<String>>()
    val tvEndEnabled = BehaviorSubject.createDefault(false)

    val isProgressBarVisible = BehaviorSubject.createDefault(false)

    val goToLoginScreen = PublishSubject.create<Unit>()

    init {

        val passwordSubject = _password
            .distinctUntilChanged()
            .doOnNext {
                tvNextEnabled.onNext(false)
            }
            .debounce(500,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                Pair(it,it.validatePassword(applicationContext))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        passwordSubject.subscribe({
            password.onNext(it.second)
        },{})

        passwordSubject.subscribe({
            tvNextEnabled.onNext(it.second is Resource.Success)
        },{})

        reauthenticate
            .withLatestFrom(passwordSubject) {_,password -> password.first}
            .doOnNext {
                isProgressBarVisible.onNext(true)
            }
            .observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.reauthenticate(it)
            }
            .doOnError {
                isProgressBarVisible.onNext(false)
                reauthenticationState.onNext(Resource.Error(it.localizedMessage))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                isProgressBarVisible.onNext(false)
                reauthenticationState.onNext(Resource.Success(""))
            },{})

        val newEmailSubject = _newEmail
            .distinctUntilChanged()
            .doOnNext {
                tvEndEnabled.onNext(false)
            }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                Pair(it,it.validateEmail(applicationContext))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        newEmailSubject.subscribe({
           newEmail.onNext(it.second)
        },{})

        newEmailSubject.subscribe({
            tvEndEnabled.onNext(it.second is Resource.Success)
        },{})

        emailChahge
            .withLatestFrom(newEmailSubject) {_,email -> email.first}
            .doOnNext {
                isProgressBarVisible.onNext(true)
            }
            .observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.updateEmail(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                isProgressBarVisible.onNext(false)
                emailChangingState.onNext(Resource.Error(it.localizedMessage))
            }
            .retry()
            .subscribe({
                isProgressBarVisible.onNext(false)
                emailChangingState.onNext(Resource.Success(""))
            },{})
    }
}