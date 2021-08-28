package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.sportapp.other.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit





class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

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
    val loginScreenBehavior = BehaviorSubject.create<Resource<String>>()

    init {
       val validatedEmailPair = _loginEmail
            .distinctUntilChanged()
            .doOnNext { loginButtonEnabled.onNext(false) }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, it.validateEmail(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        validatedEmailPair.subscribe({ pair ->
            loginEmail.onNext(pair.second)
        },{})

        val validatedPasswordPair = _loginPassword
            .distinctUntilChanged()
            .doOnNext { loginButtonEnabled.onNext(false) }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it,it.validatePassword(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .cacheWithInitialCapacity(1)

        validatedPasswordPair.subscribe({ pair->
            loginPassword.onNext(pair.second)
        },{})

        Observable.combineLatest(validatedEmailPair,validatedPasswordPair, { emailpair, passwordPair ->
            emailpair.second is Resource.Success && passwordPair.second is Resource.Success
        }).subscribe({
            loginButtonEnabled.onNext(it)
        },{},{})

        logIn.withLatestFrom(validatedEmailPair,validatedPasswordPair, { _,emailPair,passwordPair ->
            Pair(emailPair.first,passwordPair.first)
        })
            .doOnNext { loginScreenBehavior.onNext(Resource.Loading()) }
            .observeOn(Schedulers.io())
            .switchMapSingle {
                repository.loginRx(it.first,it.second)
            }
            .map {
                Resource.Success(it)
            }
            .doOnError {
                lottieResult.onNext(Unit)
                loginScreenBehavior.onNext(Resource.Error(it.localizedMessage ?: ""))
            }
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginStatus)

        lottieResult
            .delay(3000,TimeUnit.MILLISECONDS)
            .doOnNext { loginScreenBehavior.onNext(Resource.Success("")) }
            .subscribe()
    }


    fun loginWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        repository.loginWithGoogle(credentials)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loginStatus.onNext(Resource.Success(it))
            },{
                loginStatus.onNext(Resource.Error("Log in failed"))
            })
    }
}