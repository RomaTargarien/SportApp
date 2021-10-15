package com.example.sportapp.screen.auth.forgot_password

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.ValidationImpl.Companion.EmailValidation
import com.example.sportapp.other.states.LoadingScreenState
import com.example.sportapp.other.states.Resource
import com.example.sportapp.repositories.auth.AuthRepository
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val resources: Resources,
    private val router: Router,
    private val emailValidation: EmailValidation
) : ViewModel() {

    val _emailReset = BehaviorSubject.create<String>()
    val emailReset = PublishSubject.create<Resource<String>>()

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
            .map {
                Pair(it, emailValidation.validate(it))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        emailResetSubject.subscribe({
            emailReset.onNext(it.second)
        }, {})

        emailResetSubject.subscribe {
            resetPasswordButtonEnabled.onNext(it.second is Resource.Success)
        }

        buttonResetPassword
            .withLatestFrom(emailResetSubject) { _, email -> email.first }
            .doOnNext { forgotPasswordScreenBehavior.onNext(LoadingScreenState.Loading()) }
            .observeOn(Schedulers.io())
            .switchMapSingle {
                repository.restPasswordRx(it)
            }
            .doOnNext {
                lottieResult.onNext(Unit)
                forgotPasswordScreenBehavior.onNext(LoadingScreenState.Success(resources.getString(R.string.go_to_email)))
            }
            .doOnError {
                lottieResult.onNext(Unit)
                forgotPasswordScreenBehavior.onNext(
                    LoadingScreenState.Error(
                        it.localizedMessage ?: ""
                    )
                )
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        lottieResult
            .delay(3000, TimeUnit.MILLISECONDS)
            .doOnNext { forgotPasswordScreenBehavior.onNext(LoadingScreenState.Invisible()) }
            .subscribe()
    }

    fun onBackClick(): Boolean {
        router.exit()
        return true
    }
}