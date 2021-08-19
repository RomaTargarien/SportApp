package com.example.sportapp.ui.auth.viewModels

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.sportapp.R
import com.example.sportapp.other.Resource
import com.example.sportapp.other.observe
import com.example.sportapp.other.validateEmail
import com.example.sportapp.other.validatePassword
import com.example.sportapp.repositories.AuthRepository
import com.example.sportapp.ui.auth.fragments.LoginFragment
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract




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

    val goToRegisterScreen = PublishSubject.create<Unit>()
    val goToForgotPasswordScreen = PublishSubject.create<Unit>()

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
            .progressBarBehavior(isProgressBarShown)
            .switchMapSingle {
                Log.d("TAG",Thread.currentThread().toString())
                repository.loginRx(it.first,it.second)
            }
            .map {
                Resource.Success(it)
            }
            .doOnError {
                loginStatus.onNext(Resource.Error(it.localizedMessage ?: ""))
            }
            .retry()
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

    fun <T> Observable<T>.progressBarBehavior(
        progressBar: BehaviorSubject<Boolean>
    ): Observable<T> {
        return this.doOnNext {
            progressBar.onNext(true)
        }.doAfterNext {
            progressBar.onNext(false)
        }
    }
}