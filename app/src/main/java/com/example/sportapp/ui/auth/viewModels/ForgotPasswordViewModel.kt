package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.states.LoadingScreenState
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.repositories.auth.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ForgotPasswordViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context,
): ViewModel() {

    val _emailReset = BehaviorSubject.create<String>()
    val emailReset = BehaviorSubject.create<Resource<String>>()

    val resetPasswordButtonEnabled = BehaviorSubject.createDefault(false)

    val buttonResetPassword = PublishSubject.create<Unit>()

    val lottieResult = BehaviorSubject.create<Unit>()
    val forgotPasswordScreenBehavior = BehaviorSubject.create<LoadingScreenState>()

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
            resetPasswordButtonEnabled.onNext(it.second is Resource.Success)
        }

        buttonResetPassword
            .withLatestFrom(emailResetSubject) {_,email -> email.first}
            .doOnNext { forgotPasswordScreenBehavior.onNext(LoadingScreenState.Loading()) }
            .observeOn(Schedulers.io())
            .switchMapSingle {
                repository.restPasswordRx(it)
            }
            .doOnNext {
                lottieResult.onNext(Unit)
                forgotPasswordScreenBehavior.onNext(LoadingScreenState.Success(applicationContext.getString(R.string.go_to_email)))
            }
            .doOnError {
                lottieResult.onNext(Unit)
                forgotPasswordScreenBehavior.onNext(LoadingScreenState.Error(it.localizedMessage ?: ""))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        lottieResult
            .delay(3000,TimeUnit.MILLISECONDS)
            .doOnNext { forgotPasswordScreenBehavior.onNext(LoadingScreenState.Invisible()) }
            .subscribe()
    }
}