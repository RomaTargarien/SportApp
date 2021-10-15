package com.example.sportapp.screen.auth.register

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.ValidationImpl.Companion.EmailValidation
import com.example.sportapp.other.ValidationImpl.Companion.PasswordValidation
import com.example.sportapp.other.ValidationImpl.Companion.UsernameValidation
import com.example.sportapp.other.states.LoadingScreenState
import com.example.sportapp.other.states.Resource
import com.example.sportapp.repositories.auth.AuthRepository
import com.example.sportapp.screen.Screens
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val router: Router,
    private val emailValidation: EmailValidation,
    private val passwordValidation: PasswordValidation,
    private val usernameValidation: UsernameValidation,
    private val resources: Resources
) : ViewModel() {

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

    val signIn = PublishSubject.create<Unit>()

    val registerScreenBehavior = BehaviorSubject.create<LoadingScreenState>()
    val lottieResult = BehaviorSubject.create<Unit>()

    init {
        val emailSubject = _registerEmail
            .distinctUntilChanged()
            .doOnNext { buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, emailValidation.validate(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        emailSubject.subscribe {
            registerEmail.onNext(it.second)
        }

        val usernameSubject = _registerUserName
            .distinctUntilChanged()
            .doOnNext { buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, usernameValidation.validate(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        usernameSubject.subscribe {
            registerUserName.onNext(it.second)
        }

        val passwordSubject = _registerPassword
            .distinctUntilChanged()
            .doOnNext { buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, passwordValidation.validate(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        passwordSubject.subscribe {
            registerPassword.onNext(it.second)
        }

        val repeatedPasswordSubject = _registerRepeatPassword
            .distinctUntilChanged()
            .doOnNext { buttonSignInEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, passwordValidation.validate(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        repeatedPasswordSubject.subscribe {
            registerRepeatPassword.onNext(it.second)
        }

        Observable.combineLatest(
            emailSubject,
            usernameSubject,
            passwordSubject,
            repeatedPasswordSubject
        ) { email, username, password, repPassword ->
            if (!password.first.equals(repPassword.first)) {
                registerRepeatPassword.onNext(Resource.Error(resources.getString(R.string.error_incorrectly_repeated_password)))
            }
            email.second is Resource.Success &&
                    username.second is Resource.Success &&
                    password.second is Resource.Success &&
                    repPassword.second is Resource.Success &&
                    password.first.equals(repPassword.first)
        }.subscribe({
            buttonSignInEnabled.onNext(it)
        }, {})

        signIn.withLatestFrom(
            emailSubject,
            usernameSubject,
            passwordSubject
        ) { _, email, userName, password ->
            Triple(email.first, userName.first, password.first)
        }
            .doOnNext { registerScreenBehavior.onNext(LoadingScreenState.Loading()) }
            .observeOn(Schedulers.io())
            .switchMapSingle {
                repository.registerRx(it.first, it.second, it.third)
            }
            .map {
                Resource.Success(it)
            }
            .doOnError {
                lottieResult.onNext(Unit)
                registerScreenBehavior.onNext(LoadingScreenState.Error(it.localizedMessage ?: ""))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(registerStatus)

        registerStatus.subscribe({
            if (it is Resource.Success) {
                router.newRootScreen(Screens.mainActivity())
            }
        }, {})

        lottieResult
            .delay(3000, TimeUnit.MILLISECONDS)
            .doOnNext { registerScreenBehavior.onNext(LoadingScreenState.Invisible()) }
            .subscribe()
    }
}