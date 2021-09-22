package com.example.sportapp.ui.main.viewModels

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.states.LoadingScreenState
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.main.MainApiRepository
import com.example.sportapp.ui.main.viewModels.base.ReauthenticationViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class PasswordChangeViewModel @ViewModelInject constructor(
    mainApiRepository: MainApiRepository,
    private val applicationContext: Context
): ReauthenticationViewModel(mainApiRepository,applicationContext) {

    val _newPassword = BehaviorSubject.create<String>()
    val newPassword = BehaviorSubject.create<Resource<String>>()
    val tvEndEnabled = BehaviorSubject.createDefault(false)

    val passwordChahge = PublishSubject.create<Unit>()

    val snackBarMessage = BehaviorSubject.create<String>()

    val lottieResult = BehaviorSubject.create<Boolean>()

    init {
        val newPasswordSubject = _newPassword
            .distinctUntilChanged()
            .doOnNext {
                tvEndEnabled.onNext(false)
            }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .map {
                Pair(it,it.validatePassword(applicationContext))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        newPasswordSubject.subscribe({
            newPassword.onNext(it.second)
        },{})

        newPasswordSubject.subscribe({
            tvEndEnabled.onNext(it.second is Resource.Success)
        },{})

        passwordChahge
            .withLatestFrom(newPasswordSubject) {_,password -> password.first}
            .observeOn(Schedulers.io())
            .switchMap {
                mainApiRepository.updatePassword(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                lottieResult.onNext(true)
            }
            .doOnError {
            }
            .subscribe()

        lottieResult
            .delay(3000,TimeUnit.MILLISECONDS)
            .doOnNext {
                if (it) {
                    goToUserScreen.onNext("")
                }
            }
            .subscribe()
    }
}