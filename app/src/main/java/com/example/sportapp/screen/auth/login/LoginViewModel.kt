package com.example.sportapp.screen.auth.login

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.example.sportapp.R
import com.example.sportapp.other.ValidationImpl.Companion.EmailValidation
import com.example.sportapp.other.ValidationImpl.Companion.PasswordValidation
import com.example.sportapp.other.states.LoadingScreenState
import com.example.sportapp.other.states.Resource
import com.example.sportapp.repositories.auth.AuthRepository
import com.example.sportapp.screen.Screens
import com.github.terrakok.cicerone.Router
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val router: Router,
    private val emailValidation: EmailValidation,
    private val passwordValidation: PasswordValidation,
    private val resources: Resources
) : ViewModel() {

    val loginStatus = PublishSubject.create<Resource<AuthResult>>()

    val _loginPassword = BehaviorSubject.create<String>()
    val loginPassword = BehaviorSubject.create<Resource<String>>()

    val loginButtonEnabled = BehaviorSubject.createDefault(false)

    val _loginEmail = BehaviorSubject.create<String>()
    val loginEmail = BehaviorSubject.create<Resource<String>>()

    val logIn = PublishSubject.create<Unit>()

    val goToRegisterScreen = PublishSubject.create<Unit>()
    val goToForgotPasswordScreen = PublishSubject.create<Unit>()

    val lottieResult = BehaviorSubject.create<Unit>()
    val loginScreenBehavior = BehaviorSubject.create<LoadingScreenState>()

    val onLoginFragmnentCreated = BehaviorSubject.createDefault(false)

    init {
        goToRegisterScreen.subscribe({
            router.navigateTo(Screens.registerScreen())
        }, {})

        goToForgotPasswordScreen.subscribe({
            router.navigateTo(Screens.forgotPasswordScreen())
        }, {})

        val validationdEmailPair = _loginEmail
            .distinctUntilChanged()
            .doOnNext { loginButtonEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, emailValidation.validate(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        validationdEmailPair.subscribe({ pair ->
            loginEmail.onNext(pair.second)
        }, {})

        val validationdPasswordPair = _loginPassword
            .distinctUntilChanged()
            .doOnNext { loginButtonEnabled.onNext(false) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, passwordValidation.validate(it)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        validationdPasswordPair.subscribe({ pair ->
            loginPassword.onNext(pair.second)
        }, {})

        Observable.combineLatest(
            validationdEmailPair,
            validationdPasswordPair,
            { emailpair, passwordPair ->
                emailpair.second is Resource.Success && passwordPair.second is Resource.Success
            }).subscribe({
            loginButtonEnabled.onNext(it)
        }, {}, {})

        logIn.withLatestFrom(
            validationdEmailPair,
            validationdPasswordPair,
            { _, emailPair, passwordPair ->
                Pair(emailPair.first, passwordPair.first)
            })
            .doOnNext { loginScreenBehavior.onNext(LoadingScreenState.Loading()) }
            .observeOn(Schedulers.io())
            .switchMapSingle {
                repository.loginRx(it.first, it.second)
            }
            .map {
                Resource.Success(it)
            }
            .doOnError {
                lottieResult.onNext(Unit)
                loginScreenBehavior.onNext(LoadingScreenState.Error(it.localizedMessage ?: ""))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginStatus)

        lottieResult
            .delay(3000, TimeUnit.MILLISECONDS)
            .doOnNext { loginScreenBehavior.onNext(LoadingScreenState.Invisible()) }
            .subscribe()

        loginStatus.subscribe({
            if (it is Resource.Success) {
                router.newRootScreen(Screens.mainActivity())
            }
        }, {})
    }


    fun loginWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        repository.loginWithGoogle(credentials)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loginStatus.onNext(Resource.Success(it))
            }, {
                loginStatus.onNext(Resource.Error(resources.getString(R.string.log_in_failed)))
            })
    }

    fun logIn() {
        logIn.onNext(Unit)
    }

    fun goToRegisterScreen() {
        goToRegisterScreen.onNext(Unit)
    }

    fun goToForgotPasswordScreen() {
        goToForgotPasswordScreen.onNext(Unit)
    }
}