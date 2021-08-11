package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.sportapp.R
import com.example.sportapp.other.Resource
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.AuthRepository
import com.example.sportapp.ui.auth.Router
import com.example.sportapp.ui.auth.fragments.LoginFragment
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context
): ViewModel() {

    val loginStatus = BehaviorSubject.create<Resource<AuthResult>>()

    val _loginPassword = BehaviorSubject.create<String>()
    val loginPassword = BehaviorSubject.create<Resource<String>>()

    val loginButtonEnabled = BehaviorSubject.createDefault(false)

    val isProgressBarShown = BehaviorSubject.create<Boolean>()

    val snackBarMessage = PublishSubject.create<String>()

    val _loginEmail = BehaviorSubject.create<String>()
    val loginEmail = BehaviorSubject.create<Resource<String>>()

    val logIn = PublishSubject.create<Unit>()

    init {
       val validatedEmailPair = _loginEmail
            .distinctUntilChanged()
            .doOnNext { loginButtonEnabled.onNext(false) }
            .debounce(1000,TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { Pair(it, it.validateEmail(applicationContext)) }
            .observeOn(AndroidSchedulers.mainThread())
            .share()

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
            .share()

        validatedPasswordPair.subscribe({ pair->
            loginPassword.onNext(pair.second)
        },{})

        Observable.combineLatest(validatedEmailPair,validatedPasswordPair, { emailpair, passwordPair ->
            emailpair.second is Resource.Success && passwordPair.second is Resource.Success
        }).subscribe({
            loginButtonEnabled.onNext(it)
        },{},{})

        loginStatus.subscribe({
            when (it) {
                is Resource.Success -> {isProgressBarShown.onNext(false)}
                is Resource.Error -> {isProgressBarShown.onNext(false)}
                is Resource.Loading -> {isProgressBarShown.onNext(true)}
            }
        },{})
        loginStatus.subscribe({
            when (it) {
                is Resource.Success -> {snackBarMessage.onNext(applicationContext.getString(R.string.successfully_log))}
                is Resource.Error -> {snackBarMessage.onNext(it.message ?: "")}
            }
        },{})

        logIn.withLatestFrom(validatedEmailPair,validatedPasswordPair, { _,emailPair,passwordPair ->
            Pair(emailPair.first,passwordPair.first)
        })
            .observeOn(Schedulers.io())
            .flatMap {
                repository.loginRx(it.first, it.second).toObservable()
                .map<Resource<AuthResult>> {
                    Resource.Success(it)
                }
                .onErrorReturn {
                    Resource.Error(it.localizedMessage ?: "")
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginStatus)
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